package com.lawmobile.presentation.extensions

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
