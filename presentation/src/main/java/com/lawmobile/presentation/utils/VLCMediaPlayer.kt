package com.lawmobile.presentation.utils

import android.net.Uri
import android.view.SurfaceView
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer


class VLCMediaPlayer(private val libVLC: LibVLC, private val mediaPlayer: MediaPlayer) {

    private var currentMedia: Media? = null
    private var currentTime: Long = 0

    fun createMediaPlayer(url: String, view: SurfaceView) {
        releaseMedia()
        setMediaToPlayer(url)
        detachAllViews()
        attachViewToPlayer(view)
    }

    private fun attachViewToPlayer(view: SurfaceView) {
        if (mediaPlayer.vlcVout.areViewsAttached()) mediaPlayer.vlcVout.detachViews()
        mediaPlayer.vlcVout.setVideoView(view)
        mediaPlayer.vlcVout.attachViews { _, _, _, _, _, _, _ ->
            setSizeInMediaPlayer(view)
        }
    }

    private fun setMediaToPlayer(url: String) {
        currentMedia = Media(libVLC, Uri.parse(url))
        currentMedia?.setHWDecoderEnabled(true, true)
        currentMedia?.addOption(CLOCK_JITTER)
        currentMedia?.addOption(CLOCK_SYNC)
        currentMedia?.addOption(NETWORK_CACHING)
        currentMedia?.addOption(FILE_CACHING)
        mediaPlayer.media = currentMedia
    }

    fun setMediaEventListener(listener: MediaPlayer.EventListener) {
        mediaPlayer.setEventListener(listener)
    }

    fun setSizeInMediaPlayer(view: SurfaceView) {
        val width: Int = view.measuredWidth
        val height: Int = view.measuredHeight
        mediaPlayer.vlcVout.setWindowSize(width, height)
    }

    fun playMediaPlayer() {
        if (!mediaPlayer.isPlaying) {
            if (currentTime != 0L) {
                mediaPlayer.time = currentTime
            }
            mediaPlayer.play()
        }
    }

    fun stopMediaPlayer() {
        mediaPlayer.stop()
        detachAllViews()
        releaseMedia()
    }

    fun pauseMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            currentTime = getTimeInMillisMediaPlayer()
            mediaPlayer.stop()
        }
    }

    fun isMediaPlayerPlaying() = mediaPlayer.isPlaying

    fun changeAspectRatio() {
        when (getAspectRatio()) {
            ASPECT_RATIO_4_3 -> setAspectRatio(ASPECT_RATIO_16_9)
            ASPECT_RATIO_16_9 -> setAspectRatio(ASPECT_RATIO_21_9)
            ASPECT_RATIO_21_9 -> setAspectRatio(null)
            else -> setAspectRatio(ASPECT_RATIO_4_3)
        }
    }

    fun getTimeInMillisMediaPlayer() = mediaPlayer.time

    fun setProgressMediaPlayer(progress: Float) {
        if (progress == 0F) {
            mediaPlayer.stop()
            mediaPlayer.play()
            return
        }

        mediaPlayer.time =
                ((progress.toDouble() * mediaPlayer.media.duration.toDouble()) / 100).toLong()

    }

    private fun detachAllViews() {
        while (mediaPlayer.vlcVout.areViewsAttached()) {
            mediaPlayer.vlcVout.detachViews()
        }
    }

    private fun releaseMedia() {
        currentMedia?.run {
            while (!isReleased) {
                currentMedia?.release()
            }
        }
    }

    private fun getAspectRatio() = mediaPlayer.aspectRatio

    private fun setAspectRatio(aspectRadio: String? = null) {
        mediaPlayer.aspectRatio = aspectRadio
    }

    companion object {
        const val CLOCK_JITTER = ":clock-jitter=0"
        const val CLOCK_SYNC = ":clock-synchro=0"
        const val NETWORK_CACHING = ":network-caching=1000"
        const val FILE_CACHING = ":file-caching=1000"
        const val ASPECT_RATIO_4_3 = "4:3"
        const val ASPECT_RATIO_16_9 = "16:9"
        const val ASPECT_RATIO_21_9 = "21:9"
    }

}