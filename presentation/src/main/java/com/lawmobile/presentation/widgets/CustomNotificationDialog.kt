package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
        setButtonText()
        setNotificationIcon()
        setListeners()
    }

    private fun setButtonText() {
        binding.layoutNotificationInformation.buttonDismissNotification.text =
            context.getString(R.string.dismiss)
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
