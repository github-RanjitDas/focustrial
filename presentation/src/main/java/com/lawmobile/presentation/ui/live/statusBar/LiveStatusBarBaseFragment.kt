package com.lawmobile.presentation.ui.live.statusBar

import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.enums.CatalogTypes
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.CameraEventsManager
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.animations.Animations
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBar
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges

open class LiveStatusBarBaseFragment : BaseFragment() {

    private val sharedViewModel: LiveStatusBarBaseViewModel by activityViewModels()

    val blinkAnimation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)

    private var isBatteryAlertShowed = false
    private var isStorageAlertShowed = false
    var isViewLoaded = false

    lateinit var parentLayout: ConstraintLayout
    lateinit var textViewBattery: TextView
    lateinit var progressBarBattery: SafeFleetLinearProgressBar
    lateinit var imageViewBattery: ImageView
    lateinit var textViewStorage: TextView
    lateinit var progressBarStorage: SafeFleetLinearProgressBar
    lateinit var imageViewStorage: ImageView

    lateinit var batteryBarRanges: SafeFleetLinearProgressBarRanges
    lateinit var batteryBarColors: SafeFleetLinearProgressBarColors
    lateinit var storageBarRanges: SafeFleetLinearProgressBarRanges
    lateinit var storageBarColors: SafeFleetLinearProgressBarColors

    var onBatteryLow: (() -> Unit)? = null

    @ColorRes var highRangeColor: Int = 0

    override fun onResume() {
        super.onResume()
        getCameraStatus(isViewLoaded)
    }

    fun setSharedObservers() {
        sharedViewModel.catalogInfoLiveData.observe(viewLifecycleOwner, ::setCatalogInfo)
        sharedViewModel.batteryLevelLiveData.observe(viewLifecycleOwner, ::setBatteryLevel)
        sharedViewModel.storageLiveData.observe(viewLifecycleOwner, ::setStorageLevels)
    }

    fun getCameraStatus(isViewLoaded: Boolean) {
        CameraEventsManager.isReadyToReadEvents = false
        if (isViewLoaded) {
            if (CameraInfo.metadataEvents.isEmpty()) {
                sharedViewModel.getMetadataEvents()
            } else sharedViewModel.getBatteryLevel()
        }
    }

    private fun setCatalogInfo(metadataEventList: Result<List<MetadataEvent>>) {
        with(metadataEventList) {
            doIfSuccess { catalogInfoList ->
                val eventNames =
                    catalogInfoList.filter { it.type == CatalogTypes.EVENT.value }
                CameraInfo.metadataEvents.addAll(eventNames)
            }
            doIfError {
                parentLayout.showErrorSnackBar(
                    getString(R.string.catalog_error),
                    CATALOG_ERROR_ANIMATION_DURATION
                ) {
                    requireContext().verifySessionBeforeAction {
                        sharedViewModel.getMetadataEvents()
                    }
                }
            }
        }
        sharedViewModel.getBatteryLevel()
    }

    private fun setBatteryLevel(result: Event<Result<Int>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                manageBatteryLevel(it)
            }
            doIfError {
                showBatteryLevelNotAvailable()
            }
            sharedViewModel.getStorageLevels()
        }
    }

    private fun manageBatteryLevel(batteryPercent: Int) {
        if (batteryPercent > 0) {
            progressBarBattery.setProgress(batteryPercent)
            setColorInBattery(batteryPercent)
            setTextInProgressBattery(batteryPercent)
        } else showBatteryLevelNotAvailable()
    }

    private fun setTextInProgressBattery(batteryPercent: Int) {
        val hoursLeft =
            ((batteryPercent * BATTERY_TOTAL_HOURS) / TOTAL_PERCENTAGE).toString()
                .subSequence(0, 3)
        val textBatteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(getString(R.string.battery_percent, batteryPercent, hoursLeft), 0)
        else getString(R.string.battery_percent, batteryPercent, hoursLeft)
        textViewBattery.text = textBatteryPercent
    }

    private fun createAlertForInformationCamera(title: Int, message: Int) {
        val alertInformation = AlertInformation(
            title = title,
            message = message,
            onClickPositiveButton = { dialogInterface ->
                dialogInterface.dismiss()
            }
        )
        requireContext().createAlertInformation(alertInformation)
    }

    private fun setColorInBattery(batteryPercent: Int) {
        when (batteryPercent) {
            in batteryBarRanges.lowRange -> {
                imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        batteryBarColors.lowRangeColor
                    )
                onBatteryLow?.invoke()
                if (!isBatteryAlertShowed) {
                    createAlertForInformationCamera(
                        R.string.battery_alert_title,
                        R.string.battery_alert_description
                    )
                    isBatteryAlertShowed = true
                }
            }
            in batteryBarRanges.mediumRange -> {
                imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        batteryBarColors.mediumRangeColor
                    )
                imageViewBattery.clearAnimation()
            }
            else -> {
                imageViewBattery.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        highRangeColor
                    )
                imageViewBattery.clearAnimation()
            }
        }
    }

    private fun setStorageLevels(result: Event<Result<List<Double>>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                setColorInStorageLevel(it)
                setTextStorageLevel(it)
            }
            doIfError {
                textViewStorage.text = getString(R.string.not_available)
                progressBarBattery.setProgress(0)
                parentLayout.showErrorSnackBar(getString(R.string.storage_level_error))
            }
        }
        CameraEventsManager.isReadyToReadEvents = true
        EspressoIdlingResource.decrement()
    }

    private fun setColorInStorageLevel(information: List<Double>) {
        val actualPercent =
            TOTAL_PERCENTAGE - ((information[FREE_STORAGE_POSITION] * TOTAL_PERCENTAGE) / information[TOTAL_STORAGE_POSITION])
        progressBarStorage.setProgress(actualPercent.toInt())

        if (actualPercent.toInt() in storageBarRanges.highRange) {
            imageViewStorage.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red)
            imageViewStorage.startAnimationIfEnabled(blinkAnimation)
        } else {
            imageViewStorage.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.greenSuccess)
            imageViewStorage.clearAnimation()
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

        textViewStorage.text = textToStorage
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

    private fun showBatteryLevelNotAvailable() {
        textViewBattery.text = getString(R.string.not_available)
        progressBarBattery.setProgress(0)
        parentLayout.showErrorSnackBar(getString(R.string.battery_level_error))
    }

    companion object {
        const val CATALOG_ERROR_ANIMATION_DURATION = 7000
        const val BLINK_ANIMATION_DURATION = 1000L
        private const val SCALE_BYTES = 1024

        private const val BATTERY_TOTAL_HOURS = 10f
        private const val FREE_STORAGE_POSITION = 0
        private const val USED_STORAGE_POSITION = 1
        private const val TOTAL_STORAGE_POSITION = 2
        private const val TOTAL_PERCENTAGE = 100
        private const val PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY = 95
    }
}
