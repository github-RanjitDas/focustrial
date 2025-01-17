package com.lawmobile.presentation.extensions

import android.graphics.Rect
import android.view.View
import android.view.animation.Animation
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.OnSwipeTouchListener
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetClickable
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBar
import com.safefleet.mobile.safefleet_ui.widgets.snackbar.SafeFleetSnackBarSettings

private val snackBarListener = object : View.OnAttachStateChangeListener {
    override fun onViewAttachedToWindow(v: View) {
        // The interface requires to implement this method but not needed
    }

    override fun onViewDetachedFromWindow(v: View) {
        EspressoIdlingResource.decrement()
    }
}

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    setOnClickListener {
        context.checkSession(callback, it)
    }
}

fun View.setOnTouchListenerCheckConnection(
    onClick: ((View) -> Unit)? = null,
    onSwipe: ((View) -> Unit)? = null
) {
    setOnTouchListener(
        object : OnSwipeTouchListener(context) {
            override fun onClick() {
                onClick?.let { context.checkSession(it, this@setOnTouchListenerCheckConnection) }
            }

            override fun onSwipeRight() {
                onSwipe?.invoke(this@setOnTouchListenerCheckConnection)
            }
        }
    )
}

fun View.setOnSwipeRightListener(callback: (View) -> Unit) {
    setOnTouchListener(
        object : OnSwipeTouchListener(context) {
            override fun onSwipeRight() {
                callback(this@setOnSwipeRightListener)
            }
        }
    )
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

fun View.showErrorSnackBar(
    message: String,
    duration: Int = 7000,
    onRetryClick: ((View) -> Unit)? = null
): SafeFleetSnackBar? {
    return SafeFleetSnackBar.make(
        SafeFleetSnackBarSettings(
            this,
            message,
            duration,
            R.drawable.ic_warning,
            this.context.getColor(R.color.red)
        ),
        onRetryClick
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

fun View.onSizeChange(callback: () -> Unit) {
    addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        val rect = Rect(left, top, right, bottom)
        val oldRect = Rect(oldLeft, oldTop, oldRight, oldBottom)
        if (rect.width() != oldRect.width() || rect.height() != oldRect.height()) {
            callback()
        }
    }
}
