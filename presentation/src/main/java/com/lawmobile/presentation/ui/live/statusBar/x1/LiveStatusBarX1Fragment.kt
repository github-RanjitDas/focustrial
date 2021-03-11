package com.lawmobile.presentation.ui.live.statusBar.x1

import android.content.Intent
import android.os.Bundle
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStatusBarBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.live.statusBar.LiveStatusBarBaseFragment
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges

class LiveStatusBarX1Fragment : LiveStatusBarBaseFragment() {

    private val binding: FragmentLiveStatusBarBinding get() = _binding!!
    private var _binding: FragmentLiveStatusBarBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isInPortraitMode()) setSharedObservers()
        _binding = FragmentLiveStatusBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureProgressBars()
        setSharedViews()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonOpenHelpPage.setOnClickListenerCheckConnection {
            val intent = Intent(requireContext(), HelpPageActivity::class.java)
            startActivity(intent)
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
        textViewStorage = binding.textViewStorageLevels
        progressBarStorage = binding.progressStorageLevel
        imageViewStorage = binding.imageViewStorage
    }

    companion object {
        val TAG = LiveStatusBarX1Fragment::class.java.simpleName
    }
}
