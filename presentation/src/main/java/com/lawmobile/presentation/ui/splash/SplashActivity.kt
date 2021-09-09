package com.lawmobile.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import com.lawmobile.domain.helpers.runWithDelay
import com.lawmobile.presentation.databinding.ActivitySplashBinding
import com.lawmobile.presentation.extensions.isAnimationsEnabled
import com.lawmobile.presentation.ui.base.BaseActivity

class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding

    private val fadeAnimation = AlphaAnimation(0f, 1f).apply {
        duration = ANIMATION_DURATION
        interpolator = LinearInterpolator()
        startOffset = ANIMATION_START_OFFSET
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startAnimation()
        viewModel.setObserver()
    }

    private fun SplashViewModel.setObserver() {
        nextActivityResult.observe(this@SplashActivity) {
            val selectCameraIntent = Intent(baseContext, it)
            startActivity(selectCameraIntent)
        }
    }

    private fun ActivitySplashBinding.startAnimation() {
        if (isAnimationsEnabled()) showAnimation()
        goToSelectCamera()
    }

    private fun ActivitySplashBinding.showAnimation() {
        imageViewFMALogo.animation = fadeAnimation
        imageViewFMALogo.animate()
    }

    private fun goToSelectCamera() {
        runWithDelay(ANIMATION_START_OFFSET + ANIMATION_DURATION) {
            viewModel.getNextActivity()
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 1700L
        private const val ANIMATION_START_OFFSET = 300L
    }
}
