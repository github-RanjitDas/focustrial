package com.lawmobile.presentation.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object RootedHelper {

    fun isDeviceRooted(context: Context): Boolean {
        return checkRootMethodTestKeys() ||
                checkRootMethodPathsWithSu() ||
                checkRootMethod3() ||
                checkRootMethod4() ||
                checkMethodPackages(context)
    }

    private fun checkRootMethodTestKeys(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethodPathsWithSu(): Boolean {
        val paths = arrayOf(
            "/sbin/su",
            "/su/bin/su",
            "/data/local/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/data/local/xbin/su",
            "/system/xbin/busybox",
            "/system/xbin/daemonsu",
            "/system/bin/failsafe/su",
            "/system/app/Superuser.apk",
            "/system/etc/init.d/99SuperSUDaemon",
            "/dev/com.koushikdutta.superuser.daemon/",
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val inputBuffer = BufferedReader(InputStreamReader(process.inputStream))
            inputBuffer.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    private fun checkRootMethod4(): Boolean {
        System.getenv("PATH")?.split(":")?.forEach { path ->
            if (File(path, "su").exists()) return true
        }
        return false
    }

    private fun checkMethodPackages(context: Context): Boolean {
        val paths = arrayListOf(
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.noshufou.android.su",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.topjohnwu.magisk"
        )

        for (path in paths) {
            if (isPackageExisted(path,context)) return true
        }
        return false
    }

    private fun isPackageExisted(targetPackage: String, context: Context): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager = context.packageManager
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName == targetPackage) return true
        }
        return false
    }
}