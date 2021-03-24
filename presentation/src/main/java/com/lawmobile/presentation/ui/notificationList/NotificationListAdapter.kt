package com.lawmobile.presentation.ui.notificationList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.NotificationListRecyclerItemBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection

class NotificationListAdapter(
    private val onNotificationItemCLick: (CameraEvent) -> Unit
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    var notificationList = mutableListOf<CameraEvent>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var isSortedByType = false
    private var isSortedByNotification = false
    private var isSortedByDate = false

    fun sortByType() {
        if (isSortedByType) notificationList.sortByDescending { it.eventTag }
        else notificationList.sortBy { it.eventTag }
        isSortedByType = !isSortedByType
        notifyDataSetChanged()
    }

    fun sortByNotification() {
        if (isSortedByNotification) notificationList.sortByDescending { it.value }
        else notificationList.sortBy { it.value }
        isSortedByNotification = !isSortedByNotification
        notifyDataSetChanged()
    }

    fun sortByDate() {
        if (isSortedByDate) notificationList.sortByDescending { it.date }
        else notificationList.sortBy { it.date }
        isSortedByDate = !isSortedByDate
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            NotificationListRecyclerItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding, onNotificationItemCLick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class ViewHolder(
        private val binding: NotificationListRecyclerItemBinding,
        private val onNotificationItemCLick: (CameraEvent) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: CameraEvent) {
            setNotificationType(notification.eventTag)
            setTextViews(notification)
            setListener(notification)
        }

        private fun setListener(notification: CameraEvent) {
            binding.layoutNotificationItem.setOnClickListenerCheckConnection {
                onNotificationItemCLick(notification)
            }
        }

        private fun setTextViews(notification: CameraEvent) {
            binding.textViewNotification.text = notification.value
            binding.textViewNotificationDate.text = notification.date
        }

        private fun setNotificationType(type: EventTag) {
            when (type) {
                EventTag.WARNING -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_warning_icon)
                }
                EventTag.ERROR -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_error_icon)
                }
                EventTag.INFORMATION -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_info_icon)
                }
            }
        }
    }
}
