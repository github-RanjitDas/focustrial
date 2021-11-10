package com.lawmobile.presentation.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraInfo.isOfficerLogged
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.NeutralAlertInformation
import com.lawmobile.presentation.extensions.createAlertErrorConnection
import com.lawmobile.presentation.extensions.createAlertMobileDataActive
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.security.RootedHelper
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.MobileDataStatus
import com.lawmobile.presentation.utils.WifiStatus
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()

    @Inject
    lateinit var mobileDataStatus: MobileDataStatus

    @Inject
    lateinit var wifiStatus: WifiStatus

    @Inject
    lateinit var eventsUseCase: EventsUseCase

    @Inject
    lateinit var cameraHelper: CameraHelper

    private var isLiveVideoOrPlaybackActive: Boolean = false
    var isNetworkAlertShowing = MutableLiveData<Boolean>()
    private var isWifiAlertShowing = false

    private lateinit var mobileDataDialog: AlertDialog
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        setCameraHelper()
        verifyDeviceIsNotRooted()
        setBaseObservers()
        createNetworkDialogs()
        updateLastInteraction()
        setEventsUseCase()
        setEventsListener()
    }

    private fun setCameraHelper() {
        CameraHelper.setInstance(cameraHelper)
    }

    fun getApplicationVersionText(): String {
        return if (BuildConfig.DEBUG) "Version ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
        else "Version ${BuildConfig.VERSION_NAME}"
    }

    private fun setEventsUseCase() {
        baseViewModel.setEventsUseCase(eventsUseCase)
    }

    private fun verifyDeviceIsNotRooted() {
        if (RootedHelper.isDeviceRooted(this)) {
            finish()
            return
        }
    }

    private fun setEventsListener() {
        if (isOfficerLogged && CameraInfo.cameraType == CameraType.X2)
            cameraHelper.onCameraEvent(::manageCameraEvent)
    }

    private fun manageCameraEvent(cameraEvent: CameraEvent) {
        if (cameraEvent.eventType == EventType.NOTIFICATION) {
            when (cameraEvent.name) {
                NotificationType.LOW_BATTERY.value -> onLowBattery?.invoke(cameraEvent.value?.toInt())
                NotificationType.LOW_STORAGE.value -> onLowStorage?.invoke()
            }
            runOnUiThread { createNotificationDialog(cameraEvent) }
        } else when (cameraEvent.name) {
            NotificationType.BATTERY_LEVEL.value -> {
                cameraEvent.value?.toInt()?.let {
                    onBatteryLevelChanged?.invoke(it)
                }
            }
            NotificationType.STORAGE_REMAIN.value -> {
                cameraEvent.value?.toDouble()?.let { availableStorage ->
                    onStorageLevelChanged?.invoke(availableStorage)
                }
            }
        }

        baseViewModel.saveNotificationEvent(cameraEvent)
    }

    private fun setBaseObservers() {
        mobileDataStatus.observe(this, ::showMobileDataDialog)
        wifiStatus.observe(this, ::showWifiOffDialog)
    }

    private fun showWifiOffDialog(active: Boolean) {
        if (!active && !isWifiAlertShowing) {
            isWifiAlertShowing = true
            isNetworkAlertShowing.postValue(true)
            createAlertErrorConnection()
        }
    }

    private fun createNetworkDialogs() {
        val alertInformation = NeutralAlertInformation(
            R.string.mobile_data_status_title,
            R.string.mobile_data_status_message
        )
        mobileDataDialog = createAlertMobileDataActive(alertInformation)
    }

    private fun showMobileDataDialog(active: Boolean) {
        if (!isWifiAlertShowing) {
            isNetworkAlertShowing.postValue(active)
            if (active) mobileDataDialog.show()
            else mobileDataDialog.dismiss()
        }
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
        if (isActive) updateLastInteraction()
        isLiveVideoOrPlaybackActive = isActive
    }

    fun updateLastInteraction() {
        lastInteraction = Timestamp(System.currentTimeMillis())
    }

    fun showLoadingDialog() {
        EspressoIdlingResource.increment()
        loadingDialog = createAlertProgress()
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
        EspressoIdlingResource.decrement()
    }

    fun isInPortraitMode() =
        resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    companion object {
        var onBatteryLevelChanged: ((Int) -> Unit)? = null
        var onStorageLevelChanged: ((Double) -> Unit)? = null
        var onLowBattery: ((Int?) -> Unit)? = null
        var onLowStorage: (() -> Unit)? = null

        lateinit var lastInteraction: Timestamp
        var isRecordingVideo: Boolean = false
        var isRecordingAudio: Boolean = false

        const val PERMISSION_FOR_LOCATION = 100
        const val MAX_TIME_SESSION = 300000

        fun checkIfSessionIsExpired(): Boolean {
            val timeNow = Timestamp(System.currentTimeMillis())
            return (timeNow.time - BaseActivity.lastInteraction.time) > BaseActivity.MAX_TIME_SESSION
        }
    }
}
