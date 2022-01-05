package com.lawmobile.presentation.ui.login

import android.Manifest
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLoginBinding
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifyForAskingPermission
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.model.LoginState
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.OfficerPassword
import com.lawmobile.presentation.ui.login.shared.PairingResultFragment
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.safefleet.mobile.android_commons.extensions.hideKeyboard

abstract class LoginBaseActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding

    protected abstract var state: LoginState
    protected abstract var isInstructionsOpen: Boolean

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetInstructions.bottomSheetInstructions)
    }

    protected abstract val instructions: Instructions
    protected abstract val startPairing: StartPairing
    protected abstract val officerPassword: OfficerPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.versionNumberTextLogin.text = getApplicationVersionText()
        configureBottomSheet()
    }

    protected fun setInstructionsListener() {
        instructions.onInstructionsClick = {
            isInstructionsOpen = true
        }
    }

    protected fun setStartPairingListener() {
        startPairing.onStartPairingClick = {
            state = LoginState.PairingResult
        }
    }

    private fun configureBottomSheet() = with(binding) {
        sheetBehavior.isDraggable = false
        bottomSheetInstructions.buttonDismissInstructions.setOnClickListener {
            isInstructionsOpen = false
        }
        bottomSheetInstructions.buttonCloseInstructions.setOnClickListener {
            isInstructionsOpen = false
        }
    }

    fun toggleInstructionsBottomSheet(isOpen: Boolean) {
        if (isOpen) sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        else sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun verifyLocationPermission() {
        this.verifyForAskingPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            PERMISSION_FOR_LOCATION
        )
    }

    fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(::onConnectionSuccessful),
            tag = PairingResultFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    fun showUserInformationError() {
        hideKeyboard()
        binding.root.showErrorSnackBar(
            getString(R.string.error_getting_officer_information),
            Snackbar.LENGTH_INDEFINITE
        ) {
            verifySessionBeforeAction { getUserFromCamera() }
        }
    }

    abstract fun getUserFromCamera()

    abstract fun onConnectionSuccessful()

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }
}
