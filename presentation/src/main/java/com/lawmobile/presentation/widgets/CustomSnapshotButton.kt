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

class CustomSnapshotButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SafeFleetClickable(context, attrs, defStyleAttr), View.OnClickListener {

    private var buttonText: TextView
    private var buttonIcon: ImageView
    private var buttonCustomSnapshot: ImageButton
    private var disableView: View

    init {
        View.inflate(context, R.layout.button_custom_snapshot, this)
        buttonText = findViewById(R.id.textViewButtonSnapshot)
        buttonIcon = findViewById(R.id.imageViewCustomSnapshot)
        buttonCustomSnapshot = findViewById(R.id.buttonCustomSnapshot)
        disableView = findViewById(R.id.viewDisableSnapshotButton)

        setOnClickListener(this)
        buttonCustomSnapshot.setOnClickListener(this)
        buttonText.setOnClickListener(this)
        buttonIcon.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onClicked?.invoke(v)
    }

    fun setEnabledState(enable: Boolean) {
        disableView.isVisible = !enable
    }
}
