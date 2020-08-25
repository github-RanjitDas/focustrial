package com.lawmobile.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.lawmobile.presentation.R
import com.safefleet.mobile.commons.widgets.SafeFleetClickable
import kotlinx.android.synthetic.main.button_custom_record.view.*

class CustomRecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SafeFleetClickable(context, attrs, defStyleAttr), View.OnClickListener {

    private var buttonText: TextView

    init {
        View.inflate(context, R.layout.button_custom_record, this)
        buttonText = findViewById(R.id.textViewButtonRecord)
        setOnClickListener(this)
        buttonCustomRecord.setOnClickListener(this)
        textViewButtonRecord.setOnClickListener(this)
    }

    override fun isActivated(): Boolean {
        return buttonCustomRecord.isActivated
    }

    override fun setActivated(activated: Boolean) {
        buttonCustomRecord.isActivated = activated
        changeButtonState()
    }

    override fun onClick(v: View) {
        onClicked?.invoke(v)
        isActivated = !isActivated

        changeButtonState()
    }

    private fun changeButtonState() {
        val density = resources.displayMetrics.density
        with(buttonCustomRecord) {
            if (isActivated) {
                setBackgroundResource(R.drawable.background_recording_active)
                setImageResource(R.drawable.ic_record_active)
                buttonText.text = context.getString(R.string.stop)
                buttonText.setTextColor(context.getColor(R.color.red))

                setPadding(
                    (density * 51 + 0.5f).toInt(),
                    (density * 33).toInt(),
                    (density * 51 + 0.5f).toInt(),
                    (density * 50).toInt()
                )
            } else {
                setBackgroundResource(R.drawable.background_live_view_buttons)
                setImageResource(R.drawable.ic_video)
                buttonText.text = context.getString(R.string.record_video)
                buttonText.setTextColor(context.getColor(R.color.white))

                setPadding(
                    (density * 51 + 0.5f).toInt(),
                    (density * 39).toInt(),
                    (density * 51 + 0.5f).toInt(),
                    (density * 59).toInt()
                )
            }
        }
    }
}