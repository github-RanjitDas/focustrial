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
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.connectivity.MobileDataStatus
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.connectivity.WifiStatus
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.checkActivityBeforeDialog
import com.lawmobile.presentation.extensions.createAlertErrorConnection
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.extensions.createLowWifiSignalAlert
import com.lawmobile.presentation.extensions.createMobileDataAlert
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.security.RootedHelper
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.NewRelicLogger
import com.lawmobile.presentation.utils.checkIfSessionIsExpired
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()

    @Inject
    lateinit var mobileDataStatus: MobileDataStatus

    @Inject
    lateinit var wifiStatus: WifiStatus

    @Inject
    lateinit var eventsUseCase: EventsUseCase

    @Inject
    lateinit var wifiHelper: WifiHelper

    @Inject
    lateinit var cameraHelper: CameraHelper

    abstract val parentTag: String

    private var isLiveVideoOrPlaybackActive: Boolean = false
    var isNetworkAlertShowing = MutableLiveData<Boolean>()
    private var isWifiAlertShowing = false
    private var isLowSignalAlertShowing = false

    private lateinit var lowSignalDialog: AlertDialog
    private lateinit var mobileDataDialog: AlertDialog
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        setCameraHelper()
        verifyDeviceIsNotRooted()
        createNetworkDialogs()
        setBaseObservers()
        updateLastInteraction()
        setEventsUseCase()
        setEventsListener()
    }

    override fun onResume() {
        super.onResume()
        NewRelicLogger.updateActiveParent(parentTag)
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
        if (CameraInfo.isOfficerLogged && CameraInfo.cameraType.isX2())
            cameraHelper.onCameraEvent(::manageCameraEvent)
    }

    private fun manageCameraEvent(cameraEvent: CameraEvent) {
        runOnUiThread {
            if (cameraEvent.eventType == EventType.NOTIFICATION) handleNotificationEvent(cameraEvent)
            else handleInformationEvent(cameraEvent)
        }

        baseViewModel.saveNotificationEvent(cameraEvent)
    }

    private fun handleNotificationEvent(cameraEvent: CameraEvent) {
        when (cameraEvent.name) {
            NotificationType.LOW_BATTERY.value -> onLowBattery?.invoke(cameraEvent.value?.toInt())
            NotificationType.LOW_STORAGE.value -> onLowStorage?.invoke()
        }
        checkActivityBeforeDialog { createNotificationDialog(cameraEvent) }
    }

    private fun handleInformationEvent(cameraEvent: CameraEvent) {
        when (cameraEvent.name) {
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
            NotificationType.VIDEO_RECORDING_STARTED.value -> onVideoRecordingStatus?.invoke(true)
            NotificationType.VIDEO_RECORDING_STOPPED.value -> onVideoRecordingStatus?.invoke(false)
        }
    }

    private fun createNetworkDialogs() {
        mobileDataDialog = createMobileDataAlert()
        lowSignalDialog = createLowWifiSignalAlert()
    }

    private fun setBaseObservers() {
        // TODO: Commented to enable mobile-data for SSO flow.
        // mobileDataStatus.observe(this, ::showMobileDataDialog)
        wifiStatus.observe(this, ::showWifiOffDialog)
        activityCollect(wifiHelper.isWifiSignalLow, ::showLowWifiSignalAlert)
    }

    private fun showLowWifiSignalAlert(isLow: Boolean) {
        if (!isWifiAlertShowing) {
            isNetworkAlertShowing.postValue(isLow)
            if (isLow) checkActivityBeforeDialog(lowSignalDialog::show)
            else lowSignalDialog.hide()
        } else lowSignalDialog.hide()
    }

    private fun showWifiOffDialog(active: Boolean) {
        if (!active && !isWifiAlertShowing) {
            isWifiAlertShowing = true
            isNetworkAlertShowing.postValue(true)
            checkActivityBeforeDialog(::createAlertErrorConnection)
        }
    }

    private fun showMobileDataDialog(active: Boolean) {
        if (!isWifiAlertShowing) {
            isNetworkAlertShowing.postValue(active)
            if (active) checkActivityBeforeDialog(mobileDataDialog::show)
            else mobileDataDialog.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        updateLastInteraction()
    }

    override fun onRestart() {
        super.onRestart()
        if (checkIfSessionIsExpired() && CameraInfo.isOfficerLogged) createAlertSessionExpired()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (!isLiveVideoOrPlaybackActive && !isRecordingVideo && checkIfSessionIsExpired() && CameraInfo.isOfficerLogged) {
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
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            hideLoadingDialog()
        }
        loadingDialog = createAlertProgress()
        loadingDialog?.show()
    }

    fun showLoadingDialog(text: Int) {
        EspressoIdlingResource.increment()
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            hideLoadingDialog()
        }
        loadingDialog = createAlertProgress(text)
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

        var onVideoRecordingStatus: ((Boolean) -> Unit)? = null

        lateinit var lastInteraction: Timestamp
        var isRecordingVideo: Boolean = false
        var isRecordingAudio: Boolean = false

        const val PERMISSION_FOR_LOCATION = 100
        const val MAX_TIME_SESSION = 300000
    }
}
