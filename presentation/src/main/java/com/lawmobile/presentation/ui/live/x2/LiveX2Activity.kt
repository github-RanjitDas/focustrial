package com.lawmobile.presentation.ui.live.x2

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLiveViewX2Binding
import com.lawmobile.presentation.entities.MenuInformation
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.closeMenuButton
import com.lawmobile.presentation.extensions.openMenuButton
import com.lawmobile.presentation.extensions.setOnSwipeRightListener
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment.Companion.isInMainScreen
import com.lawmobile.presentation.ui.base.settingsBar.SettingsBarFragment
import com.lawmobile.presentation.ui.live.DashboardBaseActivity
import com.lawmobile.presentation.ui.live.controls.x2.ControlsX2Fragment
import com.lawmobile.presentation.ui.live.statusBar.x2.StatusBarX2Fragment
import com.lawmobile.presentation.utils.FeatureSupportHelper
import kotlinx.coroutines.delay

class LiveX2Activity : DashboardBaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var binding: ActivityLiveViewX2Binding

    private val appBarX2Fragment = AppBarX2Fragment.createInstance(true, "")
    private val menuFragment = MenuFragment()
    private val statusBarSettingsFragment = SettingsBarFragment()
    private var isMenuOpen = false

    private val menuInformation: MenuInformation by lazy {
        MenuInformation(this, menuFragment, binding.layoutCustomMenu.shadowOpenMenuView)
    }

    override suspend fun onStatusRetrieved() {
        super.onStatusRetrieved()
        delay(TIME_BETWEEN_REQUESTS)
        appBarX2Fragment.getUnreadNotificationCount()
        if (FeatureSupportHelper.supportBodyWornSettings) {
            delay(TIME_BETWEEN_REQUESTS)
            statusBarSettingsFragment.getBodyCameraSettings()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveViewX2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        isInMainScreen = true
        super.onResume()
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

    override fun animateContainer() = with(binding) {
        statusBarContainer.startAnimation(containerAnimation)
        streamContainer.startAnimation(containerAnimation)
        controlsContainer.startAnimation(containerAnimation)
        navigationContainer.startAnimation(containerAnimation)
    }

    override fun setListeners() {
        super.setListeners()
        closeMenuListener()
        openMenuListener()
    }

    private fun openMenuListener() {
        appBarX2Fragment.onTapMenuButton = {
            isMenuOpen = true
            binding.layoutCustomMenu.menuContainer.openMenuButton(menuInformation)
        }
    }

    private fun closeMenuListener() {
        menuFragment.onCloseMenuButton = {
            isMenuOpen = false
            binding.layoutCustomMenu.menuContainer.closeMenuButton(menuInformation)
        }
        binding.layoutCustomMenu.shadowOpenMenuView.setOnSwipeRightListener { onCloseMenuButton() }
    }

    private fun onCloseMenuButton() {
        animateCloseMenuContainer()
        binding.layoutCustomMenu.menuContainer.isVisible = false
        binding.layoutCustomMenu.shadowOpenMenuView.isVisible = false
        isMenuOpen = false
    }

    private fun animateCloseMenuContainer() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        val animationShadow = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        binding.layoutCustomMenu.menuContainer.startAnimation(animation)
        binding.layoutCustomMenu.shadowOpenMenuView.startAnimation(animationShadow)
    }

    override fun setFragments() {
        super.setFragments()
        setMenuFragment()
        setSettingsBarFragment()
    }

    override fun setControlsFragment() {
        controlsFragment = ControlsX2Fragment()
        super.setControlsFragment()
    }

    override fun setStatusBarFragment() {
        statusBarFragment = StatusBarX2Fragment()
        super.setStatusBarFragment()
    }

    override fun setAppBarFragment() {
        appBarFragment = appBarX2Fragment
        super.setAppBarFragment()
    }

    private fun setMenuFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.menuContainer,
            fragment = menuFragment,
            tag = MenuFragment.TAG
        )
    }

    private fun setSettingsBarFragment() {
        if (FeatureSupportHelper.supportBodyWornSettings) {
            supportFragmentManager.attachFragment(
                containerId = R.id.settingsBarContainer,
                fragment = statusBarSettingsFragment,
                tag = SettingsBarFragment.TAG
            )
        }
    }

    override fun onBackPressed() {
        if (isMenuOpen) onCloseMenuButton()
        else moveTaskToBack(true)
    }
}
