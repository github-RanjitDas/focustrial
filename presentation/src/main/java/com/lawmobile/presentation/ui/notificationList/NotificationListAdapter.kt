package com.lawmobile.presentation.ui.notificationList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.entities.NotificationType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.NotificationListRecyclerItemBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection

class NotificationListAdapter(
    private val onNotificationItemCLick: (DomainNotification) -> Unit
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    var notificationList = mutableListOf<DomainNotification>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isSortedByType = false
    var isSortedByNotification = false

    fun sortByType() {
        if (isSortedByType) notificationList.sortByDescending { it.type }
        else notificationList.sortBy { it.type }
        isSortedByType = !isSortedByType
        notifyDataSetChanged()
    }

    fun sortByNotification() {
        if (isSortedByNotification) notificationList.sortByDescending { it.value }
        else notificationList.sortBy { it.value }
        isSortedByNotification = !isSortedByNotification
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
        private val onNotificationItemCLick: (DomainNotification) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: DomainNotification) {
            setNotificationType(notification.type)
            setTextViews(notification)
            setListener(notification)
        }

        private fun setListener(notification: DomainNotification) {
            binding.layoutNotificationItem.setOnClickListenerCheckConnection {
                onNotificationItemCLick(notification)
            }
        }

        private fun setTextViews(notification: DomainNotification) {
            binding.textViewNotification.text = notification.value
            binding.textViewNotificationDate.text = DATE_PLACEHOLDER
        }

        private fun setNotificationType(type: NotificationType) {
            when (type) {
                NotificationType.WARNING -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_warning_icon)
                }
                NotificationType.ERROR -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_error_icon)
                }
                NotificationType.INFORMATION -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_info_icon)
                }
            }
        }
    }

    companion object {
        private const val DATE_PLACEHOLDER = "10/12/2020"
    }
}
