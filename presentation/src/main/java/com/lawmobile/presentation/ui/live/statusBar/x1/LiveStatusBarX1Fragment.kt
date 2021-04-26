package com.lawmobile.presentation.ui.live.statusBar.x1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStatusBarX1Binding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.live.statusBar.LiveStatusBarBaseFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges

class LiveStatusBarX1Fragment : LiveStatusBarBaseFragment() {

    private val binding: FragmentLiveStatusBarX1Binding get() = _binding!!
    private var _binding: FragmentLiveStatusBarX1Binding? = null

    private lateinit var storageBarRanges: SafeFleetLinearProgressBarRanges
    private lateinit var storageBarColors: SafeFleetLinearProgressBarColors

    private var isStorageAlertShowed = false
    private var isBatteryAlertShowed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isInPortraitMode()) {
            setSharedObservers()
            setObservers()
        }
        _binding = FragmentLiveStatusBarX1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureProgressBars()
        setSharedViews()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        getCameraStatus(isViewLoaded)
    }

    private fun setObservers() {
        sharedViewModel.storageLiveData.observe(viewLifecycleOwner, ::setStorageLevels)
    }

    private fun setListeners() {
        onBatteryLow = ::manageLowBattery
        binding.buttonOpenHelpPage.setOnClickListenerCheckConnection {
            val intent = Intent(requireContext(), HelpPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun manageLowBattery() {
        imageViewBattery.startAnimationIfEnabled(blinkAnimation)
        if (!isBatteryAlertShowed) {
            createAlertForInformationCamera(
                R.string.battery_alert_title,
                R.string.battery_alert_description
            )
            isBatteryAlertShowed = true
        }
    }

    private fun configureProgressBars() {
        batteryBarRanges = SafeFleetLinearProgressBarRanges(
            Range(41, 100),
            Range(15, 40),
            Range(0, 14)
        )
        batteryBarColors = SafeFleetLinearProgressBarColors(
            R.color.lightGreen,
            R.color.red,
            R.color.red
        )
        storageBarRanges = SafeFleetLinearProgressBarRanges(
            Range(85, 100),
            Range(70, 84),
            Range(0, 69)
        )
        storageBarColors = SafeFleetLinearProgressBarColors(
            R.color.red,
            R.color.red,
            R.color.lightGreen
        )
        highRangeColor = R.color.darkBlue
        binding.progressBatteryLevel.setBehavior(batteryBarRanges, batteryBarColors)
        binding.progressStorageLevel.setBehavior(storageBarRanges, storageBarColors)
    }

    private fun setSharedViews() {
        parentLayout = binding.layoutStatusBar
        textViewBattery = binding.textViewBatteryPercent
        progressBarBattery = binding.progressBatteryLevel
        imageViewBattery = binding.imageViewBattery
    }

    private fun setStorageLevels(result: Event<Result<List<Double>>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                setColorInStorageLevel(it)
                setTextStorageLevel(it)
            }
            doIfError {
                binding.textViewStorageLevels.text = getString(R.string.not_available)
                progressBarBattery.setProgress(0)
                parentLayout.showErrorSnackBar(getString(R.string.storage_level_error))
            }
        }
        EspressoIdlingResource.decrement()
    }

    private fun setColorInStorageLevel(information: List<Double>) {
        val remainingPercent =
            (TOTAL_PERCENTAGE - ((information[FREE_STORAGE_POSITION] * TOTAL_PERCENTAGE) / information[TOTAL_STORAGE_POSITION])).toInt()
        binding.progressStorageLevel.setProgress(remainingPercent)

        binding.imageViewStorage.apply {
            if (remainingPercent in storageBarRanges.highRange) {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red)
                startAnimationIfEnabled(blinkAnimation)
            } else {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.darkBlue)
                clearAnimation()
            }
        }

        if (remainingPercent >= PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY && !isStorageAlertShowed) {
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
        } else getStringStorageLevel(information)

        binding.textViewStorageLevels.text = textToStorage
    }

    private fun getStringStorageLevel(information: List<Double>): String {
        val used = information[USED_STORAGE_POSITION]
        val free = information[FREE_STORAGE_POSITION]
        var usedFormat = String.format("%.0f", used) + " MB"
        var freeFormat = String.format("%.0f", free) + " MB"

        if (used >= SCALE_BYTES) usedFormat = String.format("%.1f", used / SCALE_BYTES) + " GB"
        if (free >= SCALE_BYTES) freeFormat = String.format("%.1f", free / SCALE_BYTES) + " GB"

        return getString(R.string.storage_level_x1, usedFormat, freeFormat)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = LiveStatusBarX1Fragment::class.java.simpleName
        private const val PERCENT_TO_SHOW_ALERT_MEMORY_CAPACITY = 95
    }
}
