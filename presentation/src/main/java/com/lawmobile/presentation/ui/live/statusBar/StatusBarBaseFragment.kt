package com.lawmobile.presentation.ui.live.statusBar

import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.animations.Animations
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBar
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges

abstract class StatusBarBaseFragment : BaseFragment() {

    val sharedViewModel: StatusBarBaseViewModel by activityViewModels()
    val blinkAnimation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
    protected var isShowCameraStatusFailedError: Boolean = true
    lateinit var parentLayout: ConstraintLayout
    lateinit var textViewBattery: TextView
    lateinit var progressBarBattery: SafeFleetLinearProgressBar
    lateinit var imageViewBattery: ImageView

    lateinit var batteryBarRanges: SafeFleetLinearProgressBarRanges
    lateinit var batteryBarColors: SafeFleetLinearProgressBarColors

    var onBatteryLow: (() -> Unit)? = null

    @ColorRes
    var highRangeColor: Int = 0

    suspend fun getCameraStatus() = sharedViewModel.getCameraStatus()

    open fun setBatteryLevel(result: Event<Result<Int>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess(::manageBatteryLevel)
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_CAMERA_ERRORS,
                    it,
                    getString(R.string.battery_level_error)
                )
                if (isShowCameraStatusFailedError) {
                    showBatteryLevelNotAvailable()
                }
            }
        }
    }

    fun showBatteryLevelNotAvailable() {
        textViewBattery.text = getString(R.string.not_available)
        progressBarBattery.setProgress(0)
        imageViewBattery.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            batteryBarColors.lowRangeColor
        )
        parentLayout.showErrorSnackBar(getString(R.string.battery_level_error))
    }

    open fun manageBatteryLevel(batteryPercent: Int) {
        activity?.runOnUiThread {
            if (batteryPercent >= 0) {
                progressBarBattery.setProgress(batteryPercent)
                setColorInBattery(batteryPercent)
                setTextInProgressBattery(batteryPercent)
            } else showBatteryLevelNotAvailable()
        }
    }

    fun setColorInBattery(batteryPercent: Int) {
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

    open fun setTextInProgressBattery(batteryPercent: Int) {
        var hoursLeftText =
            ((batteryPercent * BATTERY_TOTAL_HOURS) / TOTAL_PERCENTAGE).toString()
                .subSequence(0, 3)
        val hoursLeftNumber = hoursLeftText.toString().toFloat()

        hoursLeftText =
            if (hoursLeftNumber < 1) (hoursLeftNumber * 60).toInt().toString() + " minutes"
            else "$hoursLeftText hours"

        val textBatteryPercent =
            Html.fromHtml(getString(R.string.battery_percent, batteryPercent, hoursLeftText), 0)
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
        const val BLINK_ANIMATION_DURATION = 1000L

        const val SCALE_BYTES = 1024
        const val FREE_STORAGE_POSITION = 0
        const val USED_STORAGE_POSITION = 1
        const val TOTAL_STORAGE_POSITION = 2

        private const val BATTERY_TOTAL_HOURS = 10f
        const val TOTAL_PERCENTAGE = 100
    }
}
