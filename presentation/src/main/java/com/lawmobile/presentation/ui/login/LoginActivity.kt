package com.lawmobile.presentation.ui.login

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLoginBinding
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.getIntentDependsCameraType
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.extensions.verifyForAskingPermission
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.live.x1.LiveX1Activity
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingResultFragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x1.StartPairingX1Fragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x2.StartPairingX2Fragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x2.StartPairingX2FragmentListener
import com.lawmobile.presentation.ui.login.validateOfficerId.ValidateOfficerIdFragment
import com.lawmobile.presentation.ui.login.validateOfficerPassword.ValidateOfficerPasswordFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource

class LoginActivity : BaseActivity(), StartPairingX2FragmentListener {

    private lateinit var activityLoginBinding: ActivityLoginBinding

    val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            activityLoginBinding.bottomSheetInstructions.bottomSheetInstructions
        )
    }

    private val onExistingOfficerId: (Boolean, String) -> Unit = { exist, officerId ->
        if (exist) showToast(
            "The user exists",
            Toast.LENGTH_LONG
        ) // Replace this with navigation to SSO login page
        else showStartPairingFragment(officerId)
    }

    private val onValidOfficerPassword: (Boolean) -> Unit = {
        if (it) startLiveViewActivity()
        else {
            activityLoginBinding.fragmentContainer.showErrorSnackBar(getString(R.string.incorrect_password))
            EspressoIdlingResource.decrement()
        }
    }

    private val onConnectionSuccessful: () -> Unit = ::showValidateOfficerPasswordFragment
    private val onValidX1Requirements: () -> Unit = ::showPairingResultFragment

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
            fragment = ValidateOfficerIdFragment.createInstance(onExistingOfficerId, officerId),
            tag = ValidateOfficerIdFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showStartPairingFragment(officerId: String = "") {
        this.officerId = officerId
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
            StartPairingX1Fragment.createInstance(onValidX1Requirements)
        else StartPairingX2Fragment.createInstance(this)

    private fun showPairingResultFragment() {
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = PairingResultFragment.createInstance(onConnectionSuccessful),
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

    override var officerId: String = ""

    override fun onValidRequirements() = showPairingResultFragment()

    override fun onEditOfficerId(officerId: String) = showValidateOfficerIdFragment(officerId)

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }
}
