package com.lawmobile.presentation.ui.live.stream

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStreamBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.live.shared.LiveStream
import com.lawmobile.presentation.ui.live.statusBar.StatusBarBaseFragment.Companion.BLINK_ANIMATION_DURATION
import com.safefleet.mobile.safefleet_ui.animations.Animations

class LiveStreamFragment : BaseFragment(), LiveStream {

    private val viewModel: LiveStreamViewModel by activityViewModels()

    private val binding: FragmentLiveStreamBinding get() = _binding!!
    private var _binding: FragmentLiveStreamBinding? = null

    override var onFullScreenClick: (() -> Unit)? = null

    private var isButtonFullscreenActivated = false
    private var exoPlayer: ExoPlayer? = null

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
        if (CameraInfo.isCameraConnected) {
            requireContext().verifySessionBeforeAction(::startLiveStreamExoPlayer)
        }
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
        if (isVisible) {
            startLiveStreamExoPlayer()
            binding.liveStreamingViewExoPlayer.setBackgroundResource(R.color.transparent)
            binding.liveViewClosed.visibility = View.GONE
        } else {
            exoPlayer?.stop()
            releasePlayer()
            binding.liveStreamingViewExoPlayer.setBackgroundResource(R.color.black)
            binding.liveViewClosed.visibility = View.VISIBLE
        }
        binding.toggleFullScreenLiveView.isClickable = isVisible
    }

    fun showRecordingAudio(isVisible: Boolean) {
        binding.imageAudio.isVisible = isVisible
        binding.imageBackgroundDisable.isVisible = isVisible
        if (isVisible) {
            val animation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
            binding.imageAudio.startAnimationIfEnabled(animation)
        } else {
            binding.imageAudio.clearAnimation()
        }
    }

    private fun startLiveStream() {
        if (viewModel.mediaPlayer.isPlaying) viewModel.mediaPlayer.stop()
        val url = viewModel.getUrlLive()
        viewModel.mediaPlayer.create(url, binding.liveStreamingView)
        viewModel.mediaPlayer.play()
    }

    private fun startLiveStreamExoPlayer() {
        releasePlayer()
        exoPlayer = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                binding.liveStreamingViewExoPlayer.player = exoPlayer
                val url = viewModel.getUrlLive()
                val mediaSource: MediaSource = RtspMediaSource.Factory().setForceUseRtpTcp(true)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
                exoPlayer.setMediaSource(mediaSource)
            }
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }

    private fun setListeners() {
        binding.toggleFullScreenLiveView.setOnClickListenerCheckConnection {
            onFullScreenClick?.invoke()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.mediaPlayer.stop()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        releasePlayer()
    }

    override val viewTag: String
        get() = SimpleFileListFragment.TAG

    companion object {
        val TAG: String = LiveStreamFragment::class.java.simpleName
        fun createInstance(isFullscreen: Boolean): LiveStreamFragment {
            return LiveStreamFragment().apply {
                isButtonFullscreenActivated = isFullscreen
            }
        }
    }
}
