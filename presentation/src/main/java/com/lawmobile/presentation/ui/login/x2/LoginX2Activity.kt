package com.lawmobile.presentation.ui.login.x2

import android.content.Intent
import android.content.RestrictionsManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.entities.customEvents.LoginRequestErrorEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.ui.login.x2.fragment.devicePassword.DevicePasswordFragment
import com.lawmobile.presentation.ui.login.x2.fragment.officerId.OfficerIdFragment
import com.lawmobile.presentation.ui.sso.SSOActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

class LoginX2Activity : LoginBaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private val viewModel: LoginX2ViewModel by viewModels()

    private lateinit var authRequest: AuthorizationRequest
    private val officerIdFragment = OfficerIdFragment.createInstance(::onContinueClick)
    private val devicePasswordFragment = DevicePasswordFragment.createInstance(::onEditOfficerId)

    override val instructions: Instructions get() = devicePasswordFragment
    override val startPairing: StartPairing get() = devicePasswordFragment

    var officerId: String
        get() = viewModel.officerId
        set(value) {
            viewModel.officerId = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = viewModel
        setCollectors()
        viewModel.setObservers()
        restoreBottomSheetState()
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
                showOfficerIdFragment()
                verifyLocationPermission()
            }
            onDevicePassword {
                showDevicePasswordFragment()
                setInstructionsListener()
                setStartPairingListener()
            }
        }
    }

    private fun handleAuthEndpointsResult(result: Result<AuthorizationEndpoints>) {
        with(result) {
            doIfSuccess { viewModel.getAuthorizationRequest(it) }
            doIfError { showRequestError() }
        }
    }

    private fun handleAuthRequestResult(result: Result<AuthorizationRequest>) {
        with(result) {
            doIfSuccess { goToSsoLogin(it) }
            doIfError { showRequestError() }
        }
    }

    private fun handleDevicePasswordResult(result: Result<String>) {
        with(result) {
            doIfSuccess {
                val hotspotName = "X" + officerId.substringBefore("@")
                val hotspotPassword = it.substring(0..14)
                viewModel.suggestWiFiNetwork(hotspotName, hotspotPassword) { isConnected ->
                    state = if (isConnected) LoginState.PairingResult
                    else LoginState.X2.DevicePassword
                }
                waitToEnableContinue()
            }
            doIfError {
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
        result.doIfSuccess { startLiveViewActivity() }
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

    private fun onContinueClick(isEmail: Boolean, officerId: String) {
        this.officerId = officerId
        if (isEmail) runOnUiThread {
            showLoadingDialog()
            viewModel.getAuthorizationEndpoints()
        }
        else state = LoginState.X2.DevicePassword
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RestrictionsManager.RESULT_ERROR_INTERNAL) {
            state = LoginState.X2.DevicePassword
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

    private fun getAuthorizationException(data: Intent?) =
        AuthorizationException.fromIntent(data)

    private fun buildAuthorizationResponse(data: Intent) =
        AuthorizationResponse.Builder(authRequest).fromUri(data.data!!).build()

    private fun onTokenResponse(response: Result<TokenResponse>) {
        with(response) {
            doIfSuccess {
                viewModel.saveToken(it.accessToken.toString())
                viewModel.getDevicePassword(officerId)
            }
            doIfError {
                showRequestError()
            }
        }
    }

    private fun goToSsoLogin(authRequest: AuthorizationRequest) {
        this.authRequest = authRequest
        val intent = Intent(baseContext, SSOActivity::class.java)
        startActivityForResult(intent, 100)
        hideLoadingDialog()
    }

    companion object {
        private const val ENABLE_CONTINUE_DELAY = 1000L
    }
}
