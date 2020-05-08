package com.lawmobile.presentation.utils

import android.net.Uri
import android.view.SurfaceView
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer


class VLCMediaPlayer(private val libVLC: LibVLC, private val mediaPlayer: MediaPlayer) {

    fun createMediaPlayer(url: String, view: SurfaceView) {
        val media = Media(libVLC, Uri.parse(url))
        media.setHWDecoderEnabled(true, true)
        media.addOption(":clock-jitter=0")
        media.addOption(":clock-synchro=0")
        media.addOption(":network-caching=1000")
        media.addOption(":file-caching=1000")
        mediaPlayer.media = media
        media.release()
        if (mediaPlayer.vlcVout.areViewsAttached()) mediaPlayer.vlcVout.detachViews()
        mediaPlayer.vlcVout.setVideoView(view)
        mediaPlayer.vlcVout.attachViews { _, _, _, _, _, _, _ ->
            setSizeInMediaPlayer(view)
        }
    }

    fun setSizeInMediaPlayer(view: SurfaceView) {
        val width: Int = view.measuredWidth
        val height: Int = view.measuredHeight
        mediaPlayer.vlcVout.setWindowSize(width, height)
    }

    fun playMediaPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.play()
        }
    }

    fun stopMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.vlcVout.detachViews()
    }

    fun pauseMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun isMediaPlayerPaying() = mediaPlayer.isPlaying

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

    private fun getAspectRatio() = mediaPlayer.aspectRatio

    private fun setAspectRatio(aspectRadio: String? = null) {
        mediaPlayer.aspectRatio = aspectRadio
    }

    companion object {
        const val ASPECT_RATIO_4_3 = "4:3"
        const val ASPECT_RATIO_16_9 = "16:9"
        const val ASPECT_RATIO_21_9 = "21:9"
    }

}