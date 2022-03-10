package com.lawmobile.body_cameras.entities

import com.lawmobile.body_cameras.constants.CameraConstants

data class AudioInformation(
    var fileName: String = "",
    var officerId: String? = null,
    var path: String? = null,
    var nameFolder: String? = null,
    var x1sn: String? = null,
    var startTime: String? = null,
    var hash: HashMetadataFile? = null,
    var x2sn: String? = null,
    var metadata: AudioMetadata? = null
) {

    fun getMetadataLog(): String {
        val content = StringBuilder()
        metadata?.run {
            content.append(String.format("\r\nPartner_ID Changed,%s,%s", partnerID ?: "", fileName))
        }

        return content.toString()
    }

    fun getPathJSONName() = "${CameraConstants.FILES_MAIN_PATH_FOLDER}${nameFolder}${getJsonName()}"
    fun getPathLogName() =
        "${CameraConstants.FILES_MAIN_PATH_FOLDER}${nameFolder}${getFileNameWithLogExtension()}"

    private fun getFileNameWithLogExtension() =
        if (fileContainsExtension()) {
            "${getNameUnitPoint()}.a"
        } else "$fileName.a"

    private fun fileContainsExtension() = fileName.contains(".")
    private fun getNameUnitPoint() = fileName.substring(0, fileName.lastIndexOf("."))
    private fun getJsonName() = fileName.replace("WAV", "JSON")
}
