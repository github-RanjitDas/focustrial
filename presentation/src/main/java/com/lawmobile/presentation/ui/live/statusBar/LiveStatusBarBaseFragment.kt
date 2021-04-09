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
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.CameraEventsManager
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.animations.Animations
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBar
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges

open class LiveStatusBarBaseFragment : BaseFragment() {

    val sharedViewModel: LiveStatusBarBaseViewModel by activityViewModels()
    val blinkAnimation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)

    var isViewLoaded = false

    lateinit var parentLayout: ConstraintLayout
    lateinit var textViewBattery: TextView
    lateinit var progressBarBattery: SafeFleetLinearProgressBar
    lateinit var imageViewBattery: ImageView

    lateinit var batteryBarRanges: SafeFleetLinearProgressBarRanges
    lateinit var batteryBarColors: SafeFleetLinearProgressBarColors

    var onBatteryLow: (() -> Unit)? = null

    @ColorRes
    var highRangeColor: Int = 0

    fun setSharedObservers() {
        sharedViewModel.catalogInfoLiveData.observe(viewLifecycleOwner, ::setCatalogInfo)
        sharedViewModel.batteryLevelLiveData.observe(viewLifecycleOwner, ::setBatteryLevel)
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

    private fun showBatteryLevelNotAvailable() {
        textViewBattery.text = getString(R.string.not_available)
        progressBarBattery.setProgress(0)
        parentLayout.showErrorSnackBar(getString(R.string.battery_level_error))
    }

    fun manageBatteryLevel(batteryPercent: Int) {
        requireActivity().runOnUiThread {
            if (batteryPercent > 0) {
                progressBarBattery.setProgress(batteryPercent)
                setColorInBattery(batteryPercent)
                setTextInProgressBattery(batteryPercent)
            } else showBatteryLevelNotAvailable()
        }
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

    private fun setTextInProgressBattery(batteryPercent: Int) {
        val hoursLeft =
            ((batteryPercent * BATTERY_TOTAL_HOURS) / TOTAL_PERCENTAGE).toString()
                .subSequence(0, 3)
        val textBatteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(getString(R.string.battery_percent, batteryPercent, hoursLeft), 0)
        else getString(R.string.battery_percent, batteryPercent, hoursLeft)
        textViewBattery.text = textBatteryPercent
    }

    fun createAlertForInformationCamera(title: Int, message: Int) {
        val alertInformation = AlertInformation(
            title = title,
            message = message,
            onClickPositiveButton = { dialogInterface ->
                dialogInterface.dismiss()
            }
        )
        requireContext().createAlertInformation(alertInformation)
    }

    companion object {
        const val CATALOG_ERROR_ANIMATION_DURATION = 7000
        const val BLINK_ANIMATION_DURATION = 1000L

        const val SCALE_BYTES = 1024
        const val FREE_STORAGE_POSITION = 0
        const val USED_STORAGE_POSITION = 1
        const val TOTAL_STORAGE_POSITION = 2

        private const val BATTERY_TOTAL_HOURS = 10f
        const val TOTAL_PERCENTAGE = 100
    }
}
