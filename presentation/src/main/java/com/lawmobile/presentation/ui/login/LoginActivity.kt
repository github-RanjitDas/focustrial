package com.lawmobile.presentation.ui.login

import android.Manifest
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifyForAskingPermission
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.live.LiveActivity
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingResultFragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.StartPairingFragment
import com.lawmobile.presentation.ui.login.validateOfficerPassword.ValidateOfficerPasswordFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.bottom_sheet_instructions_connect_camera.*

class LoginActivity : BaseActivity() {

    private val loginActivityViewModel: LoginActivityViewModel by viewModels()
    val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            bottomSheetInstructions
        )
    }

    private val connectionSuccess: (isSuccess: Boolean) -> Unit = {
        if (it) showValidateOfficerPasswordFragment()
        else showFragmentPairingCamera()
    }
    private val validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit = {
        if (it) startLiveViewActivity()
        else {
            fragmentContainer.showErrorSnackBar(getString(R.string.incorrect_password))
        }
    }
    private val validateRequirements: (isSuccess: Boolean) -> Unit = {
        if (it) showPairingResultFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        overridePendingTransition(0, R.anim.fade_out)
        configureBottomSheet()
        startAnimation()
    }

    private fun configureBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        buttonDismissInstructions.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED)
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            else sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        buttonCloseInstructions.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun startAnimation() {
        if (isAnimationsEnabled()) {
            imageViewFMALogoNoAnimation.isVisible = false
            (imageViewFMALogo.drawable as AnimatedVectorDrawable).start()
            loginActivityViewModel.waitToFinish(ANIMATION_DURATION)
            loginActivityViewModel.isWaitFinishedLiveData.observe(
                this,
                Observer(::showLoginViews)
            )
        } else {
            imageViewFMALogoNoAnimation.isVisible = true
            imageViewFMALogo.isVisible = false
            showLoginViews(true)
        }
    }

    private fun isAnimationsEnabled() =
        Settings.System.getFloat(
            contentResolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            0F
        ) != 0F
                && Settings.System.getFloat(
            contentResolver,
            Settings.Global.WINDOW_ANIMATION_SCALE,
            0F
        ) != 0F
                && Settings.System.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            0F
        ) != 0F

    private fun showLoginViews(isFinished: Boolean) {
        if (isFinished) {
            imageViewSafeFleet.isVisible = true
            showFragmentPairingCamera()
            verifyLocationPermission()
        }
    }

    private fun startLiveViewActivity() {
        val liveActivityIntent = Intent(this, LiveActivity::class.java)
        startActivity(liveActivityIntent)
        this.finish()
    }

    private fun showFragmentPairingCamera() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = StartPairingFragment.createInstance(validateRequirements),
            tag = StartPairingFragment.TAG,
            animationIn = R.anim.bottom_to_top_anim,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(connectionSuccess),
            tag = PairingResultFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showValidateOfficerPasswordFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = ValidateOfficerPasswordFragment.createInstance(validateSuccessPasswordOfficer),
            tag = ValidateOfficerPasswordFragment.TAG,
            animationIn = R.anim.slide_in_right,
            animationOut = 0
        )
    }

    private fun verifyLocationPermission() {
        this.verifyForAskingPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            PERMISSION_FOR_LOCATION
        )
    }

    override fun onBackPressed() {}

    companion object {
        private const val ANIMATION_DURATION = 2000L
    }

}
