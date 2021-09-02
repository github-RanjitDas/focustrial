package com.lawmobile.presentation.ui.selectCamera

import android.content.Intent
import android.os.Bundle
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivitySelectCameraBinding
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.LoginActivity

class SelectCameraActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(0, R.anim.fade_out)
        binding.setListeners()
    }

    private fun ActivitySelectCameraBinding.setListeners() {
        buttonContinueListener()
        buttonX1Listener()
        buttonX2Listener()
    }

    private fun ActivitySelectCameraBinding.buttonX2Listener() {
        buttonX2Camera.setOnClickListener {
            it.isActivated = !it.isActivated
            buttonX1Camera.isActivated = false
            setTextColorDependingOnState()
            enableButtonContinue(it.isActivated)
        }
    }

    private fun ActivitySelectCameraBinding.buttonX1Listener() {
        buttonX1Camera.setOnClickListener {
            it.isActivated = !it.isActivated
            buttonX2Camera.isActivated = false
            setTextColorDependingOnState()
            enableButtonContinue(it.isActivated)
        }
    }

    private fun ActivitySelectCameraBinding.setTextColorDependingOnState() {
        val textColorX1 = getTextColorDependingOnState(buttonX1Camera.isActivated)
        textViewX1.setTextColor(textColorX1)
        val textColorX2 = getTextColorDependingOnState(buttonX2Camera.isActivated)
        textViewX2.setTextColor(textColorX2)
    }

    private fun getTextColorDependingOnState(isActivated: Boolean): Int {
        return if (isActivated) resources.getColor(R.color.white, null)
        else resources.getColor(R.color.darkGrey, null)
    }

    private fun ActivitySelectCameraBinding.enableButtonContinue(isActivated: Boolean) {
        buttonContinue.isEnabled = isActivated
        buttonContinue.isActivated = isActivated
    }

    private fun ActivitySelectCameraBinding.buttonContinueListener() {
        buttonContinue.isEnabled = false
        buttonContinue.setOnClickListener {
            if (buttonX1Camera.isActivated || buttonX2Camera.isActivated) {
                goToOnBoardingCards()
            }
        }
    }

    private fun ActivitySelectCameraBinding.goToOnBoardingCards() {
        CameraInfo.cameraType = if (buttonX2Camera.isActivated) CameraType.X2 else CameraType.X1
        val loginActivityIntent = Intent(this@SelectCameraActivity, LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }
}
