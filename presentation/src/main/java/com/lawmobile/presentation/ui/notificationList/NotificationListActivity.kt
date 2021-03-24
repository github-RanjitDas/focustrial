package com.lawmobile.presentation.ui.notificationList

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityNotificationListBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseActivity

class NotificationListActivity : BaseActivity() {

    private val viewModel: NotificationListViewModel by viewModels()

    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var notificationListAdapter: NotificationListAdapter

    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetNotification.bottomSheetNotification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomAppBar()
        setListeners()
        setAdapter()
        setRecyclerView()
        configureBottomSheet()
        setNotificationList()
    }

    private fun setNotificationList() {
        notificationListAdapter.notificationList = viewModel.getNotificationList() as MutableList
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
            layoutCustomAppBar.imageButtonBackArrow.setOnClickListenerCheckConnection {
                onBackPressed()
            }
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
        binding.layoutCustomAppBar.textViewTitle.text = getString(R.string.notifications)
        binding.layoutCustomAppBar.buttonThumbnailList.isVisible = false
        binding.layoutCustomAppBar.buttonSimpleList.isVisible = false
    }
}
