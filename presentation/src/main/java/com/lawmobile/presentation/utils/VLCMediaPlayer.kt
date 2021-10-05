package com.lawmobile.presentation.utils

import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.lawmobile.domain.helpers.runWithDelay
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.MediaPlayerControls
import com.lawmobile.presentation.extensions.milliSecondsToString
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import kotlin.math.roundToInt

class VLCMediaPlayer(private val libVLC: LibVLC, private val mediaPlayer: MediaPlayer) {

    var isPlaying = false
        get() = mediaPlayer.isPlaying
        private set

    var isPaused = false
        private set

    var isEndReached = false
        private set

    var isPlayingCallback: ((Boolean) -> Unit)? = null

    private var currentMedia: Media? = null
    private var currentUrl: String = ""
    private var surfaceView: SurfaceView? = null
    private var currentDuration = 0L
    private var controls: MediaPlayerControls? = null

    private lateinit var lifecycle: Lifecycle
    private lateinit var lifecycleObserver: LifecycleEventObserver

    private var encounteredError = false
    private var isPausedByUser = false

    fun create(url: String, view: SurfaceView) {
        releaseMedia()
        setMedia(url)
        detachAllViews()
        attachView(view)
    }

    fun setControls(controls: MediaPlayerControls, duration: Long, lifecycle: Lifecycle) {
        this.controls = controls
        this.lifecycle = lifecycle
        setListeners()
        setLifecycleObserver()
        setDuration(duration)
        verifyAndSetState()
    }

    private fun setDuration(duration: Long) {
        currentDuration = duration
        controls?.durationTextView?.text = currentDuration.milliSecondsToString()
    }

    private fun verifyAndSetState() {
        if (isEndReached) {
            controls?.progressTextView?.text = controls?.durationTextView?.text
            controls?.progressBar?.progress = TOTAL_PROGRESS
            controls?.playButton?.setImageResource(R.drawable.ic_media_play)
        }

        if (isPaused) controls?.playButton?.setImageResource(R.drawable.ic_media_play)

        if (controls?.progressTextView?.text?.isEmpty() == true) {
            controls?.progressTextView?.text = 0L.milliSecondsToString()
        }
    }

    fun play() {
        isPaused = false
        isPlayingCallback?.invoke(true)
        if (!isPlaying) {
            if (isEndReached) mediaPlayer.stop()
            controls?.playButton?.setImageResource(R.drawable.ic_media_pause)
            controls?.aspectRatioButton?.isClickable = true
            isEndReached = false
            mediaPlayer.play()
        }
    }

    fun stop() {
        isPaused = false
        controls?.playButton?.setImageResource(R.drawable.ic_media_play)
        mediaPlayer.stop()
        detachAllViews()
        releaseMedia()
    }

    fun pause() {
        isPaused = true
        isPlayingCallback?.invoke(false)
        if (isPlaying) {
            mediaPlayer.stop()
            controls?.aspectRatioButton?.isClickable = false
            controls?.playButton?.setImageResource(R.drawable.ic_media_play)
        }
    }

    private fun setProgress(progress: Int) {
        currentProgress = progress
        controls?.progressBar?.progress = progress
        controls?.progressTextView?.text = getCurrentTime(progress).milliSecondsToString()
        mediaPlayer.time = getCurrentTime(progress)
    }

    private fun getCurrentTime(progress: Int) =
        ((progress.toDouble() * currentDuration.toDouble()) / TOTAL_PROGRESS).toLong()

    private fun setListeners() {
        setButtonListeners()
        setProgressBarListener()
        setEventListener()
    }

