package com.lawmobile.presentation.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lawmobile.presentation.ui.base.BaseActivity

fun hasPermissions(context: Context): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }

    return true
}

val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.NEARBY_WIFI_DEVICES,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    }
} else {
    arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
}

fun BaseActivity.requestPermissions(activity: Activity, requestCode: Int) {
    val permissionArray = getNonGrantedPermissions(activity, permissions)
    if (permissionArray != null) {
        ActivityCompat.requestPermissions(activity, permissionArray, requestCode)
    }
}

private fun getNonGrantedPermissions(
    context: Context,
    permissions: Array<String>
): Array<String?>? {
    val permissionList: ArrayList<String> = ArrayList()

    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(permission)
        }
    }
    if (permissionList.size > 0) {
        val permissionArray = arrayOfNulls<String>(permissionList.size)
        permissionList.toArray(permissionArray)
        return permissionArray
    }
    return null
}

fun BaseActivity.shouldShowPermissionRationale(
    activity: Activity,
    permissions: Array<out String>?
): Boolean {
    if (permissions == null) {
        return false
    }
    for (permission in permissions) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            return true
        }
    }
    return false
}

fun BaseActivity.shouldShowPermissionRationale(activity: Activity): Boolean {
    for (permission in permissions) {
        if (activity.shouldShowRequestPermissionRationale(permission)) {
            return true
        }
    }
    return false
}

fun isGPSActive(context: Context): Boolean {
    var gpsEnable = false
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        // no need to turn On GPS for android13 and above.
        gpsEnable = true
    }
    return gpsEnable
}

const val IS_FIRST_TIME_ASKING_PERMISSION = "IS_FIRST_TIME_ASKING_PERMISSION"
const val PREFS_PERMISSIONS = "PREFS_PERMISSIONS"
