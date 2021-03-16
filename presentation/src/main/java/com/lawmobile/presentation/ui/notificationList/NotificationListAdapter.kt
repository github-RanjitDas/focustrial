package com.lawmobile.presentation.ui.notificationList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.NotificationListRecyclerItemBinding

class NotificationListAdapter : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

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
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class ViewHolder(val binding: NotificationListRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: DomainNotification) {
            setNotificationType(notification.type)
            binding.textViewNotification.text = notification.value
            binding.textViewNotificationDate.text = DATE_PLACEHOLDER
        }

        private fun setNotificationType(type: String) {
            when (type) {
                WARNING -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_warning_icon)
                }
                ERROR -> {
                    binding.imageViewNotificationType.setImageResource(R.drawable.ic_error_icon)
                }
            }
        }
    }

    companion object {
        private const val WARNING = "Warning"
        private const val ERROR = "Error"
        private const val DATE_PLACEHOLDER = "10/12/2020"
    }
}
