package com.lawmobile.presentation.entities

import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

data class MediaPlayerControls(
    val playButton: ImageButton,
    val progressTextView: TextView,
    val durationTextView: TextView,
    val progressBar: SeekBar,
    val aspectRatioButton: ImageButton
)
