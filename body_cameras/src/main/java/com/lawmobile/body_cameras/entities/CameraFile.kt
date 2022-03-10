package com.lawmobile.body_cameras.entities

import com.lawmobile.body_cameras.constants.CameraConstants.FILES_MAIN_PATH_FOLDER
import com.lawmobile.body_cameras.enums.FileListType
import com.lawmobile.body_cameras.x1.entities.X1VideoQuality
import java.io.Serializable

data class CameraFile(
    val name: String,
    val date: String,
    val path: String,
    val nameFolder: String
) : Serializable {

    fun isVideoHighQuality(): Boolean =
        name.endsWith(X1VideoQuality.HIGH_QUALITY.value) || name.endsWith(X1VideoQuality.HIGH_QUALITY_CLIP_CAM.value)
    fun isSnapshot(): Boolean = name.endsWith(FileListType.SNAPSHOT.value)
    fun isAudio(): Boolean = name.endsWith(FileListType.AUDIO.value)
    fun getJsonPathOfImage() = "$FILES_MAIN_PATH_FOLDER$nameFolder${getImageJsonName()}"
    fun getJsonPathOfAudio() = "$FILES_MAIN_PATH_FOLDER$nameFolder${getAudioJsonName()}"
    fun getCompletePath() = path + name
    private fun getImageJsonName() = name.replace("JPG", "JSON")
    private fun getAudioJsonName() = name.replace("WAV", "JSON")
}
