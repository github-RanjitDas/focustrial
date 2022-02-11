package com.lawmobile.presentation.ui.live.x1

import android.os.Bundle
import androidx.core.view.isVisible
import com.lawmobile.presentation.databinding.ActivityLiveViewBinding
import com.lawmobile.presentation.ui.live.DashboardBaseActivity
import com.lawmobile.presentation.ui.live.appBar.x1.LiveX1AppBarFragment
import com.lawmobile.presentation.ui.live.controls.x1.ControlsX1Fragment
import com.lawmobile.presentation.ui.live.statusBar.x1.StatusBarX1Fragment

class LiveX1Activity : DashboardBaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var binding: ActivityLiveViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setFullscreenVisibility(isVisible: Boolean) {
        binding.fullStreamContainer.isVisible = isVisible
        binding.appBarContainer.isVisible = !isVisible
        binding.controlsContainer.isVisible = !isVisible
        binding.statusBarContainer.isVisible = !isVisible
        binding.navigationContainer.isVisible = !isVisible
        binding.streamContainer.isVisible = !isVisible
    }

    override fun animateAppBar() {
        binding.appBarContainer.startAnimation(appBarAnimation)
    }

    override fun animateContainer() {
        with(binding) {
            statusBarContainer.startAnimation(containerAnimation)
            streamContainer.startAnimation(containerAnimation)
            controlsContainer.startAnimation(containerAnimation)
            navigationContainer.startAnimation(containerAnimation)
        }
    }

    override fun setControlsFragment() {
        controlsFragment = ControlsX1Fragment()
        super.setControlsFragment()
    }

    override fun setStatusBarFragment() {
        statusBarFragment = StatusBarX1Fragment()
        super.setStatusBarFragment()
    }

    override fun setAppBarFragment() {
        appBarFragment = LiveX1AppBarFragment()
        super.setAppBarFragment()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
