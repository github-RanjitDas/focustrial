package com.lawmobile.presentation.ui.login.x2.fragment.devicePassword

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentStartPairingX2Binding
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.isGPSActive
import com.lawmobile.presentation.extensions.showConnectionErrorNotification
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showIncorrectPasswordErrorNotification
import com.lawmobile.presentation.extensions.showLimitOfLoginAttemptsErrorNotification
import com.lawmobile.presentation.security.IIsolatedService
import com.lawmobile.presentation.security.IsolatedService
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.PairingViewModel
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.ui.login.x2.StartPairingFragment
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import org.json.JSONObject

class DevicePasswordFragment : BaseFragment(), Instructions, StartPairing {
    var inputPassword: String = ""
    var passwordFromCamera: String = ""
    private var mLastClickTime: Long = 0
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
        viewModel.updatePasswordVerificationProgress.observe(
            requireActivity(), ::handlePasswordVerificationResult
        )
        pairingViewModel.setObservers()
        pairingViewModel.cleanCacheFiles()
    }

    private fun handlePasswordVerificationResult(result: Result<String>?) {
        result?.doIfSuccess {
            val jsonObject = JSONObject(it)
            val isPasswordCorrect = jsonObject.getBoolean("isSuccess")
            if (isPasswordCorrect) {
                val hotspotPassword = jsonObject.getString("hotspotPassword")
                binding.suggestBodyCameraNetwork(hotspotPassword.takeLast(16))
            } else {
                hideLoadingDialog()
                if (incorrectPasswordRetryAttempt >= MAX_INCORRECT_PASSWORD_ATTEMPT) {
                    requireContext().showLimitOfLoginAttemptsErrorNotification(activity as LoginBaseActivity)
                } else {
                    incorrectPasswordRetryAttempt++
                    requireContext().showIncorrectPasswordErrorNotification()
                }
            }
        }
        result?.doIfError {
            hideLoadingDialog()
            SFConsoleLogs.log(
                SFConsoleLogs.Level.ERROR,
                SFConsoleLogs.Tags.TAG_BLUETOOTH_CONNECTION_ERRORS,
                it,
                getString(R.string.error_getting_config_bluetooth)
            )
            binding.layoutStartPairing.showErrorSnackBar(getString(R.string.error_getting_config_bluetooth))
        }
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
            intent, mIsolatedServiceConnection, AppCompatActivity.BIND_AUTO_CREATE
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
            viewModel.officerId = ""
        }
    }

    private fun FragmentStartPairingX2Binding.instructionsToLinkListener() {
        textViewInstructionsToLinkCamera.setOnClickListener { onInstructionsClick?.invoke() }
    }

    private fun FragmentStartPairingX2Binding.buttonConnectListener() {
        buttonConnect.isEnabled = false
        buttonConnect.isActivated = false
        buttonConnect.setOnClickListener {
            showLoadingDialog()
            (activity as AppCompatActivity).hideKeyboard()
            if (!verifyMagiskInPhone()) verifyPermissionsToStartPairing()
        }
    }

    private fun PairingViewModel.setObservers() {
        isConnectionPossible.observe(viewLifecycleOwner, ::manageIsPossibleConnection)
    }

    private fun verifyPermissionsToStartPairing() {
        if (isGPSActive(requireContext())) startPairingProcess() else showAlertToGPSEnable()
    }

    private fun startPairingProcess() {
        if (!pairingViewModel.isWifiEnable()) {
            hideLoadingDialog()
            createAlertToNavigateWifiSettings()
        } else {
            if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) {
                // Nexus
                if (CameraInfo.wifiApRouterMode == 1) {
                    hideLoadingDialog()
                    // Wifi AP Router 1
                    initPasswordVerification()
                } else {
                    // Wifi AP Router 0
                    val hotspotPassword = binding.editTextDevicePassword.text.toString()
                    binding.suggestBodyCameraNetwork(
                        hotspotPassword,
                        "X" + CameraInfo.deviceIdFromConfig
                    )
                }
            } else {
                // CC
                inputPassword = binding.editTextDevicePassword.text.toString()
                if (viewModel.isCameraConnected()) {
                    Log.d(LoginX2Activity.TAG_CC_PWD_VALIDATION, "Camera Connected Already")
                    if (passwordFromCamera.isNotEmpty()) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return
                        } else {
                            mLastClickTime = SystemClock.elapsedRealtime()
                            (activity as LoginX2Activity).validateOfficerPassword(passwordFromCamera)
                            hideLoadingDialog()
                        }
                    } else {
                        (activity as LoginX2Activity).getUserNPasswordFromCamera()
                        hideLoadingDialog()
                    }
                } else {
                    Log.d(LoginX2Activity.TAG_CC_PWD_VALIDATION, "Camera Not Connected")
                    val hotspotPasswordDefault = CameraInfo.defaultPasswordForCC
                    binding.suggestBodyCameraNetwork(
                        hotspotPasswordDefault, "X" + CameraInfo.deviceIdFromConfig
                    )
                }
            }
        }
    }

    private fun initPasswordVerification() {
        showLoadingDialog()
        CameraInfo.officerId = binding.editTextOfficerId.text.toString()
        viewModel.verifyPasswordFromBle(
            requireContext(), binding.editTextDevicePassword.text.toString()
        )
    }

    private fun FragmentStartPairingX2Binding.suggestBodyCameraNetwork(
        hotspotPassword: String,
        hotspotSSID: String = "X" + editTextOfficerId.text.toString()
    ) {
        Log.d(TAG, "Connect with Wifi Name:$hotspotSSID")
        Log.d(TAG, "Connect with Wifi Password:$hotspotPassword")
        val handler = Handler(Looper.getMainLooper())
        pairingViewModel.suggestWiFiNetwork(
            handler,
            hotspotSSID, hotspotPassword
        ) { isConnected ->
            hideLoadingDialog()
            if (isConnected) {
                onStartPairingClick?.invoke()
            } else {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_HOTSPOT_CONNECTION_ERRORS,
                    message = "Error Connecting with Wifi : $hotspotSSID"
                )
                if (activity != null) {
                    if (!requireActivity().isFinishing && !requireActivity().isDestroyed) {
                        requireContext().showConnectionErrorNotification()
                    }
                }
            }
        }
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
                    SFConsoleLogs.Tags.TAG_SOCKET_CONNECTION_ERRORS,
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

    override val viewTag: String
        get() = TAG

    companion object {
        var incorrectPasswordRetryAttempt = 1
        const val MAX_INCORRECT_PASSWORD_ATTEMPT = 5
        val TAG: String = StartPairingFragment::class.java.simpleName
        fun createInstance(onEditOfficerId: (String) -> Unit): DevicePasswordFragment {
            return DevicePasswordFragment().apply {
                this.onEditOfficerId = onEditOfficerId
            }
        }
    }
}
