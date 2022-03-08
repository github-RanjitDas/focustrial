package com.lawmobile.presentation.ui.notificationList

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityNotificationListBinding
import com.lawmobile.presentation.entities.MenuInformation
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.closeMenuButton
import com.lawmobile.presentation.extensions.openMenuButton
import com.lawmobile.presentation.extensions.setImageDependingOnEventTag
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment
import com.lawmobile.presentation.ui.base.settingsBar.SettingsBarFragment
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class NotificationListActivity : BaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private val viewModel: NotificationListViewModel by viewModels()

    private val menuFragment = MenuFragment()
    private lateinit var menuInformation: MenuInformation
    private lateinit var appBarFragment: AppBarX2Fragment
    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var notificationListAdapter: NotificationListAdapter
    private val statusBarSettingsFragment = SettingsBarFragment()

    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetNotification.bottomSheetNotification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        menuInformation =
            MenuInformation(this, menuFragment, binding.layoutCustomMenu.shadowOpenMenuView)
        setCustomAppBar()
        setListeners()
        setObservers()
        setAdapter()
        setRecyclerView()
        configureBottomSheet()
        attachFragments()
    }

    override fun onResume() {
        super.onResume()
        getNotificationList()
    }

    private fun attachFragments() {
        attachMenuFragment()
        attachAppBarFragment()
        attachStatusBarSettingsFragment()
    }

    private fun setObservers() {
        viewModel.cameraEventsResult.observe(this, ::manageCameraEventsResult)
        viewModel.notificationEventsResult.observe(this, ::manageNotificationEventsResult)
    }

    private fun getNotificationList() {
        showLoadingDialog()
        viewModel.getCameraEvents()
    }

    private fun manageCameraEventsResult(result: Result<List<CameraEvent>>) {
        with(result) {
            doIfSuccess {
                viewModel.getNotificationEvents()
            }
            doIfError {
                hideLoadingDialog()
                binding.textViewEmptyList.isVisible = true
            }
        }
        CameraInfo.onReadyToGetSettings?.invoke()
    }

    private fun manageNotificationEventsResult(result: Result<List<CameraEvent>>) {
        with(result) {
            doIfSuccess {
                notificationListAdapter.notificationList = it as MutableList
            }
            doIfError {
                binding.textViewEmptyList.isVisible = true
            }
        }
        hideLoadingDialog()
        setAllNotificationsAsRead()
        binding.textViewEmptyList.isVisible = notificationListAdapter.notificationList.isEmpty()
    }

    private fun configureBottomSheet() {
        binding.bottomSheetNotification.layoutNotificationInformation.buttonDismissNotification.text =
            getString(R.string.OK)
        hideBottomSheet()

        with(binding) {
            bottomSheetNotification.imageButtonCloseNotification.setOnClickListener {
                hideBottomSheet()
            }
            bottomSheetNotification.layoutNotificationInformation.buttonDismissNotification.setOnClickListener {
                hideBottomSheet()
            }
            bottomSheetBehavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // The interface requires to implement this method but not needed
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN ->
                                shadowNotificationListView.isVisible = false
                            else -> shadowNotificationListView.isVisible = true
                        }
                    }
                })
        }
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setListeners() {
        with(binding) {
            textViewType.setOnClickListener {
                notificationListAdapter.sortByType()
            }
            textViewNotification.setOnClickListener {
                notificationListAdapter.sortByNotification()
            }
            textViewDateAndTime.setOnClickListener {
                notificationListAdapter.sortByDate()
            }
            menuFragment.onCloseMenuButton = {
                binding.layoutCustomMenu.menuContainer.closeMenuButton(menuInformation)
            }
            appBarFragment.onTapMenuButton = {
                binding.layoutCustomMenu.menuContainer.openMenuButton(menuInformation)
            }

            appBarFragment.onBackPressed = ::onBackPressed
        }
    }

    private fun setRecyclerView() {
        binding.recyclerViewNotifications.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@NotificationListActivity)
            adapter = notificationListAdapter
        }
    }

    private fun setAdapter() {
        notificationListAdapter = NotificationListAdapter(::onNotificationItemCLick)
    }

    private fun onNotificationItemCLick(cameraEvent: CameraEvent) {
        showBottomSheet()
        setNotificationInformation(cameraEvent)
    }

    private fun setNotificationInformation(cameraEvent: CameraEvent) {
        with(binding.bottomSheetNotification.layoutNotificationInformation) {
            val notificationType = NotificationType.getByValue(cameraEvent.name)
            textViewNotificationTitle.text = notificationType.title ?: cameraEvent.name
            imageViewNotificationIcon.setImageDependingOnEventTag(cameraEvent.eventTag)
            textViewNotificationMessage.text =
                notificationType.getCustomMessage(cameraEvent.value) ?: cameraEvent.value
            textViewNotificationDate.text = cameraEvent.date
        }
    }

    private fun setCustomAppBar() {
        appBarFragment =
            AppBarX2Fragment.createInstance(false, getString(R.string.notifications))
    }

    private fun attachAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX2Fragment.TAG
        )
    }

    private fun attachMenuFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.menuContainer,
            fragment = menuFragment,
            tag = MenuFragment.TAG
        )
    }

    private fun attachStatusBarSettingsFragment() {
        if (FeatureSupportHelper.supportBodyWornSettings) {
            supportFragmentManager.attachFragment(
                containerId = R.id.statusBarFragment,
                fragment = statusBarSettingsFragment,
                tag = SettingsBarFragment.TAG
            )
        }
    }

    private fun setAllNotificationsAsRead() {
        CameraInfo.currentNotificationCount = 0
        viewModel.setAllNotificationsAsRead()
    }
}
