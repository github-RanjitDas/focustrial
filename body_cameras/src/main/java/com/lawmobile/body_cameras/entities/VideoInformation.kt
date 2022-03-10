package com.lawmobile.body_cameras.entities

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lawmobile.body_cameras.constants.CameraConstants
import com.lawmobile.body_cameras.x1.entities.X1VideoQuality

data class VideoInformation(
    var fileName: String,
    var officerId: String? = null,
    var path: String? = null,
    var nameFolder: String? = null,
    var x1sn: String? = null,
    var metadata: VideoMetadata? = null,
    var annotations: List<Annotation>? = null,
    var associatedFiles: List<AssociatedFile>? = null,
    var endTime: String? = null,
    var gmtOffset: String? = null,
    var hash: HashMetadataFile? = null,
    var preEvent: String? = null,
    var startTime: String? = null,
    var videoSpecs: String? = null,
    var x2sn: String? = null,
    var trigger: String? = null
) {

    fun getMetadataLog(): String {
        val content = StringBuilder()
        val nameInLog = getHighQualityName()
        metadata?.run {
            content.append(
                String.format(
                    "\r\nCaseID Changed,%s,%s,%s,,",
                    caseNumber ?: "",
                    nameInLog,
                    officerId
                )
            )
            content.append(
                String.format(
                    "\r\nCaseID2 Changed,%s,%s,%s,,",
                    caseNumber2 ?: "",
                    nameInLog,
                    officerId
                )
            )
            content.append(
                String.format(
                    "\r\nDispatchNo Changed,%s,%s",
                    dispatchNumber ?: "",
                    nameInLog
                )
            )
            content.append(
                String.format(
                    "\r\nDispatchNo2 Changed,%s,%s",
                    dispatchNumber2 ?: "",
                    nameInLog
                )
            )
            content.append(String.format("\r\nDL Changed,%s,%s", driverLicense ?: "", nameInLog))
            content.append(String.format("\r\nLP Changed,%s,%s", licensePlate ?: "", nameInLog))
            content.append(
                String.format(
                    "\r\nEventType Changed2,%s,%s",
                    event?.name ?: "",
                    nameInLog
                )
            )
            content.append(
                String.format(
                    "\r\nEventType Changed,0,%s,%s,*%s,*%s",
                    nameInLog,
                    officerId,
                    event?.name ?: "",
                    event?.id ?: ""
                )
            )
            content.append(
                String.format(
                    "\r\nFirst_Name Changed,%s,%s",
                    firstName ?: "",
                    nameInLog
                )
            )
            content.append(String.format("\r\nGender Changed,%s,%s", gender ?: "", nameInLog))
            content.append(String.format("\r\nLast_Name Changed,%s,%s", lastName ?: "", nameInLog))
            content.append(String.format("\r\nLocation Changed,%s,%s", location ?: "", nameInLog))
            content.append(
                String.format(
                    "\r\nPartner_ID Changed,%s,%s",
                    partnerID ?: "",
                    nameInLog
                )
            )
            content.append(String.format("\r\nRace Changed,%s,%s", race ?: "", nameInLog))
            content.append(String.format("\r\nNotes Added,%s,%s", remarks ?: "", nameInLog))
            content.append(
                String.format(
                    "\r\nTicketNo Changed,%s,%s",
                    ticketNumber ?: "",
                    nameInLog
                )
            )
            content.append(
                String.format(
                    "\r\nTicketNo2 Changed,%s,%s",
                    ticketNumber2 ?: "",
                    nameInLog
                )
            )
        }

        return content.toString()
    }

    fun getJsonNamePath() =
        "${CameraConstants.FILES_MAIN_PATH_FOLDER}${nameFolder}${getJsonName()}"

    fun getLogPath() =
        "${CameraConstants.FILES_MAIN_PATH_FOLDER}${nameFolder}${getFileNameWithLogExtension()}"

    fun toJsonObject(): JsonObject {
        val format = Gson()
        val json = format.toJson(this)
        return format.fromJson(json, JsonObject::class.java) ?: JsonObject()
    }

    private fun getFileNameWithLogExtension() =
        if (fileContainsExtension()) {
            "${getUnitPointName()}.a"
        } else "${getHighQualityName()}.a"

    private fun fileContainsExtension() = fileName.contains(".")

    private fun getUnitPointName() =
        getHighQualityName().substring(0, getHighQualityName().lastIndexOf("."))

    private fun getJsonName() = getHighQualityName().replace("MP4", "JSON")

    private fun getHighQualityName() =
        fileName
            .replace(
                X1VideoQuality.LOW_QUALITY.value,
                X1VideoQuality.HIGH_QUALITY.value
            )
            .replace(
                X1VideoQuality.LOW_QUALITY_CLIP_CAM.value,
                X1VideoQuality.HIGH_QUALITY_CLIP_CAM.value
            )
}
