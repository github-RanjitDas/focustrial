package com.lawmobile.presentation.ui.live

import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCatalog
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityLiveViewBinding
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.live.LiveActivityViewModel.Companion.SCALE_BYTES
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.avml.cameras.entities.CatalogTypes
import com.safefleet.mobile.commons.animations.Animations
import com.safefleet.mobile.commons.helpers.Event
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import com.safefleet.mobile.commons.widgets.linearProgressBar.SafeFleetLinearProgressBarBehavior.ASCENDANT
import com.safefleet.mobile.commons.widgets.linearProgressBar.SafeFleetLinearProgressBarBehavior.DESCENDANT
import com.safefleet.mobile.commons.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges.*

class LiveActivity : BaseActivity() {

    private lateinit var activityLiveViewBinding: ActivityLiveViewBinding
    private lateinit var liveViewAppBar: View
    private lateinit var liveViewContainerLayout: View

    private val liveActivityViewModel: LiveActivityViewModel by viewModels()
    private val blinkAnimation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
    private var isViewLoaded = false
    private var isBatteryAlertShowed = false
    private var isStorageAlertShowed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLiveViewBinding = ActivityLiveViewBinding.inflate(layoutInflater)
        setContentView(activityLiveViewBinding.root)
        overridePendingTransition(0, 0)

        liveViewAppBar = findViewById(R.id.liveViewAppBar)
        liveViewContainerLayout = findViewById(R.id.liveViewContainerLayout)

