package com.lawmobile.presentation.widgets.snackbar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.findSuitableParent

class SafeFleetSnackBar(
    parent: ViewGroup,
    content: SafeFleetSnackBarView
) : BaseTransientBottomBar<SafeFleetSnackBar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            safeFleetSnackBarSettings: SafeFleetSnackBarSettings
        ): SafeFleetSnackBar? {
            safeFleetSnackBarSettings.run {
                val parent = view.findSuitableParent()
                    ?: throw IllegalArgumentException(
                        "No suitable parent found from the given view. Please provide a valid view."
                    )

                try {
                    val customView = LayoutInflater.from(view.context).inflate(
                        R.layout.safefleet_snackbar,
                        parent,
                        false
                    ) as SafeFleetSnackBarView

                    customView.tvMsg.text = message
                    customView.imLeft.setImageResource(icon)
                    customView.layRoot.setBackgroundColor(bg_color)

                    return SafeFleetSnackBar(
                        parent,
                        customView
                    ).setDuration(duration)
                } catch (e: Exception) {
                    Log.v("exception ", e.message ?: "unknown exception")
                }

                return null
            }
        }
    }
}
