package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.DialogCustomNotificationBinding

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
        when (cameraEvent.eventTag) {
            EventTag.ERROR -> {
                binding.layoutNotificationInformation.imageViewNotificationIcon.setImageResource(R.drawable.ic_error_icon)
            }
            EventTag.WARNING -> {
                binding.layoutNotificationInformation.imageViewNotificationIcon.setImageResource(R.drawable.ic_warning_icon)
            }
            EventTag.INFORMATION -> {
                binding.layoutNotificationInformation.imageViewNotificationIcon.setImageResource(R.drawable.ic_info_icon)
            }
        }
    }

    private fun setTextViews() {
        with(binding.layoutNotificationInformation) {
            textViewNotificationTitle.text = cameraEvent.name
            textViewNotificationMessage.text = cameraEvent.value
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
