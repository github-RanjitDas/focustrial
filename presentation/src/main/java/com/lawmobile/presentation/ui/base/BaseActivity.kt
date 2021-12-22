package com.lawmobile.presentation.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraInfo.isOfficerLogged
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.connectivity.MobileDataStatus
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.connectivity.WifiStatus
import com.lawmobile.presentation.extensions.createAlertErrorConnection
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.extensions.createLowWifiSignalAlert
import com.lawmobile.presentation.extensions.createMobileDataAlert
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.security.RootedHelper
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.EspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
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
    lateinit var wifiHelper: WifiHelper

    @Inject
    lateinit var cameraHelper: CameraHelper

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
        if (cameraEvent.eventType == EventType.NOTIFICATION) handleNotificationEvent(cameraEvent)
        else handleInformationEvent(cameraEvent)
        baseViewModel.saveNotificationEvent(cameraEvent)
    }

    private fun handleNotificationEvent(cameraEvent: CameraEvent) {
        when (cameraEvent.name) {
            NotificationType.LOW_BATTERY.value -> onLowBattery?.invoke(cameraEvent.value?.toInt())
            NotificationType.LOW_STORAGE.value -> onLowStorage?.invoke()
        }
        runOnUiThread {
            if (!isFinishing) createNotificationDialog(cameraEvent)
        }
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
        }
    }

    private fun createNetworkDialogs() {
        mobileDataDialog = createMobileDataAlert()
        lowSignalDialog = createLowWifiSignalAlert()
    }

    private fun setBaseObservers() {
        mobileDataStatus.observe(this, ::showMobileDataDialog)
        wifiStatus.observe(this, ::showWifiOffDialog)
        observeWifiSignalLevel()
    }

    private fun observeWifiSignalLevel() {
        lifecycleScope.launchWhenResumed {
            withContext(Dispatchers.IO) {
                wifiHelper.isWifiSignalLow.collect {
                    showLowWifiSignalAlert(it)
                }
            }
        }
    }

    private fun showLowWifiSignalAlert(isLow: Boolean) {
        runOnUiThread {
            if (!isWifiAlertShowing) {
                isNetworkAlertShowing.postValue(isLow)
                if (isLow) lowSignalDialog.show()
                else lowSignalDialog.hide()
            } else lowSignalDialog.hide()
        }
    }

    private fun showWifiOffDialog(active: Boolean) {
        if (!active && !isWifiAlertShowing) {
            isWifiAlertShowing = true
            isNetworkAlertShowing.postValue(true)
            createAlertErrorConnection()
        }
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
        if (checkIfSessionIsExpired() && this !is LoginX1Activity) this.createAlertSessionExpired()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (!isLiveVideoOrPlaybackActive && !isRecordingVideo && checkIfSessionIsExpired() && this !is LoginX1Activity) {
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
            return (timeNow.time - lastInteraction.time) > MAX_TIME_SESSION
        }
    }
}
