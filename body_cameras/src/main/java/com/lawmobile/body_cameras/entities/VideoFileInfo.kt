package com.lawmobile.body_cameras.entities

data class VideoFileInfo(
    val rval: Int = 0,
    val msg_id: Int = 0,
    val size: Long = 0,
    val date: String = "",
    val resolution: String = "",
    val duration: Int = 0,
    val media_type: String = "",
    var urlVideo: String = ""
)
