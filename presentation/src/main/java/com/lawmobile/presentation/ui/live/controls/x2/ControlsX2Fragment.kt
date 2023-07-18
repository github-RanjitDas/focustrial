package com.lawmobile.presentation.ui.live.controls.x2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.databinding.FragmentLiveControlsX2Binding
import com.lawmobile.presentation.ui.live.controls.ControlsBaseFragment
import com.lawmobile.presentation.utils.FeatureSupportHelper

class ControlsX2Fragment : ControlsBaseFragment() {

    private val binding: FragmentLiveControlsX2Binding get() = _binding!!
    private var _binding: FragmentLiveControlsX2Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveControlsX2Binding.inflate(inflater, container, false)
        setSharedObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setFeatures()
        setSharedListeners()
        setLiveViewSwitchState()
    }

    private fun setFeatures() {
        if (!FeatureSupportHelper.supportAudios) {
            binding.buttonAudio.visibility = View.GONE
            binding.guidelineMidHorizontalBottom.setGuidelinePercent(1F)
        }

        binding.buttonResetViewFinder.isVisible = FeatureSupportHelper.isButtonResetViewFinderVisible
    }

    private fun setViews() {
        buttonTakeSnapshot = binding.buttonSnapshot
        buttonRecordAudio = binding.buttonAudio
        buttonRecordVideo = binding.buttonRecord
        buttonResetViewFinder = binding.buttonResetViewFinder
        buttonSwitchLiveView = binding.buttonSwitchLiveView
        parentLayout = binding.layoutLiveControlsX2
        viewDisableButtons = binding.viewDisableButtons
        imageRecordingIndicator = binding.imageRecordingIndicator
        textLiveViewRecording = binding.textLiveViewRecording
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = ControlsX2Fragment.TAG

    companion object {
        val TAG: String = ControlsX2Fragment::class.java.simpleName
    }
}