    private fun setLifecycleObserver() {
        lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> if (isPlaying) pause()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                Lifecycle.Event.ON_RESUME -> onResume()
                else -> {
                }
            }
        }
        lifecycle.addObserver(lifecycleObserver)
    }

    private fun onDestroy() {
        stop()
        lifecycle.removeObserver(lifecycleObserver)
    }

    private fun onResume() {
        surfaceView?.let { create(currentUrl, it) }
    }

    private fun setButtonListeners() {
        controls?.playButton?.setOnClickListenerCheckConnection {
            managePlayButton()
        }
        controls?.aspectRatioButton?.setOnClickListenerCheckConnection {
            changeAspectRatio()
        }
    }

    private fun setEventListener() {
        mediaPlayer.setEventListener {
            when (it.type) {
                MediaPlayer.Event.EncounteredError -> onEncounteredError()
                MediaPlayer.Event.MediaChanged -> onMediaChanged()
                MediaPlayer.Event.TimeChanged -> onTimeChanged()
                MediaPlayer.Event.Playing -> onPlaying()
                MediaPlayer.Event.EndReached -> onEndReached()
            }
        }
    }

    private fun onPlaying() {
        setProgress(currentProgress)
        if (isPaused || isPausedByUser) pause()
        else if (!isEndReached && !isPlaying && !isPausedByUser) play()
    }

    private fun onMediaChanged() {
        if (!isEndReached && encounteredError) {
            encounteredError = false
            play()
        }
    }

    private fun onEncounteredError() {
        runWithDelay(DELAY_ON_ERROR) {
            encounteredError = true
            releaseMedia()
            setMedia(currentUrl)
        }
    }

    private fun onTimeChanged() {
        updateProgressTextView()
        updateProgressBar()
        validateEndReached()
    }

    private fun validateEndReached() {
        if (currentProgress == TOTAL_PROGRESS && isPlaying) onEndReached()
    }

    private fun onEndReached() {
        controls?.progressTextView?.text = controls?.durationTextView?.text
        controls?.progressBar?.progress = TOTAL_PROGRESS
        isPlayingCallback?.invoke(false)
        controls?.playButton?.setImageResource(R.drawable.ic_media_play)
        controls?.aspectRatioButton?.isClickable = false
        mediaPlayer.stop()
        currentProgress = 0
        isEndReached = true
    }

    private fun updateProgressBar() {
        val progress = try {
            ((mediaPlayer.time.toDouble() / currentDuration.toDouble()) * TOTAL_PROGRESS).roundToInt()
        } catch (e: Exception) {
            0
        }

        controls?.progressBar?.progress?.let {
            if (progress > it) controls?.progressBar?.progress = progress
        }
    }

    private fun updateProgressTextView() {
        if (controls?.progressTextView?.text.toString() < mediaPlayer.time.milliSecondsToString()) {
            controls?.progressTextView?.text = mediaPlayer.time.milliSecondsToString()
        }
    }

    private fun setProgressBarListener() {
        var isFromUser = false
        controls?.progressBar?.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    isFromUser = fromUser
                    if (progress > currentProgress && !isEndReached) {
                        currentProgress = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.d("onStartTrackingTouch", seekBar.toString())
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (isFromUser) setProgress(seekBar.progress)
                }
            }
        )
    }

    private fun managePlayButton() {
        isPausedByUser = if (isPlaying) {
            pause()
            true
        } else {
            play()
            false
        }
    }

    private fun attachView(view: SurfaceView) {
        surfaceView = view
        if (mediaPlayer.vlcVout.areViewsAttached()) mediaPlayer.vlcVout.detachViews()
        mediaPlayer.vlcVout.setVideoView(view)
        mediaPlayer.vlcVout.attachViews { _, _, _, _, _, _, _ ->
            setWindowSize(view)
        }
    }

    private fun setMedia(url: String) {
        currentUrl = url
        currentMedia = Media(libVLC, Uri.parse(url))
        setMediaOptions()
        mediaPlayer.media = currentMedia
    }

    private fun setMediaOptions() {
        currentMedia?.setHWDecoderEnabled(true, true)
        currentMedia?.addOption(CLOCK_JITTER)
        currentMedia?.addOption(CLOCK_SYNC)
        currentMedia?.addOption(NETWORK_CACHING)
        currentMedia?.addOption(FILE_CACHING)
    }

    private fun setWindowSize(view: SurfaceView) {
        val width: Int = view.measuredWidth
        val height: Int = view.measuredHeight
        mediaPlayer.vlcVout.setWindowSize(width, height)
    }

    private fun changeAspectRatio() {
        when (mediaPlayer.aspectRatio) {
            ASPECT_RATIO_4_3 -> mediaPlayer.aspectRatio = ASPECT_RATIO_16_9
            ASPECT_RATIO_16_9 -> mediaPlayer.aspectRatio = ASPECT_RATIO_21_9
            ASPECT_RATIO_21_9 -> mediaPlayer.aspectRatio = null
            else -> mediaPlayer.aspectRatio = ASPECT_RATIO_4_3
        }
    }

    private fun detachAllViews() {
        while (mediaPlayer.vlcVout.areViewsAttached()) {
            mediaPlayer.vlcVout.detachViews()
        }
    }

    private fun releaseMedia() {
        currentMedia?.run {
            while (!isReleased) release()
        }
    }

    companion object {
        var currentProgress = 0

        private const val TOTAL_PROGRESS = 100

        const val CLOCK_JITTER = ":clock-jitter=0"
        const val CLOCK_SYNC = ":clock-synchro=0"
        const val NETWORK_CACHING = ":network-caching=1000"
        const val FILE_CACHING = ":file-caching=1000"

        const val ASPECT_RATIO_4_3 = "4:3"
        const val ASPECT_RATIO_16_9 = "16:9"
        const val ASPECT_RATIO_21_9 = "21:9"

        private const val DELAY_ON_ERROR = 500L
    }
}
