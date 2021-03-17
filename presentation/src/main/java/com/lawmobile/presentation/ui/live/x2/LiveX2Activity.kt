package com.lawmobile.presentation.ui.live.x2

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLiveViewBinding
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.setOnSwipeRightListener
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.live.LiveActivityBaseViewModel
import com.lawmobile.presentation.ui.live.appBar.x2.LiveAppBarX2Fragment
import com.lawmobile.presentation.ui.live.controls.x1.LiveControlsX1Fragment
import com.lawmobile.presentation.ui.live.menu.LiveMenuFragment
import com.lawmobile.presentation.ui.live.navigation.x1.LiveNavigationX1Fragment
import com.lawmobile.presentation.ui.live.statusBar.x1.LiveStatusBarX1Fragment
import com.lawmobile.presentation.ui.live.statusBar.x2.LiveStatusBarX2Fragment
import com.lawmobile.presentation.ui.live.stream.LiveStreamFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.helpers.Event

class LiveX2Activity : BaseActivity() {

    private val viewModel: LiveActivityBaseViewModel by viewModels()
    private lateinit var binding: ActivityLiveViewBinding

    private val appBarFragment = LiveAppBarX2Fragment()
    private val statusBarFragment = LiveStatusBarX2Fragment()
    private val streamFragment = LiveStreamFragment()
    private val controlsFragment = LiveControlsX1Fragment()
    private val navigationFragment = LiveNavigationX1Fragment()
    private val menuFragment = LiveMenuFragment()

    private var isMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(0, 0)
        activitySetup()
    }

    private fun activitySetup() {
        if (isInPortraitMode()) {
            setObservers()
            setFragments()
            waitUntilViewIsLoadedToGetInformation()
            animateAppBar()
            animateContainer()
            setListeners()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInPortraitMode()) updateLiveOrPlaybackActive(controlsFragment.buttonSwitchLiveView.isActivated)
    }

    private fun setListeners() {
        controlsFragment.onCameraOperation = ::onCameraOperation
        controlsFragment.onLiveStreamSwitchClick = ::onLiveStreamSwitchClick
        controlsFragment.onCameraOperationFinished = ::onCameraOperationFinished
        appBarFragment.onTapMenuButton = ::onTapMenuButton
        menuFragment.onCloseMenuButton = ::onCloseMenuButton
        binding.shadowOpenMenuView?.setOnSwipeRightListener { onCloseMenuButton() }
    }

    private fun onTapMenuButton() {
        binding.menuContainer.isVisible = true
        binding.shadowOpenMenuView?.isVisible = true
        isMenuOpen = true
        animateOpenMenuContainer()
    }

    private fun onCloseMenuButton() {
        animateCloseMenuContainer()
        isMenuOpen = false
        binding.menuContainer.isVisible = false
        binding.shadowOpenMenuView?.isVisible = false
    }

    private fun onCameraOperation(message: String) {
        EspressoIdlingResource.increment()
        onLiveStreamSwitchClick(true)
        streamFragment.showLoadingState(message)
    }

    private fun onCameraOperationFinished() {
        streamFragment.hideLoadingState()
        EspressoIdlingResource.decrement()
    }

    private fun onLiveStreamSwitchClick(isActive: Boolean) {
        updateLiveOrPlaybackActive(isActive)
        streamFragment.setStreamVisibility(isActive)
    }

    private fun setObservers() {
        viewModel.isWaitFinishedLiveData.observe(this, ::startRetrievingInformation)
    }

    private fun startRetrievingInformation(isViewLoaded: Event<Boolean>) {
        isViewLoaded.getContentIfNotHandled()?.let {
            if (it && CameraInfo.events.isEmpty()) statusBarFragment.getCameraStatus(it)
            statusBarFragment.isViewLoaded = it
        }
    }

    private fun animateOpenMenuContainer() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        binding.menuContainer.startAnimation(animation)
    }

    private fun animateCloseMenuContainer() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        val animationShadow = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        binding.menuContainer.startAnimation(animation)
        binding.shadowOpenMenuView?.startAnimation(animationShadow)
    }

    private fun animateAppBar() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_anim)
        binding.appBarContainer.startAnimation(animation)
    }

    private fun animateContainer() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_and_fade_in_right).apply {
            startOffset = 300
        }
        with(binding) {
            statusBarContainer.startAnimation(animation)
            streamContainer.startAnimation(animation)
            controlsContainer.startAnimation(animation)
            navigationContainer.startAnimation(animation)
        }
    }

    private fun waitUntilViewIsLoadedToGetInformation() {
        viewModel.waitToFinish(VIEW_LOADING_TIME)
    }

    private fun setFragments() {
        setAppBarFragment()
        setStatusBarFragment()
        setStreamFragment()
        setControlsFragment()
        setNavigationFragment()
        setMenuFragment()
    }

    private fun setMenuFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.menuContainer,
            fragment = menuFragment,
            tag = LiveMenuFragment.TAG
        )
    }

    private fun setNavigationFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.navigationContainer,
            fragment = navigationFragment,
            tag = LiveNavigationX1Fragment.TAG
        )
    }

    private fun setControlsFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.controlsContainer,
            fragment = controlsFragment,
            tag = LiveControlsX1Fragment.TAG
        )
    }

    private fun setStreamFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.streamContainer,
            fragment = streamFragment,
            tag = LiveStreamFragment.TAG
        )
    }

    private fun setStatusBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.statusBarContainer,
            fragment = statusBarFragment,
            tag = LiveStatusBarX1Fragment.TAG
        )
    }

    private fun setAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = LiveAppBarX2Fragment.TAG
        )
    }

    override fun onBackPressed() {
        // This method is implemented to invalidate the behaviour of back button on the phones
    }

    companion object {
        private const val VIEW_LOADING_TIME = 800L
    }
}
