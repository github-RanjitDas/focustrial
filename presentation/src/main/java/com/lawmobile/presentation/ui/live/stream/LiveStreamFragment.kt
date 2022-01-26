package com.lawmobile.presentation.ui.live.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStreamBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.live.shared.LiveStream
import com.safefleet.mobile.safefleet_ui.animations.Animations

class LiveStreamFragment : BaseFragment(), LiveStream {

    private val viewModel: LiveStreamViewModel by activityViewModels()

    private val binding: FragmentLiveStreamBinding get() = _binding!!
    private var _binding: FragmentLiveStreamBinding? = null

    override var onFullScreenClick: (() -> Unit)? = null

    private var isButtonFullscreenActivated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setFullscreenButtonActivated()
    }

    override fun onResume() {
        super.onResume()
        startLiveStream()
    }

    private fun setFullscreenButtonActivated() {
        binding.toggleFullScreenLiveView.isActivated = isButtonFullscreenActivated
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

    fun showRecordingAudio(isVisible: Boolean) {
        binding.imageAudio?.isVisible = isVisible
        binding.imageBackgroundDisable?.isVisible = isVisible
        if (isVisible) {
            val animation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
            binding.imageAudio?.startAnimationIfEnabled(animation)
        } else {
            binding.imageAudio?.clearAnimation()
        }
    }

    private fun startLiveStream() {
        if (viewModel.mediaPlayer.isPlaying) viewModel.mediaPlayer.stop()
        val url = viewModel.getUrlLive()
        viewModel.mediaPlayer.create(url, binding.liveStreamingView)
        viewModel.mediaPlayer.play()
    }

    private fun setListeners() {
        binding.toggleFullScreenLiveView.setOnClickListenerCheckConnection {
            onFullScreenClick?.invoke()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG: String = LiveStreamFragment::class.java.simpleName
        fun createInstance(isFullscreen: Boolean): LiveStreamFragment {
            return LiveStreamFragment().apply {
                isButtonFullscreenActivated = isFullscreen
            }
        }
    }
}
