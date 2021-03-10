package com.lawmobile.presentation.ui.live.stream

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStreamBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment

class LiveStreamFragment : BaseFragment() {

    private val viewModel: LiveStreamViewModel by activityViewModels()

    private val binding: FragmentLiveStreamBinding get() = _binding!!
    private var _binding: FragmentLiveStreamBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUrlLive()
        startLiveVideoView()
        setListeners()
    }

    fun showLoadingState(message: String) {
        binding.viewLiveStreamingShadow.isVisible = true
        binding.viewLoading.isVisible = true
        binding.textViewLoading.isVisible = true
        binding.textViewLoading.text = message
    }

    fun hideLoadingState() {
        binding.viewLiveStreamingShadow.isVisible = false
        binding.viewLoading.isVisible = false
        binding.textViewLoading.isVisible = false
    }

    fun setStreamVisibility(isVisible: Boolean) {
        if (isVisible) binding.liveStreamingView.setBackgroundResource(R.color.transparent)
        else binding.liveStreamingView.setBackgroundResource(R.color.black)
        binding.toggleFullScreenLiveView.isClickable = isVisible
    }

    private fun setUrlLive() {
        val url = viewModel.getUrlLive()
        viewModel.createVLCMediaPlayer(url, binding.liveStreamingView)
    }

    private fun startLiveVideoView() {
        viewModel.startVLCMediaPlayer()
    }

    private fun setListeners() {
        binding.toggleFullScreenLiveView.setOnClickListenerCheckConnection {
            changeOrientationLive()
        }
    }

    private fun changeOrientationLive() {
        requireActivity().requestedOrientation =
            if (isInPortraitMode()) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopVLCMediaPlayer()
    }

    companion object {
        val TAG = LiveStreamFragment::class.java.simpleName
    }
}
