package com.lawmobile.presentation.extensions

import android.view.View
import android.view.animation.Animation
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetClickable
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBar
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBarSettings
import java.sql.Timestamp

private val snackBarListener = object : View.OnAttachStateChangeListener{
    override fun onViewAttachedToWindow(v: View?) {
        // The interface requires to implement this method but not needed
    }

    override fun onViewDetachedFromWindow(v: View?) {
        EspressoIdlingResource.decrement()
    }
}

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    setOnClickListener {
        context.checkSession(callback, it)
    }
}

fun SafeFleetClickable.setClickListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        context.checkSession(callback, it)
    }
}

fun SafeFleetClickable.setCheckedListenerCheckConnection(callback: (View) -> Unit) {
    onChecked = { view, _ ->
        context.checkSession(callback, view)
    }
}

fun View.showErrorSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT, onRetryClick: ((View) -> Unit)? = null) {
    SafeFleetSnackBar.make(
        SafeFleetSnackBarSettings(
            this,
            message,
            duration,
            R.drawable.ic_warning,
            this.context.getColor(R.color.red)
        ), onRetryClick
    )?.apply {
        view.addOnAttachStateChangeListener(snackBarListener)
        show()
    }
}

fun View.showSuccessSnackBar(message: String) {
    SafeFleetSnackBar.make(
        SafeFleetSnackBarSettings(
            this,
            message,
            Snackbar.LENGTH_SHORT,
            R.drawable.ic_successful_white,
            context.getColor(R.color.greenSuccess)
        )
    )?.apply {
        view.addOnAttachStateChangeListener(snackBarListener)
        show()
    }
}

fun View.startAnimationIfEnabled(animation: Animation) {
    if (context.isAnimationsEnabled()) {
        startAnimation(animation)
    }
}

fun checkIfSessionIsExpired(): Boolean {
    val timeNow = Timestamp(System.currentTimeMillis())
    return (timeNow.time - BaseActivity.lastInteraction.time) > BaseActivity.MAX_TIME_SESSION
}