        if (isInPortraitMode()) {
            setOfficerName()
            configureObservers()
            waitUntilViewIsLoadedToGetInformation()
            animateAppBar()
            animateContainer()
            turnOnLiveView()
        }
    }

    private fun logout() {
        liveActivityViewModel.disconnectCamera()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setOfficerName() {
        try {
            activityLiveViewBinding.liveViewAppBar.textViewOfficerName.text =
                CameraInfo.officerName.split(" ")[0]
            activityLiveViewBinding.liveViewAppBar.textViewOfficerLastName.text =
                CameraInfo.officerName.split(" ")[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        updateLiveOrPlaybackActive(activityLiveViewBinding.buttonSwitchLiveView.isActivated)
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
        activityLiveViewBinding.buttonSwitchLiveView.isActivated = true
    }

    private fun configureListeners() {
        activityLiveViewBinding.toggleFullScreenLiveView.setOnClickListenerCheckConnection {
            changeOrientationLive()
        }

        activityLiveViewBinding.buttonSnapshot.setClickListenerCheckConnection {
            showShadowLoading(getString(R.string.taking_snapshot))
            activateSwitch(true)
            liveActivityViewModel.takePhoto()
        }

        activityLiveViewBinding.buttonRecord.setClickListenerCheckConnection {
            if (!isRecordingVideo) {
                showShadowLoading(getString(R.string.starting_recording))
            } else {
                showShadowLoading(getString(R.string.stopping_recording))
            }
            activityLiveViewBinding.buttonSwitchLiveView.isActivated = true
            activateSwitch(true)
            manageRecordingVideo()
        }

        activityLiveViewBinding.buttonSwitchLiveView.setClickListenerCheckConnection {
            activateSwitch(activityLiveViewBinding.buttonSwitchLiveView.isActivated)
        }

        activityLiveViewBinding.buttonSnapshotList.setOnClickListenerCheckConnection {
            startFileListIntent(SNAPSHOT_LIST)
        }

        activityLiveViewBinding.buttonVideoList.setOnClickListenerCheckConnection {
            startFileListIntent(VIDEO_LIST)
        }

        activityLiveViewBinding.buttonOpenHelpPage.setOnClickListenerCheckConnection {
            val intent = Intent(this, HelpPageActivity::class.java)
            intent.putExtra("LiveActivity", true)
            startActivity(intent)
        }

        activityLiveViewBinding.liveViewAppBar.buttonLogout.setOnClickListenerCheckConnection {
            this.createAlertConfirmAppExit(::logout)
        }
    }

    private fun refreshActivityInformation(isViewLoaded: Boolean) {
        if (isViewLoaded) {
            if (CameraInfo.events.isEmpty()) {
                liveActivityViewModel.getCatalogInfo()
            } else liveActivityViewModel.getBatteryLevel()
        }
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
        activityLiveViewBinding.toggleFullScreenLiveView.isClickable = isActive
    }

    private fun changeVisibilityView(isVisible: Boolean) {
        if (isVisible) activityLiveViewBinding.liveStreamingView.setBackgroundResource(R.color.transparent)
        else activityLiveViewBinding.liveStreamingView.setBackgroundResource(R.color.black)
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

    private fun setCatalogInfo(domainCatalogList: Result<List<DomainCatalog>>) {
        with(domainCatalogList) {
            doIfSuccess { catalogInfoList ->
                val eventNames =
                    catalogInfoList.filter { it.type == CatalogTypes.EVENT.value }
                CameraInfo.events.addAll(eventNames)
            }
            doIfError {
                liveViewAppBar.showErrorSnackBar(
                    getString(R.string.catalog_error),
                    CATALOG_ERROR_ANIMATION_DURATION
                ) {
                    liveActivityViewModel.getCatalogInfo()
                }
            }
        }
        liveActivityViewModel.getBatteryLevel()
    }

    private fun setBatteryLevel(result: Event<Result<Int>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess(::manageBatteryLevel)
            doIfError {
                showBatteryLevelNotAvailable()
            }
        }
        liveActivityViewModel.getStorageLevels()
    }

    private fun manageBatteryLevel(batteryPercent: Int) {
        if (batteryPercent > 0) {
            activityLiveViewBinding.progressBatteryLevel.setProgress(batteryPercent, DESCENDANT)
            setColorInBattery(batteryPercent)
            setTextInProgressBattery(batteryPercent)
        } else showBatteryLevelNotAvailable()
    }

    private fun showBatteryLevelNotAvailable() {
        activityLiveViewBinding.textViewBatteryPercent.text = getString(R.string.not_available)
        activityLiveViewBinding.progressBatteryLevel.setProgress(0, ASCENDANT)
        liveViewAppBar.showErrorSnackBar(getString(R.string.battery_level_error))
    }

    private fun setColorInBattery(batteryPercent: Int) {
        when (batteryPercent) {
            in LOW_DESCENDANT_RANGE.value -> {
                activityLiveViewBinding.imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(this@LiveActivity, R.color.red)
                activityLiveViewBinding.imageViewBattery.startAnimationIfEnabled(blinkAnimation)
                if (!isBatteryAlertShowed) {
                    createAlertForInformationCamera(
                        R.string.battery_alert_title,
                        R.string.battery_alert_description
                    )
                    isBatteryAlertShowed = true
                }
            }
            in MEDIUM_DESCENDANT_RANGE.value -> {
                activityLiveViewBinding.imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(this@LiveActivity, R.color.red)
                activityLiveViewBinding.imageViewBattery.clearAnimation()
            }
            else -> {
                activityLiveViewBinding.imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(this@LiveActivity, R.color.darkBlue)
                activityLiveViewBinding.imageViewBattery.clearAnimation()
            }
        }
    }

    private fun setTextInProgressBattery(batteryPercent: Int) {
        val hoursLeft =
            ((batteryPercent * BATTERY_TOTAL_HOURS) / TOTAL_PERCENTAGE).toString().subSequence(0, 3)
        val textBatteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(getString(R.string.battery_percent, batteryPercent, hoursLeft), 0)
        else getString(R.string.battery_percent, batteryPercent, hoursLeft)
        activityLiveViewBinding.textViewBatteryPercent.text = textBatteryPercent
    }

    private fun setStorageLevels(result: Event<Result<List<Double>>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                setColorInStorageLevel(it)
                setTextStorageLevel(it)
            }
            doIfError {
                activityLiveViewBinding.textViewStorageLevels.text =
                    getString(R.string.not_available)
                activityLiveViewBinding.progressBatteryLevel.setProgress(0, ASCENDANT)
                liveViewAppBar.showErrorSnackBar(getString(R.string.storage_level_error))
            }
        }
        EspressoIdlingResource.decrement()
    }

    private fun setColorInStorageLevel(information: List<Double>) {
        val actualPercent =
            TOTAL_PERCENTAGE - ((information[FREE_STORAGE_POSITION] * TOTAL_PERCENTAGE) / information[TOTAL_STORAGE_POSITION])
        activityLiveViewBinding.progressStorageLevel.setProgress(
            actualPercent.toInt(),
            ASCENDANT
        )

        if (actualPercent.toInt() in HIGH_ASCENDANT_RANGE.value) {
            activityLiveViewBinding.imageViewStorage.backgroundTintList =
                ContextCompat.getColorStateList(this@LiveActivity, R.color.red)
            activityLiveViewBinding.imageViewStorage.startAnimationIfEnabled(blinkAnimation)
        } else {
            activityLiveViewBinding.imageViewStorage.backgroundTintList =
                ContextCompat.getColorStateList(this@LiveActivity, R.color.darkBlue)
            activityLiveViewBinding.imageViewStorage.clearAnimation()
        }

        if (actualPercent.toInt() >= PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY && !isStorageAlertShowed) {
            isStorageAlertShowed = true
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

        activityLiveViewBinding.textViewStorageLevels.text = textToStorage
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
        activityLiveViewBinding.viewLiveStreamingShadow.isVisible = false
        activityLiveViewBinding.viewDisableButtons.isVisible = false
        activityLiveViewBinding.viewLoading.isVisible = false
        activityLiveViewBinding.textViewLoading.isVisible = false
    }

    private fun showShadowLoading(message: String) {
        EspressoIdlingResource.increment()
        activityLiveViewBinding.buttonSwitchLiveView.isActivated = true
        activityLiveViewBinding.viewLiveStreamingShadow.isVisible = true
        activityLiveViewBinding.viewDisableButtons.isVisible = true
        activityLiveViewBinding.viewLoading.isVisible = true
        activityLiveViewBinding.textViewLoading.isVisible = true
        activityLiveViewBinding.textViewLoading.text = message
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
        isRecordingVideo = !isRecordingVideo
        showRecordingIndicator(isRecordingVideo)
        result.doIfError {
            liveViewAppBar.showErrorSnackBar(getString(R.string.error_saving_video))
        }
    }

    private fun showRecordingIndicator(show: Boolean) {
        activityLiveViewBinding.imageRecordingIndicator.isVisible = show
        activityLiveViewBinding.textLiveViewRecording.isVisible = show
        activityLiveViewBinding.buttonRecord.isActivated = show
        if (show) {
            val animation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
            activityLiveViewBinding.imageRecordingIndicator.startAnimationIfEnabled(animation)
        } else {
            activityLiveViewBinding.imageRecordingIndicator.clearAnimation()
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
        liveActivityViewModel.createVLCMediaPlayer(url, activityLiveViewBinding.liveStreamingView)
    }

    private fun startLiveVideoView() {
        liveActivityViewModel.startVLCMediaPlayer()
    }

    override fun onBackPressed() {}

    companion object {
        private var cameFromLandscape = false
        private const val BLINK_ANIMATION_DURATION = 1000L
        private const val CATALOG_ERROR_ANIMATION_DURATION = 7000
        private const val BATTERY_TOTAL_HOURS = 10f
        private const val VIEW_LOADING_TIME = 800L
        private const val FREE_STORAGE_POSITION = 0
        private const val USED_STORAGE_POSITION = 1
        private const val TOTAL_STORAGE_POSITION = 2
        private const val TOTAL_PERCENTAGE = 100
        private const val PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY = 95
    }
}
