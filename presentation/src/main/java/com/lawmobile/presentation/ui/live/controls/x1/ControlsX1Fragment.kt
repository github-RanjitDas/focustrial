package com.lawmobile.presentation.ui.live.controls.x1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        setSharedListeners()
        turnOnLiveViewSwitch()
    }

    private fun setViews() {
        buttonSnapshot = binding.buttonSnapshot
        buttonRecord = binding.buttonRecord
        buttonSwitchLiveView = binding.buttonSwitchLiveView
        parentLayout = binding.layoutLiveControls
        viewDisableButtons = binding.viewDisableButtons
        imageRecordingIndicator = binding.imageRecordingIndicator
        textLiveViewRecording = binding.textLiveViewRecording
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = ControlsX1Fragment::class.java.simpleName
    }
}
