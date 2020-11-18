package com.lawmobile.presentation.ui.base

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.NeutralAlertInformation
import com.lawmobile.presentation.extensions.createAlertMobileDataActive
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.MobileDataStatus
import dagger.android.support.DaggerAppCompatActivity
import java.sql.Timestamp
import javax.inject.Inject
import kotlin.system.exitProcess

open class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var baseViewModel: BaseViewModel

    @Inject
    lateinit var mobileDataStatus: MobileDataStatus

    private var isLiveVideoOrPlaybackActive: Boolean = false
    private lateinit var mobileDataDialog: AlertDialog
    var isRecordingVideo: Boolean = false
    var isAlertShowing = MutableLiveData<Boolean>()

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
        createMobileDataDialog()
        mobileDataStatus.observe(this, Observer(::showMobileDataDialog))
        updateLastInteraction()
    }

    private fun createMobileDataDialog() {
        val alertInformation = NeutralAlertInformation(
            R.string.mobile_data_status_title,
            R.string.mobile_data_status_message
        )
        mobileDataDialog = this.createAlertMobileDataActive(alertInformation)
    }

    private fun showMobileDataDialog(active: Boolean) {
        isAlertShowing.postValue(active)
        if (active) mobileDataDialog.show()
        else mobileDataDialog.dismiss()
    }

    override fun onStop() {
        super.onStop()
        updateLastInteraction()
    }

    override fun onRestart() {
        super.onRestart()
        if (checkIfSessionIsExpired() && this !is LoginActivity) this.createAlertSessionExpired()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (!isLiveVideoOrPlaybackActive && !isRecordingVideo && checkIfSessionIsExpired() && this !is LoginActivity) {
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

    fun updateLastInteraction() {
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