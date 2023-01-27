package com.lawmobile.presentation.ui.notificationList

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.NotificationListRecyclerItemBinding
import com.lawmobile.presentation.extensions.setImageDependingOnEventTag
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
            println("bind:$notification")
            setNotificationType(notification.eventTag)
            configureNotificationStyle(notification)
            setTextViews(notification)
            setListener(notification)
        }

        private fun setListener(notification: CameraEvent) {
            binding.layoutNotificationItem.setOnClickListenerCheckConnection {
                setNotificationStyleAsRead()
                onNotificationItemCLick(notification)
            }
        }

        private fun setTextViews(notification: CameraEvent) {
            val notificationType = NotificationType.getByValue(notification.name)
            binding.textViewNotification.text = notificationType.title ?: notification.name
            binding.textViewNotificationDate.text = notification.date
        }

        private fun configureNotificationStyle(notification: CameraEvent) {
            if (notification.isRead) setNotificationStyleAsRead()
            else setNotificationStyleAsUnread()
        }

        private fun setNotificationStyleAsRead() {
            binding.layoutNotificationItem.setBackgroundResource(R.color.white)
            binding.textViewNotificationDate.setTypeface(null, Typeface.NORMAL)
            binding.textViewNotification.setTypeface(null, Typeface.NORMAL)
        }

        private fun setNotificationStyleAsUnread() {
            binding.layoutNotificationItem.setBackgroundResource(R.color.backgroundCardView)
            binding.textViewNotification.setTypeface(null, Typeface.BOLD)
            binding.textViewNotificationDate.setTypeface(null, Typeface.BOLD)
        }

        private fun setNotificationType(eventTag: EventTag) {
            binding.imageViewNotificationType.setImageDependingOnEventTag(eventTag)
        }
    }
}
