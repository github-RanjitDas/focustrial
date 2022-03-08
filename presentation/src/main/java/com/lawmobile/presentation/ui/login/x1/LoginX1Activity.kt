package com.lawmobile.presentation.ui.login.x1

import android.os.Bundle
import androidx.activity.viewModels
import com.lawmobile.domain.entities.User
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.ui.login.x1.fragment.StartPairingFragment
import com.lawmobile.presentation.ui.login.x1.fragment.officerPassword.OfficerPasswordFragment
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class LoginX1Activity : LoginBaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private val viewModel: LoginX1ViewModel by viewModels()

    private val startPairingFragment = StartPairingFragment()
    private val officerPasswordFragment = OfficerPasswordFragment()

    override val instructions: Instructions get() = startPairingFragment
    override val startPairing: StartPairing get() = startPairingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, R.anim.fade_out)
        baseViewModel = viewModel
        setCollectors()
        restoreBottomSheetState()
    }

    override fun handleLoginState(loginState: LoginState) {
        super.handleLoginState(loginState)
        with(loginState) {
            onStartPairing {
                showStartPairingFragment()
                verifyLocationPermission()
                setInstructionsListener()
                setStartPairingListener()
            }
            onOfficerPassword {
                showOfficerPasswordFragment()
                setOnEmptyPasswordListener()
            }
        }
    }

    private fun setOnEmptyPasswordListener() {
        officerPasswordFragment.onEmptyPassword = viewModel::getUserFromCamera
    }

    override fun handleUserResult(result: Result<User>) {
        super.handleUserResult(result)
        result.doIfSuccess { officerPasswordFragment.passwordFromCamera = it.password ?: "" }
    }

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
        super.onConnectionSuccessful()
        state = LoginState.X1.OfficerPassword
    }
}
