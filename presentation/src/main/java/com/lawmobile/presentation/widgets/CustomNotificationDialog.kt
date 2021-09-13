package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.DialogCustomNotificationBinding
import com.lawmobile.presentation.extensions.setImageDependingOnEventTag

class CustomNotificationDialog(
    context: Context,
    cancelable: Boolean,
    private val cameraEvent: CameraEvent
) : Dialog(
    context,
    cancelable,
    null
),
    View.OnClickListener {

    private lateinit var binding: DialogCustomNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = findViewById<View>(android.R.id.content) as ViewGroup
        binding = DialogCustomNotificationBinding.inflate(layoutInflater, view)
        setTextViews()
        setButtonText(context.getString(R.string.dismiss))
        setNotificationIcon()
        setListeners()
    }

    fun setButtonText(text: String) {
        binding.layoutNotificationInformation.buttonDismissNotification.text = text
    }

    private fun setNotificationIcon() {
        binding.layoutNotificationInformation.imageViewNotificationIcon
            .setImageDependingOnEventTag(cameraEvent.eventTag)
    }

    private fun setTextViews() {
        with(binding.layoutNotificationInformation) {
            val notificationType = NotificationType.getByValue(cameraEvent.name)
            textViewNotificationTitle.text = notificationType.title ?: cameraEvent.name
            textViewNotificationMessage.text =
                notificationType.getCustomMessage(cameraEvent.value) ?: cameraEvent.value
            textViewNotificationDate.isVisible = cameraEvent.date.isNotEmpty()
            textViewNotificationDate.text = cameraEvent.date
        }
    }

    private fun setListeners() {
        binding.layoutNotificationInformation.buttonDismissNotification.setOnClickListener(this)
        binding.imageButtonCloseNotification.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        dismiss()
    }
}
