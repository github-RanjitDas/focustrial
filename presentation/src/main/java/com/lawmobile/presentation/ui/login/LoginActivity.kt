package com.lawmobile.presentation.ui.login

import android.Manifest
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLoginBinding
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.getIntentDependsCameraType
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifyForAskingPermission
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.live.x1.LiveX1Activity
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingResultFragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.StartPairingFragment
import com.lawmobile.presentation.ui.login.validateOfficerId.ValidateOfficerIdFragment
import com.lawmobile.presentation.ui.login.validateOfficerPassword.ValidateOfficerPasswordFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource

class LoginActivity : BaseActivity() {

    private lateinit var activityLoginBinding: ActivityLoginBinding

    val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            activityLoginBinding.bottomSheetInstructions.bottomSheetInstructions
        )
    }

    private val onExistingOfficerId: (Boolean) -> Unit = {
        if (it) // go to SSO web view
            else showStartPairingFragment()
    }

    private val onConnectionSuccess: (Boolean) -> Unit = {
        if (it) showValidateOfficerPasswordFragment()
        else showStartPairingFragment()
    }

    private val onValidOfficerPassword: (Boolean) -> Unit = {
        if (it) startLiveViewActivity()
        else {
            activityLoginBinding.fragmentContainer.showErrorSnackBar(getString(R.string.incorrect_password))
            EspressoIdlingResource.decrement()
        }
    }

    private val onValidRequirements: (isSuccess: Boolean) -> Unit = {
        if (it) showPairingResultFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        configureBottomSheet()
        setLoginViews()
    }

    private fun configureBottomSheet() {
        sheetBehavior.isDraggable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        activityLoginBinding.bottomSheetInstructions.buttonDismissInstructions.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        activityLoginBinding.bottomSheetInstructions.buttonCloseInstructions.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setLoginViews() {
        activityLoginBinding.versionNumberTextLogin.text = getApplicationVersionText()
        showValidateOfficerIdFragment()
        verifyLocationPermission()
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = getIntentDependsCameraType(LiveX1Activity(), LiveX2Activity())
        startActivity(intent)
        finish()
    }

    private fun showValidateOfficerIdFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = ValidateOfficerIdFragment.createInstance(onExistingOfficerId),
            tag = ValidateOfficerIdFragment.TAG,
            animationIn = R.anim.bottom_to_top_anim,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showStartPairingFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = StartPairingFragment.createInstance(onValidRequirements),
            tag = StartPairingFragment.TAG,
            animationIn = R.anim.bottom_to_top_anim,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(onConnectionSuccess),
            tag = PairingResultFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showValidateOfficerPasswordFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = ValidateOfficerPasswordFragment.createInstance(onValidOfficerPassword),
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

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }
}
