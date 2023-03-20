package com.lawmobile.presentation.ui.login

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLoginBinding
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifyForAskingPermission
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.PairingResultFragment
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.state.LoginState
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBar

abstract class LoginBaseActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding

    protected var state: LoginState
        get() = baseViewModel.getLoginState()
        set(value) {
            baseViewModel.setLoginState(value)
        }

    protected lateinit var baseViewModel: LoginBaseViewModel

    private var isInstructionsOpen: Boolean
        get() = baseViewModel.isInstructionsOpen
        set(value) {
            baseViewModel.isInstructionsOpen = value
            toggleInstructionsBottomSheet(value)
        }

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetInstructions.bottomSheetInstructions)
    }

    protected abstract val instructions: Instructions
    protected abstract val startPairing: StartPairing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.versionNumberTextLogin.text = getApplicationVersionText()
        configureBottomSheet()
    }

    protected fun setCollectors() {
        activityCollect(baseViewModel.loginState, ::handleLoginState)
        baseViewModel.userFromCameraResult.observe(this, ::handleUserResult)
    }

    protected open fun handleLoginState(loginState: LoginState) {
        loginState.onPairingResult {
            showPairingResultFragment()
        }
    }

    protected open fun handleUserResult(result: Result<User>) {
        with(result) {
            doIfSuccess {
                CameraInfo.officerName = it.name ?: ""
                CameraInfo.officerId = it.id ?: ""
            }
            doIfError {
                showUserInformationError()
            }
        }
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

    protected fun restoreBottomSheetState() {
        toggleInstructionsBottomSheet(isInstructionsOpen)
    }

    protected fun updateInstructionText(isX2: Boolean) {
        val textViewInstruction5 =
            binding.bottomSheetInstructions.bottomSheetInstructions.findViewById<TextView>(R.id.textViewInstruction5)
        val textViewInstruction6 =
            binding.bottomSheetInstructions.bottomSheetInstructions.findViewById<TextView>(R.id.textViewInstruction6)
        if (isX2) {
            textViewInstruction5.text = getString(R.string.instruction_5_x2)
            textViewInstruction6.text = getString(R.string.instruction_6_x2)
        }
    }

    private fun toggleInstructionsBottomSheet(isOpen: Boolean) {
        if (isOpen) sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        else sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun showPermissionDialogToEducateUser() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.location_permission_title))
            builder.setCancelable(false)
            builder.setMessage(getString(R.string.location_permission_description))
            builder.setPositiveButton(getString(R.string.button_allow_permissions)) { dialog, _ ->
                dialog.cancel()
                verifyLocationPermission()
            }
//        builder.setNegativeButton("Exit") { dialog, which ->
//            dialog.cancel()
//            this.finish()
//        }
            builder.show()
        }
    }

    private fun verifyLocationPermission() {
        this.verifyForAskingPermission(
            Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_FOR_LOCATION
        )
    }

    private fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(::onConnectionSuccessful),
            tag = PairingResultFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showUserInformationError() {
        hideKeyboard()
        binding.root.showErrorSnackBar(
            getString(R.string.error_getting_officer_information), Snackbar.LENGTH_INDEFINITE
        ) {
            verifySessionBeforeAction { baseViewModel.getUserFromCamera() }
        }
    }

    fun showErrorSnackBar(message: Int, retry: () -> Unit): SafeFleetSnackBar? {
        hideKeyboard()
        return binding.root.showErrorSnackBar(
            getString(message), Snackbar.LENGTH_INDEFINITE
        ) {
            retry.invoke()
        }
    }

    protected open fun onConnectionSuccessful() {
        baseViewModel.getUserFromCamera()
    }

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }
}
