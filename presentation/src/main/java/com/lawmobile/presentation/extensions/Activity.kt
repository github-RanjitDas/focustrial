package com.lawmobile.presentation.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun BaseActivity.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun BaseActivity.verifyForAskingPermission(permission: String, requestCode: Int) {
    if (!isPermissionGranted(permission)) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
}

@SuppressLint("InflateParams")
fun BaseActivity.createAlertProgress(textLoading: Int = R.string.loading_wait): AlertDialog {
    val builder = AlertDialog.Builder(this)
    val view = layoutInflater.inflate(R.layout.loading_dialog, null)
    val textView = view.findViewById<TextView>(R.id.textViewLoading)
    textView.setText(textLoading)
    builder.apply {
        setView(view)
        setCancelable(false)
    }
    val dialog = builder.create()
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)

    return dialog
}

fun BaseActivity.runWithDelay(
    delay: Long = 200,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    callback: () -> Unit
) {
    lifecycleScope.launch(dispatcher) {
        delay(delay)
        callback()
    }
}

fun <T> BaseActivity.activityCollect(flow: Flow<T>, callback: (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { callback(it) }
        }
    }
}

fun Activity.isDeXEnabled(): Boolean {
    val config = resources.configuration
    return try {
        val configClass = config::class.java
        configClass.getField(DESKTOP_MODE_ENABLED).getInt(configClass) ==
            configClass.getField(SEM_DESKTOP_MODE_ENABLED).getInt(config)
    } catch (e: Exception) {
        false
    }
}

fun Activity.changeOrientation() {
    if (isInPortraitMode()) setLandscapeOrientation()
    else setPortraitOrientation()
}

fun Activity.setLandscapeOrientation() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity.setPortraitOrientation() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Activity.isInPortraitMode(): Boolean =
    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Activity.toggleDeXFullScreen() {
    if (!isDeXEnabled()) changeOrientation()
}

private const val DESKTOP_MODE_ENABLED = "SEM_DESKTOP_MODE_ENABLED"
private const val SEM_DESKTOP_MODE_ENABLED = "semDesktopModeEnabled"
