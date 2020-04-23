package com.lawmobile.presentation.extensions

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lawmobile.presentation.ui.base.BaseActivity

fun BaseActivity.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun BaseActivity.verifyForAskingPermission(permission: String, requestCode: Int) {
    if (!isPermissionGranted(permission)){
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
}