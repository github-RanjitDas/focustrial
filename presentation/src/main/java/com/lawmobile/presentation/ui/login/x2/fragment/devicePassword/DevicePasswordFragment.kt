package com.lawmobile.presentation.ui.login.x2.fragment.devicePassword

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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.customEvents.WrongCredentialsEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentStartPairingX2Binding
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.security.IIsolatedService
import com.lawmobile.presentation.security.IsolatedService
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.PairingViewModel
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.x1.fragment.StartPairingFragment
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class DevicePasswordFragment : BaseFragment(), Instructions, StartPairing {
    private var _binding: FragmentStartPairingX2Binding? = null
    private val binding get() = _binding!!

    private val pairingViewModel: PairingViewModel by viewModels()
    private val viewModel: DevicePasswordViewModel by activityViewModels()

    private lateinit var serviceBinder: IIsolatedService
    private var isServiceBounded = false

    override var onInstructionsClick: (() -> Unit)? = null
    override var onStartPairingClick: (() -> Unit)? = null
    private lateinit var onEditOfficerId: (String) -> Unit

    var officerId: String = ""

    private var officerPassword: String
        get() = viewModel.officerPassword
        set(value) {
            viewModel.officerPassword = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartPairingX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setListeners()
        pairingViewModel.setObservers()
        pairingViewModel.cleanCacheFiles()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.officerId.isEmpty()) viewModel.officerId = officerId
        binding.setOfficerId()
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

    private fun FragmentStartPairingX2Binding.setOfficerId() {
        editTextOfficerId.setText(viewModel.officerId)
    }

    private fun FragmentStartPairingX2Binding.setListeners() {
        editTextDevicePasswordListener()
        buttonConnectListener()
        instructionsToLinkListener()
        buttonEditOfficerIdListener()
    }

    private fun FragmentStartPairingX2Binding.editTextDevicePasswordListener() {
        editTextDevicePassword.setText(officerPassword)
        editTextDevicePassword.addTextChangedListener {
            officerPassword = it.toString()
            buttonConnect.isEnabled = it.toString().isNotEmpty()
            buttonConnect.isActivated = it.toString().isNotEmpty()
        }
    }

    private fun FragmentStartPairingX2Binding.buttonEditOfficerIdListener() {
        buttonEditOfficerId.setOnClickListener {
            editTextDevicePassword.setText("")
            onEditOfficerId(viewModel.officerId)
        }
    }

    private fun FragmentStartPairingX2Binding.instructionsToLinkListener() {
        textViewInstructionsToLinkCamera.setOnClickListener { onInstructionsClick?.invoke() }
    }

    private fun FragmentStartPairingX2Binding.buttonConnectListener() {
        buttonConnect.isEnabled = false
        buttonConnect.isActivated = false
        buttonConnect.setOnClickListener {
            (activity as AppCompatActivity).hideKeyboard()
            if (!verifyMagiskInPhone()) verifyPermissionsToStartPairing()
        }
    }

    private fun PairingViewModel.setObservers() {
        isConnectionPossible.observe(viewLifecycleOwner, ::manageIsPossibleConnection)
    }

    private fun verifyPermissionsToStartPairing() {
        if (arePermissionsGranted()) {
            if (isGPSActive()) startPairingProcess() else showAlertToGPSEnable()
        } else showAlertToNavigateToPermissions()
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

    private fun startPairingProcess() {
        if (!pairingViewModel.isWifiEnable()) {
            createAlertToNavigateWifiSettings()
        } else binding.suggestBodyCameraNetwork()
    }

    private fun FragmentStartPairingX2Binding.suggestBodyCameraNetwork() {
        val networkName = editTextOfficerId.text.toString()
        val passwordName = editTextDevicePassword.text.toString()

        pairingViewModel.suggestWiFiNetwork(networkName, passwordName) { isConnected ->
            if (isConnected) onStartPairingClick?.invoke()
            else showWrongCredentialsNotification()
        }
    }

    private fun showWrongCredentialsNotification() {
        val cameraEvent = WrongCredentialsEvent.event
        context?.createNotificationDialog(cameraEvent)
            ?.setButtonText(resources.getString(R.string.OK))
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
            doIfSuccess { onStartPairingClick?.invoke() }
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
        } else false
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
        val TAG: String = StartPairingFragment::class.java.simpleName
        fun createInstance(onEditOfficerId: (String) -> Unit): DevicePasswordFragment {
            return DevicePasswordFragment().apply {
                this.onEditOfficerId = onEditOfficerId
            }
        }
    }
}
