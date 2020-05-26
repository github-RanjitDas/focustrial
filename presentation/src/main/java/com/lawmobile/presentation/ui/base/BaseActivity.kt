package com.lawmobile.presentation.ui.base

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.WindowManager
import com.lawmobile.presentation.ui.login.LoginActivity
import dagger.android.support.DaggerAppCompatActivity
import java.sql.Timestamp
import javax.inject.Inject
import kotlin.system.exitProcess

open class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var baseViewModel: BaseViewModel
    var isLiveVideoOrPlaybackActive: Boolean = false

    fun killApp() {
        baseViewModel.deactivateCameraHotspot()
        finishAffinity()
        exitProcess(0)
    }

    fun restartApp() {
        val intent: Intent? = this.baseContext.packageManager
            .getLaunchIntentForPackage(this.baseContext.packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        updateLastInteraction()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (!isLiveVideoOrPlaybackActive && checkIfSessionIsExpired() && this !is LoginActivity) {
            return
        }
        updateLastInteraction()
    }

    fun updateLiveOrPlaybackActive(isActive: Boolean) {
        if (isActive) {
            updateLastInteraction()
        }

        isLiveVideoOrPlaybackActive = isActive
    }

    private fun updateLastInteraction(){
        lastInteraction = Timestamp(System.currentTimeMillis())
    }

    fun checkIfSessionIsExpired(): Boolean {
        val timeNow = Timestamp(System.currentTimeMillis())
        return (timeNow.time - lastInteraction.time) > MAX_TIME_SESSION
    }

    companion object {
        const val PERMISSION_FOR_LOCATION = 100
        const val MAX_TIME_SESSION = 300000
        private lateinit var lastInteraction: Timestamp
    }

}