package com.lawmobile.presentation.ui.login.x2

import android.content.Intent
import android.content.RestrictionsManager
import android.os.Bundle
import androidx.activity.viewModels
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.entities.customEvents.LoginRequestErrorEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.x2.fragment.devicePassword.DevicePasswordFragment
import com.lawmobile.presentation.ui.login.x2.fragment.devicePassword.DevicePasswordFragmentListener
import com.lawmobile.presentation.ui.login.x2.fragment.officerId.OfficerIdFragment
import com.lawmobile.presentation.ui.sso.SSOActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

class LoginX2Activity : LoginBaseActivity(), DevicePasswordFragmentListener {

    private val viewModel: LoginX2ViewModel by viewModels()
    override var officerId: String = ""
    private lateinit var authRequest: AuthorizationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLoginViews()
        viewModel.setObservers()
    }

    private fun LoginX2ViewModel.setObservers() {
        authEndpointsResult.observe(this@LoginX2Activity, ::handleAuthEndpointsResult)
        authRequestResult.observe(this@LoginX2Activity, ::handleAuthRequestResult)
        devicePasswordResult.observe(this@LoginX2Activity, ::handleDevicePasswordResult)
        userFromCameraResult.observe(this@LoginX2Activity, ::handleUserResult)
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
        hideLoadingDialog()
    }

    private fun handleDevicePasswordResult(result: Result<String>) {
        with(result) {
            doIfSuccess {
                hideLoadingDialog()
                val hotspotName = "X" + officerId.substringBefore("@")
                val hotspotPassword = it.substring(0..14)
                viewModel.suggestWiFiNetwork(hotspotName, hotspotPassword) { isConnected ->
                    if (isConnected) showPairingResultFragment()
                    else showDevicePasswordFragment()
                }
            }
            doIfError {
                showRequestError()
            }
        }
    }

    private fun showRequestError() {
        hideLoadingDialog()
        val errorEvent = LoginRequestErrorEvent.event
        createNotificationDialog(errorEvent) {
            setButtonText(resources.getString(R.string.OK))
            onConfirmationClick = ::showDevicePasswordFragment
        }
    }

    private fun handleUserResult(result: Result<User>) {
        with(result) {
            doIfSuccess {
                CameraInfo.officerName = it.name ?: ""
                CameraInfo.officerId = it.id ?: ""
                startLiveViewActivity()
            }
            doIfError {
                showUserInformationError()
            }
        }
    }

    override fun getUserFromCamera() = viewModel.getUserFromCamera()

    private fun setLoginViews() {
        showValidateOfficerIdFragment()
        verifyLocationPermission()
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = Intent(this, LiveX2Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showValidateOfficerIdFragment(officerId: String = "") {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = OfficerIdFragment.createInstance(::onContinueClick, officerId),
            tag = OfficerIdFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showDevicePasswordFragment() {
        val devicePasswordFragment = DevicePasswordFragment.createInstance(this)
        val fragmentTag = DevicePasswordFragment.TAG

        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = devicePasswordFragment,
            tag = fragmentTag,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    override fun onValidRequirements() = showPairingResultFragment()

    override fun onEditOfficerId(officerId: String) = showValidateOfficerIdFragment(officerId)

    private fun onContinueClick(isEmail: Boolean, officerId: String) {
        this.officerId = officerId
        if (isEmail) runOnUiThread {
            showLoadingDialog()
            viewModel.getAuthorizationEndpoints()
        }
        else showDevicePasswordFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RestrictionsManager.RESULT_ERROR_INTERNAL) {
            showDevicePasswordFragment()
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
    }

    override fun onConnectionSuccessful() = getUserFromCamera()
}
