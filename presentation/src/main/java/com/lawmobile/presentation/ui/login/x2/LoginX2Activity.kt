package com.lawmobile.presentation.ui.login.x2

import android.content.Intent
import android.content.RestrictionsManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.entities.customEvents.BluetoothErrorEvent
import com.lawmobile.domain.entities.customEvents.LoginRequestErrorEvent
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.extensions.isGPSActive
import com.lawmobile.presentation.extensions.showIncorrectPasswordErrorNotification
import com.lawmobile.presentation.extensions.showLimitOfLoginAttemptsErrorNotification
import com.lawmobile.presentation.keystore.KeystoreHandler
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.ui.login.x2.fragment.devicePassword.DevicePasswordFragment
import com.lawmobile.presentation.ui.login.x2.fragment.officerId.OfficerIdFragment
import com.lawmobile.presentation.ui.sso.SSOActivity
import com.lawmobile.presentation.utils.EncodePassword
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.json.JSONObject

class LoginX2Activity : LoginBaseActivity() {

    private var snackBarObject: SafeFleetSnackBar? = null
    override val parentTag: String
        get() = this::class.java.simpleName

    private val viewModel: LoginX2ViewModel by viewModels()
    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var authRequest: AuthorizationRequest
    private val officerIdFragment = OfficerIdFragment.createInstance(::onContinueClick)
    private val devicePasswordFragment = DevicePasswordFragment.createInstance(::onEditOfficerId)

    override val instructions: Instructions get() = devicePasswordFragment
    override val startPairing: StartPairing get() = devicePasswordFragment

    private var mLastClickTime: Long = 0

    var officerId: String
        get() = viewModel.officerId
        set(value) {
            viewModel.officerId = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = viewModel
        viewModel.updateConfigProgress.observe(this@LoginX2Activity, ::handleConfigResult)
        updateInstructionText(true)
        restoreBottomSheetState()
        setCollectors()
        viewModel.setObservers()
    }

    fun getUserNPasswordFromCamera() {
        viewModel.getUserFromCamera()
    }

    private fun logBluetoothError(exception: Exception) {
        SFConsoleLogs.log(
            SFConsoleLogs.Level.ERROR,
            SFConsoleLogs.Tags.TAG_BLUETOOTH_CONNECTION_ERRORS,
            exception,
            getString(R.string.error_getting_config_bluetooth)
        )
    }

    private fun handleConfigResult(result: Result<String>?) {
        with(result) {
            this?.doIfSuccess { onReceivedConfigFromBle() }
            this?.doIfError {
                logBluetoothError(it)
                Log.d(TAG, "Check for saved configs...")
                val configs = KeystoreHandler.getConfigFromKeystore(this@LoginX2Activity)
                if (configs == null) {
                    // Flow Blocked
                    showError()
                } else {
                    Log.d(TAG, "configs not null:$configs")
                    val jsonObject = JSONObject(configs)
                    var savedOfficerId = jsonObject.getString("deviceId")
                    if (CameraInfo.cameraType == CameraType.X2) {
                        savedOfficerId = "x2-$savedOfficerId"
                    }
                    Log.d(TAG, "savedOfficerId:$savedOfficerId,officerId$officerId")

                    if (officerId == savedOfficerId) {
                        Log.d(TAG, "Found Saved Configs from KeyStore : $configs")
                        viewModel.saveConfigLocally(configs)
                        onReceivedConfigFromBle()
                    } else {
                        // Flow Blocked
                        showError()
                    }
                }
            }
            hideLoadingDialog()
        }
    }

    private fun showError() {
        snackBarObject = showErrorSnackBar(
            R.string.error_getting_config_bluetooth, ::retryBleConnectionToFetchConfigs
        )
    }

    private fun retryBleConnectionToFetchConfigs() {
        initBleConnectionToFetchConfigs()
    }

    private fun initBleConnectionToFetchConfigs() {
        if (viewModel.verifyBluetoothEnabled()) {
            showLoadingDialog()
            viewModel.fetchConfigFromBle(this)
        } else {
            officerIdFragment.setButtonContinueEnable(true)
            showBluetoothOffDialog(::initBleConnectionToFetchConfigs)
        }
    }

    private fun showBluetoothOffDialog(callback: () -> Unit) {
        runOnUiThread {
            val event = BluetoothErrorEvent.event
            createNotificationDialog(event, true) {
                setButtonText(resources.getString(R.string.OK))
                onOkButtonClick = {
                    officerIdFragment.setButtonContinueEnable(false)
                    viewModel.enableBluetooth()
                    handler.postDelayed(
                        {
                            callback.invoke()
                        },
                        WAIT_TO_ENABLE_BLUETOOTH_DELAY

                    )
                }
            }
        }
    }

