package com.lawmobile.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.lawmobile.presentation.R
import com.safefleet.mobile.commons.widgets.SafeFleetClickable

class CustomSnapshotButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SafeFleetClickable(context, attrs, defStyleAttr), View.OnClickListener {

    private var buttonText: TextView
    private var buttonIcon: ImageView
    private var buttonCustomSnapshot: ImageButton

    init {
        View.inflate(context, R.layout.button_custom_snapshot, this)
        buttonText = findViewById(R.id.textViewButtonSnapshot)
        buttonIcon = findViewById(R.id.imageViewCustomSnapshot)
        buttonCustomSnapshot = findViewById(R.id.buttonCustomSnapshot)

        setOnClickListener(this)
        buttonCustomSnapshot.setOnClickListener(this)
        buttonText.setOnClickListener(this)
        buttonIcon.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onClicked?.invoke(v)
    }
}