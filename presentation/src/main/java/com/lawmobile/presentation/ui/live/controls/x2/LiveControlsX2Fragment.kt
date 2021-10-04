package com.lawmobile.presentation.ui.live.controls.x2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.presentation.databinding.FragmentLiveControlsX2Binding
import com.lawmobile.presentation.ui.live.controls.LiveControlsBaseFragment
import com.lawmobile.presentation.ui.live.controls.x1.LiveControlsX1Fragment

class LiveControlsX2Fragment : LiveControlsBaseFragment() {

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
        setSharedListeners()
        setLiveViewSwitchState()
    }

    private fun setViews() {
        buttonSnapshot = binding.buttonSnapshot
        buttonAudio = binding.buttonAudio
        buttonRecord = binding.buttonRecord
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

    companion object {
        val TAG = LiveControlsX1Fragment::class.java.simpleName
    }
}