    private fun onReceivedConfigFromBle() {
        Log.d(TAG, "Successfully Received Configs from BLE...")
        hideLoadingDialog()
        viewModel.verifyInternetConnection {
            if (it) {
                when (CameraInfo.backOfficeType) {
                    BackOfficeType.NEXUS -> {
                        // Nexus
                        when (CameraInfo.wifiApRouterMode) {
                            1 -> runOnUiThread {
                                showLoadingDialog()
                                viewModel.getAuthorizationEndpoints()
                            }

                            else -> {
                                state = LoginState.X2.DevicePassword
                            }
                        }
                    }

                    else -> {
                        // Not Nexus
                        state = LoginState.X2.DevicePassword
                    }
                }
            } else {
                // No Internet
                state = LoginState.X2.DevicePassword
            }
        }
    }

    private fun LoginX2ViewModel.setObservers() {
        authEndpointsResult.observe(this@LoginX2Activity, ::handleAuthEndpointsResult)
        authRequestResult.observe(this@LoginX2Activity, ::handleAuthRequestResult)
        devicePasswordResult.observe(this@LoginX2Activity, ::handleDevicePasswordResult)
        userFromCameraResult.observe(this@LoginX2Activity, ::handleUserResult)
    }

    override fun handleLoginState(loginState: LoginState) {
        super.handleLoginState(loginState)
        with(loginState) {
            onOfficerId {
                startRequestPermission()
            }
            onDevicePassword {
                showDevicePasswordFragment()
                setInstructionsListener()
                setStartPairingListener()
            }
        }
    }

    private fun startRequestPermission() {
        verifyPermissions()
    }

