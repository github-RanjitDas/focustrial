package com.lawmobile.presentation.ui.live

import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.live.LiveActivityViewModel.Companion.SCALE_BYTES
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CatalogTypes
import com.safefleet.mobile.commons.animations.Animations
import com.safefleet.mobile.commons.helpers.Event
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import com.safefleet.mobile.commons.widgets.linearProgressBar.SafeFleetLinearProgressBarBehavior.ASCENDANT
import com.safefleet.mobile.commons.widgets.linearProgressBar.SafeFleetLinearProgressBarBehavior.DESCENDANT
import com.safefleet.mobile.commons.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges.*
import kotlinx.android.synthetic.main.activity_live_view.*
import kotlinx.android.synthetic.main.live_view_app_bar.*

class LiveActivity : BaseActivity() {

    private val liveActivityViewModel: LiveActivityViewModel by viewModels()
    private val blinkAnimation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
    private var isViewLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_view)
        overridePendingTransition(0, 0)

        if (isInPortraitMode()) {
            setOfficerName()
            configureObservers()
            waitUntilViewIsLoadedToGetInformation()
            animateAppBar()
            animateContainer()
            turnOnLiveView()
        }
    }

    private fun isInPortraitMode() =
        resources.configuration.orientation == SCREEN_ORIENTATION_PORTRAIT

    private fun setOfficerName() {
        textViewOfficerName.text = CameraInfo.officerName.split(" ")[0]
        textViewOfficerLastName.text = CameraInfo.officerName.split(" ")[1]
    }

    override fun onResume() {
        super.onResume()
        updateLiveOrPlaybackActive(buttonSwitchLiveView.isActivated)
        setUrlLive()
        startLiveVideoView()
        configureListeners()
        if (!cameFromLandscape) refreshActivityInformation(isViewLoaded)
        else cameFromLandscape = false
    }

    private fun waitUntilViewIsLoadedToGetInformation() {
        liveActivityViewModel.waitToFinish(VIEW_LOADING_TIME)
    }

    private fun animateAppBar() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_anim).apply {
            startOffset = 0
        }
        liveViewAppBar.startAnimation(animation)
    }

    private fun animateContainer() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right).apply {
            startOffset = 300
        }
        liveViewContainerLayout.startAnimation(animation)
    }

    private fun turnOnLiveView() {
        buttonSwitchLiveView.isActivated = true
    }

    private fun configureListeners() {
        toggleFullScreenLiveView.setOnClickListenerCheckConnection {
            changeOrientationLive()
        }

        buttonSnapshot.setClickListenerCheckConnection {
            showShadowLoading(getString(R.string.taking_picture))
            activateSwitch(true)
            liveActivityViewModel.takePhoto()
        }

        buttonRecord.setClickListenerCheckConnection {
            if (!isRecordingVideo) {
                showShadowLoading(getString(R.string.starting_recording))
            } else {
                showShadowLoading(getString(R.string.stopping_recording))
            }
            buttonSwitchLiveView.isActivated = true
            activateSwitch(true)
            manageRecordingVideo()
        }

        buttonSwitchLiveView.setClickListenerCheckConnection {
            activateSwitch(buttonSwitchLiveView.isActivated)
        }

        buttonSnapshotList.setOnClickListenerCheckConnection {
            startFileListIntent(SNAPSHOT_LIST)
        }

        buttonVideoList.setOnClickListenerCheckConnection {
            startFileListIntent(VIDEO_LIST)
        }

        buttonOpenHelpPage.setOnClickListenerCheckConnection {
            val intent = Intent(this, HelpPageActivity::class.java)
            intent.putExtra("LiveActivity", true)
            startActivity(intent)
        }

        buttonLogout.setOnClickListenerCheckConnection {
            this.createAlertConfirmAppExit()
        }
    }

    private fun refreshActivityInformation(isViewLoaded: Boolean) {
        if (isViewLoaded) liveActivityViewModel.getBatteryLevel()
    }

    private fun startFileListIntent(fileType: String) {
        updateLiveOrPlaybackActive(false)
        val fileListIntent = Intent(this, FileListActivity::class.java)
        fileListIntent.putExtra(FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
    }

    private fun activateSwitch(isActive: Boolean) {
        updateLiveOrPlaybackActive(isActive)
        changeVisibilityView(isActive)
        toggleFullScreenLiveView.isClickable = isActive
    }

    private fun changeVisibilityView(isVisible: Boolean) {
        if (isVisible) liveStreamingView.setBackgroundResource(R.color.transparent)
        else liveStreamingView.setBackgroundResource(R.color.black)
    }

    private fun configureObservers() {
        liveActivityViewModel.isWaitFinishedLiveData
            .observe(this, Observer(::startRetrievingInformation))
        liveActivityViewModel.catalogInfoLiveData
            .observe(this, Observer(::setCatalogInfo))
        liveActivityViewModel.batteryLevelLiveData
            .observe(this, Observer(::setBatteryLevel))
        liveActivityViewModel.storageLiveData
            .observe(this, Observer(::setStorageLevels))
        liveActivityViewModel.resultRecordVideoLiveData
            .observe(this, Observer(::manageResultInRecordingVideo))
        liveActivityViewModel.resultStopVideoLiveData
            .observe(this, Observer(::manageResultInRecordingVideo))
        liveActivityViewModel.resultTakePhotoLiveData
            .observe(this, Observer(::manageResultTakePhoto))
    }

    private fun startRetrievingInformation(isViewLoaded: Boolean) {
        if (isViewLoaded && CameraInfo.events.isEmpty()) liveActivityViewModel.getCatalogInfo()
        this.isViewLoaded = isViewLoaded
    }

    private fun setCatalogInfo(catalogInfoList: Result<List<CameraConnectCatalog>>) {
        with(catalogInfoList) {
            doIfSuccess { catalogInfoList ->
                val eventNames =
                    catalogInfoList.filter { it.type == CatalogTypes.EVENT.value }
                CameraInfo.events.addAll(eventNames)
            }
            doIfError {
                liveViewAppBar.showErrorSnackBar(getString(R.string.catalog_error))
            }
        }
        liveActivityViewModel.getBatteryLevel()
    }

    private fun setBatteryLevel(result: Event<Result<Int>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess { batteryPercent ->
                progressBatteryLevel.setProgress(batteryPercent, DESCENDANT)
                setColorInBattery(batteryPercent)
                setTextInProgressBattery(batteryPercent)
            }
            doIfError {
                liveViewAppBar.showErrorSnackBar(getString(R.string.battery_level_error))
            }
        }
        liveActivityViewModel.getStorageLevels()
    }

    private fun setColorInBattery(batteryPercent: Int) {
        when (batteryPercent) {
            in LOW_DESCENDANT_RANGE.value -> {
                imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(this@LiveActivity, R.color.red)
                imageViewBattery.startAnimationIfEnabled(blinkAnimation)
                createAlertForInformationCamera(
                    R.string.battery_alert_title,
                    R.string.battery_alert_description
                )
            }
            in MEDIUM_DESCENDANT_RANGE.value -> {
                imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(this@LiveActivity, R.color.red)
                imageViewBattery.clearAnimation()
            }
            else -> {
                imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(this@LiveActivity, R.color.darkBlue)
                imageViewBattery.clearAnimation()
            }
        }
    }

    private fun setTextInProgressBattery(batteryPercent: Int) {
        val hoursLeft =
            ((batteryPercent * BATTERY_TOTAL_HOURS) / TOTAL_PERCENTAGE).toString().subSequence(0, 3)
        val textBatteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(getString(R.string.battery_percent, batteryPercent, hoursLeft), 0)
        else getString(R.string.battery_percent, batteryPercent, hoursLeft)
        textViewBatteryPercent.text = textBatteryPercent
    }

    private fun setStorageLevels(result: Event<Result<List<Double>>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                setColorInStorageLevel(it)
                setTextStorageLevel(it)
            }
            doIfError {
                val default = listOf(60.0, 0.0, 60.0)
                setColorInStorageLevel(default)
                setTextStorageLevel(default)
                liveViewAppBar.showErrorSnackBar(getString(R.string.storage_level_error))
            }
        }
        EspressoIdlingResource.decrement()
    }

    private fun setColorInStorageLevel(information: List<Double>) {
        val remainingPercent =
            TOTAL_PERCENTAGE - ((information[FREE_STORAGE_POSITION] * TOTAL_PERCENTAGE) / information[TOTAL_STORAGE_POSITION])
        progressStorageLevel.setProgress(
            remainingPercent.toInt(),
            ASCENDANT
        )

        if (remainingPercent.toInt() in HIGH_ASCENDANT_RANGE.value) {
            imageViewStorage.backgroundTintList =
                ContextCompat.getColorStateList(this@LiveActivity, R.color.red)
            imageViewStorage.startAnimationIfEnabled(blinkAnimation)
        } else {
            imageViewStorage.backgroundTintList =
                ContextCompat.getColorStateList(this@LiveActivity, R.color.darkBlue)
        }

        if (remainingPercent.toInt() == PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY) {
            createAlertForInformationCamera(
                R.string.storage_alert_title,
                R.string.storage_alert_description
            )
        }
    }

    private fun setTextStorageLevel(information: List<Double>) {
        val textToStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getStringStorageLevel(information), 0)
        } else {
            getStringStorageLevel(information)
        }

        textViewStorageLevels.text = textToStorage
    }

    private fun getStringStorageLevel(information: List<Double>): String {
        val used = information[USED_STORAGE_POSITION]
        val free = information[FREE_STORAGE_POSITION]
        var usedFormat = String.format("%.0f", used) + " MB"
        var freeFormat = String.format("%.0f", free) + " MB"
        if (used >= SCALE_BYTES) {
            usedFormat = String.format("%.1f", used / SCALE_BYTES) + " GB"
        }

        if (free >= SCALE_BYTES) {
            freeFormat = String.format("%.1f", free / SCALE_BYTES) + " GB"
        }

        return getString(R.string.storage_level, usedFormat, freeFormat)
    }

    private fun createAlertForInformationCamera(title: Int, message: Int) {
        val alertInformation = AlertInformation(
            title = title,
            message = message,
            onClickPositiveButton = { dialogInterface ->
                dialogInterface.dismiss()
            })
        this.createAlertInformation(alertInformation)
    }

    private fun manageResultTakePhoto(result: Event<Result<Unit>>) {
        hideShadowLoading()
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                liveActivityViewModel.playSoundTakePhoto()
                liveViewAppBar.showSuccessSnackBar(getString(R.string.live_view_take_photo_success))
            }
            doIfError {
                liveViewAppBar.showErrorSnackBar(getString(R.string.live_view_take_photo_failed))
            }
        }
    }

    private fun hideShadowLoading() {
        EspressoIdlingResource.decrement()
        viewLiveStreamingShadow.isVisible = false
        viewDisableButtons.isVisible = false
        viewLoading.isVisible = false
        textViewLoading.isVisible = false
    }

    private fun showShadowLoading(message: String) {
        EspressoIdlingResource.increment()
        buttonSwitchLiveView.isActivated = true
        viewLiveStreamingShadow.isVisible = true
        viewDisableButtons.isVisible = true
        viewLoading.isVisible = true
        textViewLoading.isVisible = true
        textViewLoading.text = message
    }

    private fun manageRecordingVideo() {
        if (isRecordingVideo) {
            liveActivityViewModel.stopRecordVideo()
            return
        }

        liveActivityViewModel.startRecordVideo()
    }

    private fun manageResultInRecordingVideo(result: Result<Unit>) {
        hideShadowLoading()
        with(result) {
            doIfSuccess {
                isRecordingVideo = !isRecordingVideo
                showRecordingIndicator(isRecordingVideo)
            }
            doIfError {
                liveViewAppBar.showErrorSnackBar(getString(R.string.error_saving_video))
                buttonRecord.isActivated = false
                showRecordingIndicator(false)
            }
        }
    }

    private fun showRecordingIndicator(show: Boolean) {
        imageRecordingIndicator.isVisible = show
        textLiveViewRecording.isVisible = show
        buttonRecord.isActivated = isRecordingVideo
        if (show) {
            val animation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
            imageRecordingIndicator.startAnimationIfEnabled(animation)
        } else {
            imageRecordingIndicator.clearAnimation()
        }
    }

    private fun changeOrientationLive() {
        cameFromLandscape = true
        requestedOrientation =
            if (isInPortraitMode()) {
                SCREEN_ORIENTATION_LANDSCAPE
            } else {
                SCREEN_ORIENTATION_PORTRAIT
            }
    }

    override fun onStop() {
        super.onStop()
        liveActivityViewModel.stopVLCMediaPlayer()
    }

    private fun setUrlLive() {
        val url = liveActivityViewModel.getUrlLive()
        liveActivityViewModel.createVLCMediaPlayer(url, liveStreamingView)
    }

    private fun startLiveVideoView() {
        liveActivityViewModel.startVLCMediaPlayer()
    }

    override fun onBackPressed() {}

    companion object {
        private var cameFromLandscape = false
        private const val BLINK_ANIMATION_DURATION = 1000L
        private const val BATTERY_TOTAL_HOURS = 8f
        private const val VIEW_LOADING_TIME = 600L
        private const val FREE_STORAGE_POSITION = 0
        private const val USED_STORAGE_POSITION = 1
        private const val TOTAL_STORAGE_POSITION = 2
        private const val TOTAL_PERCENTAGE = 100
        private const val PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY = 95
    }
}
