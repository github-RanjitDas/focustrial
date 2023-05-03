package com.lawmobile.presentation.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLoginBinding
import com.lawmobile.presentation.extensions.IS_FIRST_TIME_ASKING_PERMISSION
import com.lawmobile.presentation.extensions.PREFS_PERMISSIONS
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.hasPermissions
import com.lawmobile.presentation.extensions.requestPermissions
import com.lawmobile.presentation.extensions.shouldShowPermissionRationale
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.PairingResultFragment
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity.Companion.TAG_CC_PWD_VALIDATION
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBar
import kotlin.system.exitProcess

abstract class LoginBaseActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding

    protected var state: LoginState
        get() = baseViewModel.getLoginState()
        set(value) {
            baseViewModel.setLoginState(value)
        }

    protected lateinit var baseViewModel: LoginBaseViewModel

    protected var isOnlyShowSuccessAnimation = false

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
    protected var permissionStatus: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionStatus = getSharedPreferences(PREFS_PERMISSIONS, Context.MODE_PRIVATE)
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
            }
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_CAMERA_ERRORS,
                    it,
                    getString(R.string.error_getting_officer_information)
                )
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

    private fun showPermissionDialogToEducateUser() {
        var title = ""
        var description = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            title = getString(R.string.nearby_permission_title)
            description = getString(R.string.nearby_permission_description)
        } else {
            title = getString(R.string.location_permission_title)
            description = getString(R.string.location_permission_description)
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setMessage(description)
        builder.setPositiveButton(getString(R.string.button_allow_permissions)) { dialog, _ ->
            dialog.cancel()
            requestPermissions(this, REQUEST_PERMISSION_CODE)
        }
        builder.setNegativeButton("Exit") { dialog, which ->
            dialog.cancel()
            this@LoginBaseActivity.finishAffinity()
            exitProcess(0)
        }
        builder.show()
    }

    protected fun verifyPermissions() {
        if (hasPermissions(this)) {
            onPermissionsGranted()
        } else {
            // Some permissions not granted.
            if (shouldShowPermissionRationale(this)) {
                // should rationale is false so user denied the request, so show educate dialog
                showPermissionDialogToEducateUser()
            } else if (!isFirstTimeAskingPermission()) {
                // should rationale is false so user denied the request, and we are not
                // requesting permissions first time, It means it denied permanently
                showDialogToOpenSettings()
            } else {
                // Just Request the Permissions
                requestPermissions(this, REQUEST_PERMISSION_CODE)
            }
        }
    }

    private fun showDialogToOpenSettings() {
        var title = 0
        var description = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            title = R.string.please_enable_permission
            description = R.string.please_enable_permission_setting_nearby
        } else {
            title = R.string.please_enable_permission
            description = R.string.please_enable_permission_setting_location
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setMessage(description)
        builder.setPositiveButton(getString(R.string.button_allow_permissions)) { dialog, _ ->
            dialog.cancel()
            openSettingScreen()
        }
        builder.setNegativeButton("Exit") { dialog, which ->
            dialog.cancel()
            this@LoginBaseActivity.finishAffinity()
            exitProcess(0)
        }
        builder.show()
    }

    private var sentToSettings = false
    private fun openSettingScreen() {
        sentToSettings = true
        resultLauncherSettings.launch(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.packageName)
            )
        )
    }

    private var resultLauncherSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (hasPermissions(this)) {
                    onPermissionsGranted()
                }
            } else {
                verifyPermissions()
            }
        }

    abstract fun onPermissionsGranted()

    private fun isFirstTimeAskingPermission(): Boolean {
        return permissionStatus!!.getBoolean(IS_FIRST_TIME_ASKING_PERMISSION, true)
    }

    private fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(
                ::onConnectionSuccessful,
                ::setHandleConnectionAnimationCallback
            ),
            tag = PairingResultFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    open fun setHandleConnectionAnimationCallback(handleConnectionAnimation: OnHandleConnectionAnimation) {
        this.handleConnectionAnimationListener = null
        this.handleConnectionAnimationListener = handleConnectionAnimation
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

    open fun onConnectionSuccessful() {
        Log.d(TAG_CC_PWD_VALIDATION, "onConnectionSuccessful()")
        CameraInfo.isCameraConnected = true
        baseViewModel.getUserFromCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            updateFirstTimeAskingPermissionPrefs()
            if (hasPermissions(this)) {
                onPermissionsGranted()
            } else {
                if (shouldShowPermissionRationale(this, permissions)) {
                    showPermissionDialogToEducateUser()
                } else {
                    showDialogToOpenSettings()
                }
            }
        }
    }

    private fun updateFirstTimeAskingPermissionPrefs() {
        val editor = permissionStatus!!.edit()
        editor.putBoolean(IS_FIRST_TIME_ASKING_PERMISSION, false)
        editor.apply()
    }

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }

    protected var handleConnectionAnimationListener: OnHandleConnectionAnimation? = null

    interface OnHandleConnectionAnimation {
        fun onShowSuccessAnimation()
    }
}
