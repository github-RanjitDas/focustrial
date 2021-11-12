package com.lawmobile.presentation.ui.login

import android.Manifest
import android.content.Intent
import android.content.RestrictionsManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLoginBinding
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.getIntentDependsCameraType
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.extensions.verifyForAskingPermission
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.live.x1.LiveX1Activity
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingResultFragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x1.StartPairingX1Fragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x2.StartPairingX2Fragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x2.StartPairingX2FragmentListener
import com.lawmobile.presentation.ui.login.validateOfficerId.ValidateOfficerIdFragment
import com.lawmobile.presentation.ui.login.validateOfficerPassword.ValidateOfficerPasswordFragment
import com.lawmobile.presentation.ui.login.validateOfficerPassword.ValidateOfficerPasswordFragmentListener
import com.lawmobile.presentation.ui.sso.SSOActivity
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

class LoginActivity :
    BaseActivity(),
    StartPairingX2FragmentListener,
    ValidateOfficerPasswordFragmentListener {

    private val viewModel: LoginActivityViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    override var user: User? = null
    override var officerId: String = ""
    private lateinit var authRequest: AuthorizationRequest

    val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            binding.bottomSheetInstructions.bottomSheetInstructions
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBottomSheet()
        setLoginViews()
        viewModel.setObservers()
    }

    private fun LoginActivityViewModel.setObservers() {
        authEndpointsResult.observe(this@LoginActivity, ::handleAuthEndpointsResult)
        authRequestResult.observe(this@LoginActivity, ::handleAuthRequestResult)
        devicePasswordResult.observe(this@LoginActivity, ::handleDevicePasswordResult)
        userFromCameraResult.observe(this@LoginActivity, ::handleUserResult)
    }

    private fun handleAuthEndpointsResult(result: Result<AuthorizationEndpoints>) {
        with(result) {
            doIfSuccess { viewModel.getAuthorizationRequest(it) }
            doIfError { // change this and show a dialog to continue with offline login
                showToast(it.message.toString())
            }
        }
    }

    private fun handleAuthRequestResult(result: Result<AuthorizationRequest>) {
        with(result) {
            doIfSuccess { goToSsoLogin(it) }
            doIfError { // change this and show a dialog to continue with offline login
                showToast(it.message.toString())
            }
        }
        hideLoadingDialog()
    }

    private fun handleDevicePasswordResult(result: Result<String>) {
        with(result) {
            doIfSuccess { // change this and use the password for suggested wifi
                showToast(it)
            }
            doIfError { // change this and show a dialog to continue with offline login
                showToast(it.message.toString())
            }
        }
    }

    private fun handleUserResult(result: Result<User>) {
        with(result) {
            doIfSuccess {
                CameraInfo.officerName = it.name ?: ""
                CameraInfo.officerId = it.id ?: ""
                when (CameraInfo.cameraType) {
                    CameraType.X1 -> user = it
                    CameraType.X2 -> startLiveViewActivity()
                }
            }
            doIfError { showUserInformationError() }
        }
    }

    private fun showUserInformationError() {
        hideKeyboard()
        binding.root.showErrorSnackBar(
            getString(R.string.error_getting_officer_information),
            Snackbar.LENGTH_INDEFINITE
        ) {
            verifySessionBeforeAction { viewModel.getUserFromCamera() }
        }
    }

    private fun configureBottomSheet() {
        sheetBehavior.isDraggable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.bottomSheetInstructions.buttonDismissInstructions.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.bottomSheetInstructions.buttonCloseInstructions.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setLoginViews() {
        binding.versionNumberTextLogin.text = getApplicationVersionText()
        showFragmentDependingOnCameraType()
        verifyLocationPermission()
    }

    private fun showFragmentDependingOnCameraType() {
        if (CameraInfo.cameraType == CameraType.X1) showStartPairingFragment()
        else showValidateOfficerIdFragment()
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = getIntentDependsCameraType(LiveX1Activity(), LiveX2Activity())
        startActivity(intent)
        finish()
    }

    private fun showValidateOfficerIdFragment(officerId: String = "") {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = ValidateOfficerIdFragment.createInstance(::onContinueClick, officerId),
            tag = ValidateOfficerIdFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showStartPairingFragment() {
        val startPairingFragment = getStartPairingFragmentDependingOnCameraType()
        val fragmentTag = getFragmentTagDependingOnCameraType()

        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = startPairingFragment,
            tag = fragmentTag,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun getFragmentTagDependingOnCameraType() =
        if (CameraInfo.cameraType == CameraType.X1) StartPairingX1Fragment.TAG
        else StartPairingX2Fragment.TAG

    private fun getStartPairingFragmentDependingOnCameraType() =
        if (CameraInfo.cameraType == CameraType.X1)
            StartPairingX1Fragment.createInstance(::onValidRequirements)
        else StartPairingX2Fragment.createInstance(this)

    private fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(::onConnectionSuccessful),
            tag = PairingResultFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showValidateOfficerPasswordFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = ValidateOfficerPasswordFragment.createInstance(this),
            tag = ValidateOfficerPasswordFragment.TAG,
            animationIn = R.anim.slide_and_fade_in_right,
            animationOut = 0
        )
    }

    private fun verifyLocationPermission() {
        this.verifyForAskingPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            PERMISSION_FOR_LOCATION
        )
    }

    override fun onValidRequirements() = showPairingResultFragment()

    override fun onEditOfficerId(officerId: String) = showValidateOfficerIdFragment(officerId)

    override fun onPasswordValidationResult(isValid: Boolean) {
        if (isValid) startLiveViewActivity()
        else {
            binding.root.showErrorSnackBar(getString(R.string.incorrect_password))
            EspressoIdlingResource.decrement()
        }
    }

    override fun onEmptyUserInformation() = showUserInformationError()

    private fun onContinueClick(isEmail: Boolean, officerId: String) {
        this.officerId = officerId
        if (isEmail) runOnUiThread {
            showLoadingDialog()
            viewModel.getAuthorizationEndpoints()
        }
        else showStartPairingFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RestrictionsManager.RESULT_ERROR_INTERNAL) {
            showStartPairingFragment()
        }

        if (!viewModel.isUserAuthorized() && data != null) {
            val response = buildAuthorizationResponse(data)
            val exception = getAuthorizationException(data)

            when {
                response.authorizationCode != null -> {
                    viewModel.authenticateToGetToken(response, ::onIdTokenResponse)
                }
                exception != null -> {
                    showToast(getString(R.string.sso_auth_error))
                    showStartPairingFragment()
                }
            }
        }
    }

    private fun getAuthorizationException(data: Intent?) =
        AuthorizationException.fromIntent(data)

    private fun buildAuthorizationResponse(data: Intent) =
        AuthorizationResponse.Builder(authRequest).fromUri(data.data!!).build()

    private fun onIdTokenResponse(response: Result<TokenResponse>) {
        with(response) {
            doIfSuccess {
                viewModel.saveToken(it.accessToken.toString())
                viewModel.getDevicePassword(officerId)
            }
            doIfError {
                showToast(getString(R.string.auth_token_error, it.message))
            }
        }
    }

    private fun goToSsoLogin(authRequest: AuthorizationRequest) {
        this.authRequest = authRequest
        val intent = Intent(baseContext, SSOActivity::class.java)
        startActivityForResult(intent, 100)
    }

    private fun onConnectionSuccessful() {
        viewModel.getUserFromCamera()
        if (CameraInfo.cameraType == CameraType.X1) showValidateOfficerPasswordFragment()
    }

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }
}
