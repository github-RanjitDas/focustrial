package com.lawmobile.presentation.ui.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Process
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.NeutralAlertInformation
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.MobileDataStatus
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var baseViewModel: BaseViewModel

    @Inject
    lateinit var mobileDataStatus: MobileDataStatus

    private var isLiveVideoOrPlaybackActive: Boolean = false
    private lateinit var mobileDataDialog: AlertDialog
    var isRecordingVideo: Boolean = false
    var isMobileDataAlertShowing = MutableLiveData<Boolean>()
    private var loadingDialog: AlertDialog? = null
    private var isLoading = false

    fun logout() {
        baseViewModel.deactivateCameraHotspot()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
        baseViewModel.isWaitFinishedLiveData.observe(this, Observer(::handleTimeout))
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
        isMobileDataAlertShowing.postValue(active)
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

    fun showLoadingDialog() {
        isLoading = true
        baseViewModel.waitToFinish(LOADING_TIMEOUT)
        EspressoIdlingResource.increment()
        loadingDialog = this.createAlertProgress()
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        isLoading = false
        baseViewModel.cancelWait()
        loadingDialog?.dismiss()
        loadingDialog = null
        EspressoIdlingResource.decrement()
    }

    fun isInPortraitMode() =
        resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    private fun handleTimeout(timedOut: Boolean) {
        if (timedOut && isLoading) {
            hideLoadingDialog()
            finish()
            this.showToast(getString(R.string.loading_files_error), Toast.LENGTH_SHORT)
        }
    }

    companion object {
        const val LOADING_TIMEOUT = 20000L
        const val PERMISSION_FOR_LOCATION = 100
        const val MAX_TIME_SESSION = 300000
        lateinit var lastInteraction: Timestamp
    }
}