package com.lawmobile.presentation.extensions

import android.widget.ImageButton
import com.lawmobile.presentation.R

fun ImageButton.buttonFilterState(isActive: Boolean) {
    background = if (isActive) {
        setImageResource(R.drawable.ic_filter_white)
        androidx.core.content.ContextCompat.getDrawable(
            context,
            R.drawable.background_button_blue
        )
    } else {
        setImageResource(R.drawable.ic_filter)
        androidx.core.content.ContextCompat.getDrawable(
            context,
            R.drawable.border_rounded_blue
        )
    }
}