    private fun handleAuthEndpointsResult(result: Result<AuthorizationEndpoints>) {
        with(result) {
            doIfSuccess {
                Log.d(TAG, "Successfully Received EndPoints for SSO Login.")
                viewModel.getAuthorizationRequest(it)
            }
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_END_POINTS_ERRORS,
                    it,
                    "Error in end points result"
                )
                showRequestError()
            }
        }
    }

    private fun handleAuthRequestResult(result: Result<AuthorizationRequest>) {
        with(result) {
            doIfSuccess {
                Log.d(TAG, "Successfully Received Auth Request for SSO Login.. Now Go to SSO Login...")
                goToSsoLogin(it)
            }
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_END_POINTS_ERRORS,
                    it,
                    "Error in Authentication"
                )
                showRequestError()
            }
        }
    }

    private fun handleDevicePasswordResult(result: Result<String>) {
        with(result) {
            doIfSuccess {
                Log.d(TAG, "SSO Completed with Success:$result")
                val hotspotName = "X$officerId"
                Log.d(TAG, "Now Connect with Wifi :Name: $hotspotName")
                val hotspotPassword = it.takeLast(16)
                Log.d(TAG, "Password: $it ")
                val handler = Handler(Looper.getMainLooper())
                viewModel.suggestWiFiNetwork(handler, hotspotName, hotspotPassword) { isConnected ->
                    if (isConnected) {
                        waitToEnableContinue()
                        state = LoginState.PairingResult
                    } else {
                        SFConsoleLogs.log(
                            SFConsoleLogs.Level.ERROR,
                            SFConsoleLogs.Tags.TAG_HOTSPOT_CONNECTION_ERRORS,
                            message = "Error Connecting with Wifi : $hotspotName"
                        )
                        waitToEnableContinue()
                        showRequestError()
                    }
                }
            }
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_END_POINTS_ERRORS,
                    it,
                    "SSO Error"
                )
                Log.d(TAG, "SSO Completed with Error:$it")
                showRequestError()
            }
        }
    }

    private fun waitToEnableContinue() {
        lifecycleScope.launch {
            delay(ENABLE_CONTINUE_DELAY)
            officerIdFragment.setButtonContinueEnable(true)
            hideLoadingDialog()
        }
    }

    private fun showRequestError() {
        hideLoadingDialog()
        val errorEvent = LoginRequestErrorEvent.event
        createNotificationDialog(errorEvent) {
            setButtonText(resources.getString(R.string.OK))
            onConfirmationClick = {
                state = LoginState.X2.DevicePassword
            }
        }
        officerIdFragment.setButtonContinueEnable(true)
    }

    override fun handleUserResult(result: Result<User>) {
        super.handleUserResult(result)
        hideLoadingDialog()
        if (CameraInfo.isBackOfficeCC()) {
            // CC
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return
            } else {
                mLastClickTime = SystemClock.elapsedRealtime()
                result.doIfSuccess {
                    val passwordFromCamera = it.password ?: ""
                    devicePasswordFragment.passwordFromCamera = passwordFromCamera
                    validateOfficerPassword(passwordFromCamera)
                }
            }
        } else {
            // Nexus
            startLiveViewActivity()
        }
    }

    private fun isCorrectPassword(passwordFromCamera: String): Boolean {
        val inputPassword = devicePasswordFragment.inputPassword
        val sha256Password = EncodePassword.encodePasswordOfficer(inputPassword)
        Log.d(
            TAG_CC_PWD_VALIDATION,
            "input password:$inputPassword,sha256Password:$sha256Password,passwordFromCamera:$passwordFromCamera"
        )
        return sha256Password.isNotEmpty() && sha256Password == passwordFromCamera
    }

    fun validateOfficerPassword(passwordFromCamera: String) {
        Log.d(TAG_CC_PWD_VALIDATION, "validateOfficerPassword:$passwordFromCamera")
        if (passwordFromCamera.isNotEmpty() && isCorrectPassword(passwordFromCamera)) {
            isOnlyShowSuccessAnimation = true
            state = LoginState.PairingResult
            handler.postDelayed(
                {
                    handleConnectionAnimationListener?.onShowSuccessAnimation()
                },
                1000
            )
        } else {
            state = LoginState.X2.DevicePassword
            showUserPasswordError()
        }
    }

    private fun showUserPasswordError() {
        Log.d(
            TAG_CC_PWD_VALIDATION,
            "showUserPasswordError:" + DevicePasswordFragment.incorrectPasswordRetryAttempt
        )
        if (DevicePasswordFragment.incorrectPasswordRetryAttempt >= DevicePasswordFragment.MAX_INCORRECT_PASSWORD_ATTEMPT) {
            this.showLimitOfLoginAttemptsErrorNotification(this)
        } else {
            DevicePasswordFragment.incorrectPasswordRetryAttempt++
            this.showIncorrectPasswordErrorNotification()
        }
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = Intent(this, LiveX2Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showOfficerIdFragment() {
        officerIdFragment.officerId = officerId
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = officerIdFragment,
            tag = OfficerIdFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showDevicePasswordFragment() {
        devicePasswordFragment.officerId = officerId
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = devicePasswordFragment,
            tag = DevicePasswordFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun onEditOfficerId(officerId: String) {
        this.officerId = officerId
        state = LoginState.X2.OfficerId
    }

    private fun fetchConfigsFromBle() {
        runOnUiThread {
            initBleConnectionToFetchConfigs()
        }
    }

    private fun onContinueClick(officerId: String) {
        if (officerId == "demo_account") {
            this.officerId = officerId
            CameraInfo.officerId = officerId
            CameraInfo.demoMode = true
            startLiveViewActivity()
            return
        } else {
            CameraInfo.demoMode = false
        }
        if (snackBarObject != null && snackBarObject!!.isShown) {
            snackBarObject!!.dismiss()
        }
        if (isGPSActive(this)) {
            this.officerId = officerId
            CameraInfo.officerId = officerId
            fetchConfigsFromBle()
        } else {
            officerIdFragment.setButtonContinueEnable(true)
            showAlertToGPSEnable()
        }
    }

    private fun showAlertToGPSEnable() {
        val alertInformation = AlertInformation(
            R.string.gps_necessary_title,
            R.string.gps_necessary_description,
            { dialogInterface -> dialogInterface.cancel() },
            null
        )
        createAlertInformation(alertInformation)
    }

    override fun onPermissionsGranted() {
        showOfficerIdFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RestrictionsManager.RESULT_ERROR_INTERNAL || resultCode == RestrictionsManager.RESULT_ERROR) {
            showRequestError()
        }

        if (!viewModel.isUserAuthorized() && data != null) {
            val response = buildAuthorizationResponse(data)
            val exception = getAuthorizationException(data)

            when {
                response.authorizationCode != null -> {
                    showLoadingDialog()
                    viewModel.authenticateToGetToken(response, ::onTokenResponse)
                }

                exception != null -> {
                    showRequestError()
                }
            }
        } else {
            officerIdFragment.setButtonContinueEnable(true)
        }
    }

    private fun getAuthorizationException(data: Intent?) = AuthorizationException.fromIntent(data)

    private fun buildAuthorizationResponse(data: Intent) =
        AuthorizationResponse.Builder(authRequest).fromUri(data.data!!).build()

    private fun onTokenResponse(response: Result<TokenResponse>) {
        with(response) {
            doIfSuccess {
                Log.d(TAG, "onTokenResponse:Success:$it")
                viewModel.saveToken(it.accessToken.toString())
                handler.postDelayed(
                    {
                        viewModel.getDevicePassword(CameraInfo.userId)
                    },
                    1000
                )
            }
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_END_POINTS_ERRORS,
                    it,
                    "Token Error"
                )
                Log.d(TAG, "onTokenResponse:Error:$it")
                showRequestError()
            }
        }
    }

    private fun goToSsoLogin(authRequest: AuthorizationRequest) {
        Log.d(
            "SSOLogin",
            "Move to SSO Page: " +
                CameraInfo.officerId + "," + CameraInfo.discoveryUrl + "," + CameraInfo.tenantId
        )
        this.authRequest = authRequest
        val intent = Intent(baseContext, SSOActivity::class.java)
        startActivityForResult(intent, 100)
        hideLoadingDialog()
    }

    companion object {
        const val TAG_CC_PWD_VALIDATION = "CC_PWD_VALIDATION"
        private const val ENABLE_CONTINUE_DELAY = 1000L
        private const val WAIT_TO_ENABLE_BLUETOOTH_DELAY = 1000L
        private const val TAG = "LoginX2Activity"
    }
}
