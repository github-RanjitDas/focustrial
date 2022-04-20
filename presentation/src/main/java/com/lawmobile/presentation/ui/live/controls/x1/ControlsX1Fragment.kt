package com.lawmobile.presentation.ui.live.controls.x1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.databinding.FragmentLiveControlsX1Binding
import com.lawmobile.presentation.ui.live.controls.ControlsBaseFragment

class ControlsX1Fragment : ControlsBaseFragment() {

    private val binding: FragmentLiveControlsX1Binding get() = _binding!!
    private var _binding: FragmentLiveControlsX1Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveControlsX1Binding.inflate(inflater, container, false)
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
        binding.buttonResetViewFinder.isVisible = BuildConfig.DEBUG
    }

    private fun setViews() {
        buttonTakeSnapshot = binding.buttonSnapshot
        buttonRecordVideo = binding.buttonRecord
        buttonSwitchLiveView = binding.buttonSwitchLiveView
        buttonResetViewFinder = binding.buttonResetViewFinder
        parentLayout = binding.layoutLiveControls
        viewDisableButtons = binding.viewDisableButtons
        imageRecordingIndicator = binding.imageRecordingIndicator
        textLiveViewRecording = binding.textLiveViewRecording
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = ControlsX1Fragment::class.java.simpleName
    }
}
