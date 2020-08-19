package com.lawmobile.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.lawmobile.presentation.R
import com.safefleet.mobile.commons.widgets.SafeFleetClickable
import kotlinx.android.synthetic.main.button_custom_snapshot.view.*

class CustomSnapshotButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SafeFleetClickable(context, attrs, defStyleAttr), View.OnClickListener {

    init {
        View.inflate(context, R.layout.button_custom_snapshot, this)
        setOnClickListener(this)
        buttonCustomSnapshot.setOnClickListener(this)
        textViewButtonSnapshot.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onClicked?.invoke(v)
    }
}