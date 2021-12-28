package com.lawmobile.presentation.ui.live

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.annotation.IdRes
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.runWithDelay
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.live.controls.ControlsBaseFragment
import com.lawmobile.presentation.ui.live.controls.x1.ControlsX1Fragment
import com.lawmobile.presentation.ui.live.model.DashboardState
import com.lawmobile.presentation.ui.live.navigation.x1.NavigationX1Fragment
import com.lawmobile.presentation.ui.live.statusBar.StatusBarBaseFragment
import com.lawmobile.presentation.ui.live.stream.LiveStreamFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers

abstract class DashboardBaseActivity : BaseActivity() {
    private val viewModel: DashboardBaseViewModel by viewModels()

    private val state: DashboardState get() = viewModel.getDashboardState()

    lateinit var appBarFragment: BaseFragment
    lateinit var statusBarFragment: StatusBarBaseFragment
    private lateinit var liveStreamFragment: LiveStreamFragment
    lateinit var controlsFragment: ControlsBaseFragment
    lateinit var navigationFragment: BaseFragment

    private var isFirstOpen = true

    val appBarAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_anim)
    }

    val containerAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.slide_and_fade_in_right).apply {
            startOffset = 300
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        observeState()
    }

    override fun onResume() {
        super.onResume()
        state.onDefault {
            updateLiveOrPlaybackActive(controlsFragment.buttonSwitchLiveView.isActivated)
        }
    }

    private fun observeState() {
        viewModel.dashboardState.observe(this) {
            with(it) {
                onDefault {
                    setFullscreenVisibility(false)
                    setFragments()
                    getInformationAfterAnimation()
                    animateAppBar()
                    animateContainer()
                    setListeners()
                }
                onFullscreen {
                    setFullscreenVisibility(true)
                    setFullscreenStream()
                }
            }
        }
    }

    abstract fun setFullscreenVisibility(isVisible: Boolean)

    private fun getInformationAfterAnimation() {
        runWithDelay(VIEW_LOADING_TIME, Dispatchers.Main.immediate) {
            statusBarFragment.getCameraStatus(true)
            statusBarFragment.isViewLoaded = true
        }
    }

    abstract fun animateAppBar()

    abstract fun animateContainer()

    open fun setListeners() {
        controlsFragment.cameraOperationListener()
        controlsFragment.liveStreamSwitchListener()
    }

    private fun ControlsBaseFragment.liveStreamSwitchListener() {
        onLiveStreamSwitchClick = ::onLiveStreamSwitchClick
    }

    private fun ControlsBaseFragment.cameraOperationListener() {
        onCameraOperation = {
            EspressoIdlingResource.increment()
            onLiveStreamSwitchClick(true)
            liveStreamFragment.showLoadingState(it)
        }

        onCameraOperationFinished = {
            liveStreamFragment.hideLoadingState()
            EspressoIdlingResource.decrement()
        }
    }

    private fun onLiveStreamSwitchClick(isActive: Boolean) {
        updateLiveOrPlaybackActive(isActive)
        liveStreamFragment.setStreamVisibility(isActive)
    }

    private fun setFullscreenStream() {
        if (isFirstOpen) {
            setStreamFragment(R.id.fullStreamContainer)
            isFirstOpen = false
        }
        setStreamFragment(R.id.fullStreamContainer)
    }

    open fun setFragments() {
        setAppBarFragment()
        setStatusBarFragment()
        setStreamFragment(R.id.streamContainer)
        setControlsFragment()
        setNavigationFragment()
    }

    private fun setNavigationFragment() {
        navigationFragment = NavigationX1Fragment()
        supportFragmentManager.attachFragment(
            containerId = R.id.navigationContainer,
            fragment = navigationFragment,
            tag = NavigationX1Fragment.TAG
        )
    }

    open fun setControlsFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.controlsContainer,
            fragment = controlsFragment,
            tag = ControlsX1Fragment.TAG
        )
    }

    private fun setStreamFragment(@IdRes containerId: Int) {
        liveStreamFragment = LiveStreamFragment()
        supportFragmentManager.attachFragment(
            containerId = containerId,
            fragment = liveStreamFragment,
            tag = LiveStreamFragment.TAG
        )
    }

    open fun setStatusBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.statusBarContainer,
            fragment = statusBarFragment,
            tag = statusBarFragment::class.java.simpleName
        )
    }

    open fun setAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX2Fragment.TAG
        )
    }

    companion object {
        private const val VIEW_LOADING_TIME = 800L
    }
}
