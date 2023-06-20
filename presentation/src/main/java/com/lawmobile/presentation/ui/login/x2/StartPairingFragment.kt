package com.lawmobile.presentation.ui.login.x2

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentStartPairingBinding
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.isGPSActive
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.security.IIsolatedService
import com.lawmobile.presentation.security.IsolatedService
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.PairingViewModel
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class StartPairingFragment : BaseFragment(), Instructions, StartPairing {

    private var _binding: FragmentStartPairingBinding? = null
    private val binding get() = _binding!!

    private val pairingViewModel: PairingViewModel by viewModels()

    private lateinit var serviceBinder: IIsolatedService
    private var isServiceBounded = false

    override var onInstructionsClick: (() -> Unit)? = null
    override var onStartPairingClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartPairingBinding.inflate(inflater, container, false)
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

    private fun FragmentStartPairingBinding.setListeners() {
        buttonGoListener()
        buttonInstructionsListener()
    }


    private fun FragmentStartPairingBinding.buttonInstructionsListener() {
        buttonInstructionsToLinkCamera.setOnClickListener {
            onInstructionsClick?.invoke()
        }
    }

    private fun FragmentStartPairingBinding.buttonGoListener() {
        buttonGo.setOnClickListener {
            if (!verifyMagiskInPhone()) {
                verifyPermissionsToStartPairing()
            }
        }
    }


    private fun PairingViewModel.setObservers() {
        isConnectionPossible.observe(viewLifecycleOwner, ::manageIsPossibleConnection)
    }

    private fun verifyPermissionsToStartPairing() {
        if (isGPSActive(requireContext())) startPairing() else showAlertToGPSEnable()
    }

    private fun startPairing() {
        if (!pairingViewModel.isWifiEnable()) {
            createAlertToNavigateWifiSettings()
            return
        }
        val serialNumberCamera = pairingViewModel.getNetworkName()
        if (!CameraType.isValidBodyCameraNumber(serialNumberCamera)) {
            pairingViewModel.isConnectionPossible()
            return
        }
        onStartPairingClick?.invoke()
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
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_HOTSPOT_CONNECTION_ERRORS,
                    it,
                    "Unable to Connect to Socket"
                )
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

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = StartPairingFragment::class.java.simpleName
    }
}
