package com.lawmobile.presentation.ui.login.x1.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentStartPairingX1Binding
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.security.IIsolatedService
import com.lawmobile.presentation.security.IsolatedService
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.shared.PairingViewModel
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.lawmobile.presentation.ui.selectCamera.SelectCameraActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class StartPairingFragment : BaseFragment() {

    private var _binding: FragmentStartPairingX1Binding? = null
    private val binding get() = _binding!!

    private val pairingViewModel: PairingViewModel by viewModels()
    lateinit var onValidRequirements: () -> Unit

    private lateinit var serviceBinder: IIsolatedService
    private var isServiceBounded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartPairingX1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairingViewModel.setObservers()
        binding.setListeners()
        pairingViewModel.cleanCacheFiles()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(activity, IsolatedService::class.java)
        context?.bindService(
            intent,
            mIsolatedServiceConnection,
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }

    private fun FragmentStartPairingX1Binding.setListeners() {
        buttonGoListener()
        buttonInstructionsListener()
        changeCameraListener()
    }

    private fun FragmentStartPairingX1Binding.changeCameraListener() {
        buttonChangeCamera.setOnClickListener { goToSelectCamera() }
    }

    private fun FragmentStartPairingX1Binding.buttonInstructionsListener() {
        buttonInstructionsToLinkCamera.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun FragmentStartPairingX1Binding.buttonGoListener() {
        buttonGo.setOnClickListener {
            if (!verifyMagiskInPhone()) {
                verifyPermissionsToStartPairing()
            }
        }
    }

    private fun goToSelectCamera() {
        val selectCameraIntent = Intent(context, SelectCameraActivity::class.java)
        activity?.startActivity(selectCameraIntent)
        activity?.finish()
    }

    private fun PairingViewModel.setObservers() {
        validateConnectionLiveData.observe(viewLifecycleOwner, ::manageIsPossibleConnection)
    }

    private fun showBottomSheet() {
        (activity as LoginX1Activity).sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun verifyPermissionsToStartPairing() {
        if (arePermissionsGranted()) {
            if (isGPSActive()) startPairingResultFragment() else showAlertToGPSEnable()
        } else {
            showAlertToNavigateToPermissions()
        }
    }

    private fun arePermissionsGranted() =
        (activity as BaseActivity).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun isGPSActive(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnable = false
        try {
            gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return gpsEnable
    }

    private fun startPairingResultFragment() {
        if (!pairingViewModel.isWifiEnable()) {
            createAlertToNavigateWifiSettings()
            return
        }
        val serialNumberCamera = pairingViewModel.getNetworkName()
        if (!CameraType.isValidBodyCameraNumber(serialNumberCamera)) {
            pairingViewModel.isPossibleConnection()
            return
        }
        onValidRequirements()
    }

    private fun showAlertToNavigateToPermissions() {
        val alertInformation = AlertInformation(
            R.string.please_enable_permission,
            R.string.please_enable_permission_location,
            { startIntentToGivePermission() },
            { dialogInterface -> dialogInterface.cancel() }
        )
        activity?.createAlertInformation(alertInformation)
    }

    private fun showAlertToGPSEnable() {
        val alertInformation = AlertInformation(
            R.string.gps_necessary_title,
            R.string.gps_necessary_description,
            { dialogInterface -> dialogInterface.cancel() },
            null
        )
        activity?.createAlertInformation(alertInformation)
    }

    private fun startIntentToGivePermission() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun manageIsPossibleConnection(result: Result<Unit>) {
        with(result) {
            doIfSuccess { onValidRequirements() }
            doIfError {
                binding.layoutStartPairing.showErrorSnackBar(getString(R.string.verify_camera_wifi))
            }
        }
    }

    private fun createAlertToNavigateWifiSettings() {
        val alertInformation =
            AlertInformation(
                R.string.wifi_is_off, R.string.please_turn_wifi_on,
                {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                },
                {
                    it.dismiss()
                }
            )

        activity?.createAlertInformation(alertInformation)
    }

    private fun verifyMagiskInPhone(): Boolean {
        return if (isServiceBounded) {
            return try {
                serviceBinder.isMagiskPresent
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    private val mIsolatedServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            serviceBinder = IIsolatedService.Stub.asInterface(iBinder)
            isServiceBounded = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            isServiceBounded = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = StartPairingFragment::class.java.simpleName
        fun createInstance(onValidRequirements: () -> Unit): StartPairingFragment =
            StartPairingFragment().apply { this.onValidRequirements = onValidRequirements }
    }
}