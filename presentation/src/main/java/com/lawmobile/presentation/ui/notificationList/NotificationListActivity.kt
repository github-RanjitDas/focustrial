package com.lawmobile.presentation.ui.notificationList

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityNotificationListBinding
import com.lawmobile.presentation.entities.MenuInformation
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.closeMenuButton
import com.lawmobile.presentation.extensions.openMenuButton
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class NotificationListActivity : BaseActivity() {

    private val viewModel: NotificationListViewModel by viewModels()

    private val menuFragment = MenuFragment()
    private lateinit var menuInformation: MenuInformation
    private lateinit var appBarFragment: AppBarX2Fragment
    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var notificationListAdapter: NotificationListAdapter

    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetNotification.bottomSheetNotification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        menuInformation = MenuInformation(this, menuFragment, binding.layoutCustomMenu.shadowOpenMenuView)
        setCustomAppBar()
        setListeners()
        setObservers()
        setAdapter()
        setRecyclerView()
        configureBottomSheet()
        getNotificationList()
        setAllNotificationsAsRead()
        attachFragments()
    }

    private fun attachFragments() {
        attachMenuFragment()
        attachAppBarFragment()
    }

    private fun setObservers() {
        viewModel.notificationListResult.observe(this, ::setNotificationList)
    }

    private fun getNotificationList() {
        viewModel.getAllNotificationEvents()
    }

    private fun setNotificationList(result: Result<List<CameraEvent>>) {
        with(result) {
            doIfSuccess {
                notificationListAdapter.notificationList = it as MutableList
            }
            doIfError {
                binding.root.showErrorSnackBar(
                    getString(R.string.notification_list_error),
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    getNotificationList()
                }
            }
        }
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
            textViewNotificationTitle.text = cameraEvent.name
            with(imageViewNotificationIcon) {
                when (cameraEvent.eventTag) {
                    EventTag.ERROR -> setImageResource(R.drawable.ic_error_icon)
                    EventTag.WARNING -> setImageResource(R.drawable.ic_warning_icon)
                    EventTag.INFORMATION -> setImageResource(R.drawable.ic_info_icon)
                }
            }
            textViewNotificationDate.text = cameraEvent.date
            textViewNotificationMessage.text = cameraEvent.value
        }
    }

    private fun setCustomAppBar() {
        appBarFragment =
            AppBarX2Fragment.createInstance(false, getString(R.string.notifications), false)
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

    private fun setAllNotificationsAsRead() {
        viewModel.setAllNotificationsAsRead()
    }
}
