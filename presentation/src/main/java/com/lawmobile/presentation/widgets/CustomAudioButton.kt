package com.lawmobile.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetClickable

class CustomAudioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SafeFleetClickable(context, attrs, defStyleAttr), View.OnClickListener {

    private var buttonText: TextView
    private var buttonIcon: ImageView
    private var buttonCustomAudio: ImageButton
    private var disableView: View

    init {
        View.inflate(context, R.layout.button_custom_audio, this)
        buttonText = findViewById(R.id.textViewButtonAudio)
        buttonIcon = findViewById(R.id.imageViewCustomAudio)
        buttonCustomAudio = findViewById(R.id.buttonCustomAudio)
        disableView = findViewById(R.id.viewDisableButton)

        setOnClickListener(this)
        buttonCustomAudio.setOnClickListener(this)
        buttonText.setOnClickListener(this)
        buttonIcon.setOnClickListener(this)
    }

    override fun isActivated(): Boolean {
        return buttonCustomAudio.isActivated
    }

    override fun setActivated(activated: Boolean) {
        buttonCustomAudio.isActivated = activated
        changeButtonState()
    }

    override fun onClick(v: View) {
        onClicked?.invoke(v)
        isActivated = !isActivated

        changeButtonState()
    }

    fun setEnabledState(enable: Boolean) {
        disableView.isVisible = !enable
    }

    private fun changeButtonState() {
        with(buttonCustomAudio) {
            if (isActivated) {
                setBackgroundResource(R.drawable.background_recording_active)
                buttonIcon.setImageResource(R.drawable.ic_record_active)
                buttonText.text = context.getString(R.string.stop_audio)
                buttonText.setTextColor(context.getColor(R.color.red))
            } else {
                setBackgroundResource(R.drawable.background_live_view_buttons)
                buttonIcon.setImageResource(R.drawable.ic_audio_white)
                buttonText.text = context.getString(R.string.record_audio)
                buttonText.setTextColor(context.getColor(R.color.white))
            }
        }
    }
}
