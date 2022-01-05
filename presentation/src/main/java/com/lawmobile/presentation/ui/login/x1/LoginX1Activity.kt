package com.lawmobile.presentation.ui.login.x1

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.isAnimationsEnabled
import com.lawmobile.presentation.extensions.runWithDelay
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.model.LoginState
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.OfficerPassword
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.x1.fragment.StartPairingFragment
import com.lawmobile.presentation.ui.login.x1.fragment.officerPassword.OfficerPasswordFragment
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class LoginX1Activity : LoginBaseActivity() {

    private val viewModel: LoginX1ViewModel by viewModels()

    override var state: LoginState
        get() = viewModel.getLoginState()
        set(value) {
            viewModel.setLoginState(value)
        }

    override var isInstructionsOpen: Boolean
        get() = viewModel.isInstructionsOpen
        set(value) {
            viewModel.isInstructionsOpen = value
            toggleInstructionsBottomSheet(value)
        }

    private val startPairingFragment = StartPairingFragment()
    private val officerPasswordFragment = OfficerPasswordFragment()

    override val instructions: Instructions get() = startPairingFragment
    override val startPairing: StartPairing get() = startPairingFragment
    override val officerPassword: OfficerPassword get() = officerPasswordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, R.anim.fade_out)
        toggleInstructionsBottomSheet(isInstructionsOpen)
        viewModel.setObservers()
    }

    private fun startAnimation() {
        if (isAnimationsEnabled()) {
            binding.imageViewFMALogoNoAnimation.isVisible = false
            (binding.imageViewFMALogo.drawable as AnimatedVectorDrawable).start()
            runWithDelay(ANIMATION_DURATION) { state = LoginState.X1.StartPairing }
        } else state = LoginState.X1.StartPairing
    }

    private fun showLoginViews() {
        binding.imageViewFMALogoNoAnimation.isVisible = true
        binding.imageViewFMALogo.isVisible = false
        binding.imageViewSafeFleetFooterLogo.isVisible = true
        binding.versionNumberTextLogin.isVisible = true
    }

    private fun LoginX1ViewModel.setObservers() {
        activityCollect(loginState, ::handleLoginState)
        userFromCameraResult.observe(this@LoginX1Activity, ::handleUserResult)
    }

    private fun handleLoginState(loginState: LoginState) {
        with(loginState) {
            onSplashAnimation(::startAnimation) {
                showLoginViews()
            }
            onStartPairing {
                showStartPairingFragment()
                verifyLocationPermission()
                setInstructionsListener()
                setStartPairingListener()
            }
            onPairingResult {
                showPairingResultFragment()
            }
            onOfficerPassword {
                showOfficerPasswordFragment()
                setOnEmptyPasswordListener()
            }
        }
    }

    private fun setOnEmptyPasswordListener() {
        officerPassword.onEmptyPassword = ::getUserFromCamera
    }

    private fun handleUserResult(result: Result<DomainUser>) {
        viewModel.setCameraType()
        with(result) {
            doIfSuccess {
                CameraInfo.officerName = it.name
                CameraInfo.officerId = it.id
                officerPassword.passwordFromCamera = it.password
            }
            doIfError {
                showUserInformationError()
            }
        }
    }

    override fun getUserFromCamera() = viewModel.getUserFromCamera()

    private fun showStartPairingFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = startPairingFragment,
            tag = StartPairingFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showOfficerPasswordFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = officerPasswordFragment,
            tag = OfficerPasswordFragment.TAG,
            animationIn = R.anim.slide_and_fade_in_right,
            animationOut = 0
        )
    }

    override fun onConnectionSuccessful() {
        getUserFromCamera()
        state = LoginState.X1.OfficerPassword
    }

    companion object {
        private const val ANIMATION_DURATION = 2000L
    }
}
