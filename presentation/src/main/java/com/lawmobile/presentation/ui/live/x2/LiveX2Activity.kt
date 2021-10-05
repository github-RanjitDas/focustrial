package com.lawmobile.presentation.ui.live.x2

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLiveViewBinding
import com.lawmobile.presentation.entities.MenuInformation
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.closeMenuButton
import com.lawmobile.presentation.extensions.openMenuButton
import com.lawmobile.presentation.extensions.setOnSwipeRightListener
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment.Companion.isInMainScreen
import com.lawmobile.presentation.ui.base.statusBar.StatusBarSettingsFragment
import com.lawmobile.presentation.ui.live.LiveActivityBaseViewModel
import com.lawmobile.presentation.ui.live.controls.x1.LiveControlsX1Fragment
import com.lawmobile.presentation.ui.live.controls.x2.LiveControlsX2Fragment
import com.lawmobile.presentation.ui.live.navigation.x1.LiveNavigationX1Fragment
import com.lawmobile.presentation.ui.live.navigation.x2.LiveNavigationX2Fragment
import com.lawmobile.presentation.ui.live.statusBar.x1.LiveStatusBarX1Fragment
import com.lawmobile.presentation.ui.live.statusBar.x2.LiveStatusBarX2Fragment
import com.lawmobile.presentation.ui.live.stream.LiveStreamFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.helpers.Event

class LiveX2Activity : BaseActivity() {

    private val viewModel: LiveActivityBaseViewModel by viewModels()
    private lateinit var binding: ActivityLiveViewBinding

    private val appBarFragment = AppBarX2Fragment.createInstance(true, "")
    private val statusBarFragment = LiveStatusBarX2Fragment()
    private val streamFragment = LiveStreamFragment()
    private val controlsFragment = LiveControlsX2Fragment()
    private val navigationFragment = LiveNavigationX2Fragment()
    private val menuFragment = MenuFragment()
    private val statusBarSettingsFragment = StatusBarSettingsFragment.createInstance()
    private var isMenuOpen = false

    private lateinit var menuInformation: MenuInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(0, 0)
        activitySetup()
        menuInformation =
            MenuInformation(this, menuFragment, binding.layoutCustomMenu.shadowOpenMenuView)
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
        isInMainScreen = true
        if (isInPortraitMode()) updateLiveOrPlaybackActive(controlsFragment.buttonSwitchLiveView.isActivated)
        statusBarFragment.getCameraStatus(statusBarFragment.isViewLoaded)
    }

    private fun setListeners() {
        controlsFragment.onCameraOperation = ::onCameraOperation
        controlsFragment.onLiveStreamSwitchClick = ::onLiveStreamSwitchClick
        controlsFragment.onCameraOperationFinished = ::onCameraOperationFinished
        menuFragment.onCloseMenuButton = {
            isMenuOpen = false
            binding.layoutCustomMenu.menuContainer.closeMenuButton(menuInformation)
        }
        appBarFragment.onTapMenuButton = {
            isMenuOpen = true
            binding.layoutCustomMenu.menuContainer.openMenuButton(menuInformation)
        }
        binding.layoutCustomMenu.shadowOpenMenuView.setOnSwipeRightListener { onCloseMenuButton() }
    }

    private fun onCloseMenuButton() {
        animateCloseMenuContainer()
        binding.layoutCustomMenu.menuContainer.isVisible = false
        binding.layoutCustomMenu.shadowOpenMenuView.isVisible = false
        isMenuOpen = false
    }

    private fun onCameraOperation(message: String) {
        EspressoIdlingResource.increment()
        onLiveStreamSwitchClick(true)
        streamFragment.showLoadingState(message)
        setStreamFragment()
    }

    private fun onCameraOperationFinished(isAudio: Boolean) {
        streamFragment.hideLoadingState()
        streamFragment.showRecordingAudio(isAudio)
        streamFragment.setStreamVisibility(!isAudio)
        EspressoIdlingResource.decrement()
        setStreamFragment()
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
            statusBarFragment.getCameraStatus(it)
            statusBarFragment.isViewLoaded = it
        }
    }

    private fun animateCloseMenuContainer() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        val animationShadow = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        binding.layoutCustomMenu.menuContainer.startAnimation(animation)
        binding.layoutCustomMenu.shadowOpenMenuView.startAnimation(animationShadow)
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
        setStatusBarSettingsFragment()
    }

    private fun setMenuFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.menuContainer,
            fragment = menuFragment,
            tag = MenuFragment.TAG
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

    private fun setStatusBarSettingsFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.settingsBarContainer,
            fragment = statusBarSettingsFragment,
            tag = StatusBarSettingsFragment.TAG
        )
    }

    private fun setAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX2Fragment.TAG
        )
    }

    override fun onBackPressed() {
        if (isMenuOpen) onCloseMenuButton()
        else moveTaskToBack(true)
    }

    companion object {
        private const val VIEW_LOADING_TIME = 800L
    }
}
