package com.lawmobile.presentation.ui.notificationList

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityNotificationListBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseActivity

class NotificationListActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var notificationListAdapter: NotificationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomAppBar()
        setListeners()
        setAdapter()
        setRecyclerView()
        notificationListAdapter.notificationList = mutableListOf(
            DomainNotification(
                "Warning",
                "Low battery"
            ),
            DomainNotification(
                "Error",
                "Bluetooth not available"
            ),
            DomainNotification(
                "Warning",
                "Full memory"
            )
        )
    }

    private fun setListeners() {
        with(binding) {
            textViewType.setOnClickListener {
                notificationListAdapter.sortByType()
            }
            textViewNotification.setOnClickListener {
                notificationListAdapter.sortByNotification()
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
        notificationListAdapter = NotificationListAdapter()
    }

    private fun setCustomAppBar() {
        binding.layoutCustomAppBar.textViewTitle.text = getString(R.string.notifications)
        binding.layoutCustomAppBar.buttonThumbnailList.isVisible = false
        binding.layoutCustomAppBar.buttonSimpleList.isVisible = false
    }
}
