package com.lawmobile.body_cameras.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.lawmobile.body_cameras.cache.CameraServiceCache
import com.lawmobile.body_cameras.constants.CameraConstants
import com.lawmobile.body_cameras.constants.CameraConstants.FILES_MAIN_PATH
import com.lawmobile.body_cameras.constants.CameraConstants.NAME_SETUP_CONFIGURATION
import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.body_cameras.entities.InformationToSaveMetadata
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.SetupConfiguration
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.enums.XCameraCommandCodes
import com.lawmobile.body_cameras.x1.entities.X1FileResponse
import com.lawmobile.body_cameras.x1.entities.XCameraCommand
import com.safefleet.mobile.kotlin_commons.extensions.convertToObject
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.extensions.md5
import com.safefleet.mobile.kotlin_commons.extensions.mergeWith
import com.safefleet.mobile.kotlin_commons.extensions.toJsonObject
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MetadataHelper(
    private val format: Gson,
    private var fileInformationHelper: FileInformationHelper,
    private val commandHelper: CommandHelper
) {

    suspend fun saveVideoInformation(
        videoInformation: VideoInformation
    ): Result<Unit> = suspendCancellableCoroutine { coroutineTask ->
        CoroutineScope(Dispatchers.IO).launch {
            val response = saveJsonVideoInformation(videoInformation)
            with(response) {
                delay(100)
                doIfSuccess {
                    val saveResult = saveLogVideoInformation(videoInformation)
                    coroutineTask.resume(saveResult)
                }
                doIfError { coroutineTask.resume(Result.Error(it)) }
            }
        }
    }

    suspend fun savePhotoInformationInOneFile(
        list: List<PhotoInformation>
    ): Result<Unit> {
        val informationToSaveMetadataX1 = InformationToSaveMetadata(
            format.toJson(list),
            CameraConstants.FILES_MISC_PATH_FOLDER + CameraConstants.PHOTO_NAME_JSON
        )
        return saveMetadataFiles(informationToSaveMetadataX1)
    }

    suspend fun savePhotoInformation(
        photoInformation: PhotoInformation
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            delay(200)
            val responseJson = saveJsonSnapshotInformation(photoInformation)
            responseJson.doIfSuccess {
                delay(300)
                return@withContext saveLogSnapshotInformation(photoInformation)
            }
            return@withContext responseJson
        }

    suspend fun saveAudioInformation(audioInformation: AudioInformation): Result<Unit> =
        withContext(Dispatchers.IO) {
            delay(200)
            val responseJson = saveJsonAudioInformation(audioInformation)
            responseJson.doIfSuccess {
                delay(300)
                return@withContext saveLogAudioInformation(audioInformation)
            }
            return@withContext responseJson
        }

    suspend fun saveConfigurationFile(setupConfiguration: SetupConfiguration): Result<Unit> =
        withContext(Dispatchers.IO) {
            val information = InformationToSaveMetadata(
                format.toJson(setupConfiguration),
                FILES_MAIN_PATH + NAME_SETUP_CONFIGURATION
            )
            return@withContext saveMetadataFiles(information)
        }

    fun getPhotoInformationList(bytes: ByteArray): Result<List<PhotoInformation>> {
        return try {
            val stringJSON = String(bytes)
            val objectList =
                format.fromJson(stringJSON, Array<PhotoInformation>::class.java).asList()
            Result.Success(objectList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getCategoryInfoList(bytes: ByteArray): Result<List<CameraCatalog>> {
        return try {
            val stringJSON = String(bytes)
            val objectList =
                format.fromJson(stringJSON, Array<CameraCatalog>::class.java).asList()
            Result.Success(objectList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun saveLogVideoInformation(videoInformation: VideoInformation): Result<Unit> {
        val cameraInformationLog = InformationToSaveMetadata(
            videoInformation.getMetadataLog(),
            videoInformation.getLogPath()
        )
        return getResultWithAttempts(ATTEMPTS_RETRY_REQUEST) {
            saveMetadataFiles(cameraInformationLog)
        }
    }

    private suspend fun saveJsonVideoInformation(videoInformation: VideoInformation): Result<Unit> {
        val jsonMetadataMerged = getMergedJsonMetadata(videoInformation)
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonMetadataString = gson.toJson(jsonMetadataMerged)
        val cameraInformation =
            InformationToSaveMetadata(jsonMetadataString, videoInformation.getJsonNamePath())

        return getResultWithAttempts(ATTEMPTS_RETRY_REQUEST) {
            saveMetadataFiles(cameraInformation).apply {
                doIfSuccess { CameraServiceCache.updateVideosInformationJson(jsonMetadataString) }
            }
        }
    }

    private fun getMergedJsonMetadata(videoInformation: VideoInformation): JsonObject {
        val jsonStringCached = CameraServiceCache.findVideoInformation(videoInformation.fileName)
        val jsonObjectCached = jsonStringCached.toJsonObject()
        val jsonObjectUpdated = videoInformation.toJsonObject()
        return if (jsonStringCached.isEmpty()) jsonObjectUpdated
        else jsonObjectUpdated mergeWith jsonObjectCached
    }

    private suspend fun saveLogSnapshotInformation(
        photoInformation: PhotoInformation
    ): Result<Unit> {
        val informationLog = InformationToSaveMetadata(
            photoInformation.getMetadataLog(),
            photoInformation.getPathLogName()
        )
        return saveMetadataFiles(informationLog)
    }

    private suspend fun saveJsonSnapshotInformation(
        photoInformation: PhotoInformation
    ): Result<Unit> {
        val informationSaveMetadata = InformationToSaveMetadata(
            format.toJson(photoInformation),
            photoInformation.getPathJSONName()
        )
        return saveMetadataFiles(informationSaveMetadata)
    }

    private suspend fun saveJsonAudioInformation(
        audioInformation: AudioInformation
    ): Result<Unit> {
        val informationSaveMetadata = InformationToSaveMetadata(
            format.toJson(audioInformation),
            audioInformation.getPathJSONName()
        )
        return saveMetadataFiles(informationSaveMetadata)
    }

    private suspend fun saveLogAudioInformation(
        audioInformation: AudioInformation
    ): Result<Unit> {
        val informationLog = InformationToSaveMetadata(
            audioInformation.getMetadataLog(),
            audioInformation.getPathLogName()
        )
        return saveMetadataFiles(informationLog)
    }

    private suspend fun saveMetadataFiles(informationToSaveMetadata: InformationToSaveMetadata): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val connection = fileInformationHelper.connectDataSocket()
                if (connection is Result.Error) return@withContext connection

                val commandUploadFile = getCommandUploadFile(informationToSaveMetadata)
                val response = commandHelper.isCommandSuccess(commandUploadFile)

                response.doIfSuccess {
                    fileInformationHelper.writeInOutputStream(informationToSaveMetadata.data.toByteArray())
                    return@withContext isNotificationPutFileArriveWithAttempts()
                }

                return@withContext response
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.Error(e)
            }
        }

    suspend fun isNotificationPutFileArriveWithAttempts(): Result<Unit> {
        var attemptsForNotification = 0
        var isPutFileNotificationArrive: Result<Boolean>
        do {
            isPutFileNotificationArrive = isPutFileNotificationArrive()
            attemptsForNotification++
            delay(250)
        } while (attemptsForNotification < ATTEMPTS_GET_NOTIFICATION && isPutFileNotificationArrive is Result.Error)

        return if (isPutFileNotificationArrive is Result.Success && isPutFileNotificationArrive.data) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception("Was not possible save the file"))
        }
    }

    private fun getCommandUploadFile(informationToSaveMetadata: InformationToSaveMetadata): XCameraCommand {
        return XCameraCommand.Builder()
            .addMsgId(XCameraCommandCodes.UPLOAD_FILE.commandValue)
            .addParam(informationToSaveMetadata.path)
            .addOffset(0)
            .addSize(informationToSaveMetadata.data.toByteArray().size)
            .addMD5Sum(informationToSaveMetadata.data.md5())
            .build()
    }

    private suspend fun isPutFileNotificationArrive(): Result<Boolean> {
        if (commandHelper.isInputStreamAvailable()) {
            val responseInputStream = commandHelper.readInputStream()
            val responseNotification = responseInputStream.convertToObject<X1FileResponse>()
            if (responseNotification is Result.Success && responseNotification.data.msg_id == NOTIFICATION_MSG_ID) {
                return Result.Success(responseNotification.data.type == PUT_FILE_COMPLETE_NOTIFICATION)
            }
        }
        return Result.Error(Exception("Does not arrive notification"))
    }

    companion object {
        private const val ATTEMPTS_RETRY_REQUEST = 3
        private const val ATTEMPTS_GET_NOTIFICATION = 10
        private const val PUT_FILE_COMPLETE_NOTIFICATION = "put_file_complete"
        private const val NOTIFICATION_MSG_ID = "7"
    }
}
