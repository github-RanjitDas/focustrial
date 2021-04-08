package com.lawmobile.presentation.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraInfo.isOfficerLogged
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.NeutralAlertInformation
import com.lawmobile.presentation.extensions.checkIfSessionIsExpired
import com.lawmobile.presentation.extensions.createAlertMobileDataActive
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.security.RootedHelper
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.MobileDataStatus
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by viewModels()

    @Inject
    lateinit var mobileDataStatus: MobileDataStatus

    @Inject
    lateinit var eventsUseCase: EventsUseCase

    private var isLiveVideoOrPlaybackActive: Boolean = false
    private lateinit var mobileDataDialog: AlertDialog
    var isMobileDataAlertShowing = MutableLiveData<Boolean>()
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        verifyDeviceIsNotRooted()
        setBaseObservers()
        createMobileDataDialog()
        updateLastInteraction()
        setEventsUseCase()
        setEventsListener()
    }

    private fun setEventsUseCase() {
        viewModel.setEventsUseCase(eventsUseCase)
    }

    private fun verifyDeviceIsNotRooted() {
        if (RootedHelper.isDeviceRooted(this)) {
            finish()
            return
        }
    }

    private fun setEventsListener() {
        if (isOfficerLogged && CameraInfo.cameraType == CameraType.X2)
            CameraHelper.getInstance().onCameraEvent(::manageCameraEvent)
    }

    private fun manageCameraEvent(cameraEvent: CameraEvent) {
        if (cameraEvent.eventType == EventType.NOTIFICATION) {
            runOnUiThread { createNotificationDialog(cameraEvent) }
            when (cameraEvent.name) {
                NotificationType.LOW_BATTERY.value -> onLowBattery?.invoke()
                NotificationType.LOW_STORAGE.value -> onLowStorage?.invoke()
            }
        } else when (cameraEvent.name) {
            NotificationType.BATTERY_LEVEL.value -> {
                cameraEvent.value?.toInt()?.let {
                    onBatteryLevelChanged?.invoke(it)
                }
            }
            NotificationType.STORAGE_REMAIN.value -> {
                cameraEvent.value?.toDouble()?.let {
                    onStorageLevelChanged?.invoke(it)
                }
            }
        }

        viewModel.saveNotificationEvent(cameraEvent)
    }

    private fun setBaseObservers() {
        mobileDataStatus.observe(this, Observer(::showMobileDataDialog))
    }

    private fun createMobileDataDialog() {
        val alertInformation = NeutralAlertInformation(
            R.string.mobile_data_status_title,
            R.string.mobile_data_status_message
        )
        mobileDataDialog = createAlertMobileDataActive(alertInformation)
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
        if (isActive) updateLastInteraction()
        isLiveVideoOrPlaybackActive = isActive
    }

    fun updateLastInteraction() {
        lastInteraction = Timestamp(System.currentTimeMillis())
    }

    fun showLoadingDialog() {
        EspressoIdlingResource.increment()
        loadingDialog = this.createAlertProgress()
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
        var onLowBattery: (() -> Unit)? = null
        var onLowStorage: (() -> Unit)? = null

        lateinit var lastInteraction: Timestamp
        var isRecordingVideo: Boolean = false

        const val PERMISSION_FOR_LOCATION = 100
        const val MAX_TIME_SESSION = 300000
    }
}
