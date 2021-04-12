package com.lawmobile.presentation.ui.live.statusBar.x2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStatusBarX2Binding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.live.statusBar.LiveStatusBarBaseFragment
import com.lawmobile.presentation.utils.CameraEventsManager
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges

class LiveStatusBarX2Fragment : LiveStatusBarBaseFragment() {

    private val binding: FragmentLiveStatusBarX2Binding get() = _binding!!
    private var _binding: FragmentLiveStatusBarX2Binding? = null

    private lateinit var storageBarRanges: SafeFleetLinearProgressBarRanges
    private lateinit var storageBarColors: SafeFleetLinearProgressBarColors

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isInPortraitMode()) {
            setSharedObservers()
            setObservers()
        }
        _binding = FragmentLiveStatusBarX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureProgressBars()
        setSharedViews()
        setListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCameraStatus(isViewLoaded)
    }

    override fun onResume() {
        super.onResume()
        if (CameraInfo.metadataEvents.isNotEmpty()) {
            sharedViewModel.getBatteryLevel()
        }
    }

    private fun setObservers() {
        sharedViewModel.storageLiveData.observe(viewLifecycleOwner, ::setStorageLevels)
    }

    private fun configureProgressBars() {
        batteryBarRanges = SafeFleetLinearProgressBarRanges(
            highRange = Range(36, 100),
            mediumRange = Range(6, 35),
            lowRange = Range(0, 5)
        )
        batteryBarColors = SafeFleetLinearProgressBarColors(
            highRangeColor = R.color.lightGreen,
            mediumRangeColor = R.color.colorOrange,
            lowRangeColor = R.color.red
        )
        storageBarRanges = SafeFleetLinearProgressBarRanges(
            highRange = Range(85, 100),
            mediumRange = Range(70, 84),
            lowRange = Range(0, 69)
        )
        storageBarColors = SafeFleetLinearProgressBarColors(
            highRangeColor = R.color.red,
            mediumRangeColor = R.color.red,
            lowRangeColor = R.color.lightGreen
        )
        highRangeColor = batteryBarColors.highRangeColor
        binding.progressBatteryLevel.setBehavior(batteryBarRanges, batteryBarColors)
        binding.progressStorageLevel.setBehavior(storageBarRanges, storageBarColors)
    }

    private fun setSharedViews() {
        parentLayout = binding.layoutStatusBar
        textViewBattery = binding.textViewBatteryPercent
        progressBarBattery = binding.progressBatteryLevel
        imageViewBattery = binding.imageViewBattery
    }

    private fun setListeners() {
        with(BaseActivity) {
            onBatteryLevelChanged = ::manageBatteryLevel
            onStorageLevelChanged = ::manageStorageLevel
            onLowBattery = ::manageLowBattery
            onLowStorage = ::manageLowStorage
        }

        binding.buttonOpenHelpPage.setOnClickListenerCheckConnection {
            val intent = Intent(requireContext(), HelpPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun manageLowStorage() {
        binding.imageViewStorage.startAnimationIfEnabled(blinkAnimation)
        createAlertForInformationCamera(
            R.string.storage_alert_title,
            R.string.storage_alert_description
        )
    }

    private fun manageLowBattery() {
        imageViewBattery.startAnimationIfEnabled(blinkAnimation)
        createAlertForInformationCamera(
            R.string.battery_alert_title,
            R.string.battery_alert_description
        )
    }

    private fun setStorageLevels(result: Event<Result<List<Double>>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                manageStorageLevel(getUsedStoragePercent(it))
            }
            doIfError {
                binding.textViewStorageLevels.text = getString(R.string.not_available)
                progressBarBattery.setProgress(0)
                parentLayout.showErrorSnackBar(getString(R.string.storage_level_error))
            }
        }
        CameraEventsManager.isReadyToReadEvents = true
        EspressoIdlingResource.decrement()
    }

    private fun manageStorageLevel(usedPercent: Double) {
        requireActivity().runOnUiThread {
            setColorInStorageLevel(usedPercent)
            setTextStorageLevel(usedPercent)
        }
    }

    private fun getUsedStoragePercent(information: List<Double>) =
        (TOTAL_PERCENTAGE - ((information[FREE_STORAGE_POSITION] * TOTAL_PERCENTAGE) / information[TOTAL_STORAGE_POSITION]))

    private fun setColorInStorageLevel(usedPercent: Double) {
        binding.progressStorageLevel.setProgress(usedPercent.toInt())
        binding.imageViewStorage.apply {
            if (usedPercent.toInt() in storageBarRanges.highRange) {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red)
            } else {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.greenSuccess)
                clearAnimation()
            }
        }
    }

    private fun setTextStorageLevel(usedPercent: Double) {
        val usedStorageString = getString(R.string.storage_level_x2, usedPercent.toInt())

        val textToStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(usedStorageString, 0)
        } else usedStorageString

        binding.textViewStorageLevels.text = textToStorage
    }

    companion object {
        val TAG = LiveStatusBarX2Fragment::class.java.simpleName
    }
}
