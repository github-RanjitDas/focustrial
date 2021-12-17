package com.lawmobile.presentation.ui.login.x1

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.live.x1.LiveX1Activity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.x1.fragment.StartPairingFragment
import com.lawmobile.presentation.ui.login.x1.fragment.officerPassword.OfficerPasswordFragment
import com.lawmobile.presentation.ui.login.x1.fragment.officerPassword.OfficerPasswordFragmentListener
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class LoginX1Activity : LoginBaseActivity(), OfficerPasswordFragmentListener {

    private val viewModel: LoginX1ViewModel by viewModels()
    override var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLoginViews()
        viewModel.setObservers()
    }

    private fun setLoginViews() {
        showStartPairingFragment()
        verifyLocationPermission()
    }

    private fun LoginX1ViewModel.setObservers() {
        userFromCameraResult.observe(this@LoginX1Activity, ::handleUserResult)
    }

    private fun handleUserResult(result: Result<User>) {
        with(result) {
            doIfSuccess {
                CameraInfo.officerName = it.name ?: ""
                CameraInfo.officerId = it.id ?: ""
                user = it
            }
            doIfError {
                showUserInformationError()
            }
        }
    }

    override fun getUserFromCamera() = viewModel.getUserFromCamera()

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = Intent(this, LiveX1Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showStartPairingFragment() {
        val startPairingFragment = StartPairingFragment.createInstance(::onValidRequirements)
        val fragmentTag = StartPairingFragment.TAG

        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = startPairingFragment,
            tag = fragmentTag,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showValidateOfficerPasswordFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = OfficerPasswordFragment.createInstance(this),
            tag = OfficerPasswordFragment.TAG,
            animationIn = R.anim.slide_and_fade_in_right,
            animationOut = 0
        )
    }

    private fun onValidRequirements() = showPairingResultFragment()

    override fun onPasswordValidationResult(isValid: Boolean) {
        if (isValid) startLiveViewActivity()
        else {
            binding.root.showErrorSnackBar(getString(R.string.incorrect_password))
            EspressoIdlingResource.decrement()
        }
    }

    override fun onEmptyUserInformation() = showUserInformationError()

    override fun onConnectionSuccessful() {
        getUserFromCamera()
        showValidateOfficerPasswordFragment()
    }
}
