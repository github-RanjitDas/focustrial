package com.lawmobile.body_cameras.x2

import com.lawmobile.body_cameras.CameraServiceImpl
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.NotificationDictionary
import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.SetupConfiguration
import com.lawmobile.body_cameras.enums.XCameraCommandCodes
import com.lawmobile.body_cameras.enums.XCameraStatus
import com.lawmobile.body_cameras.utils.CommandHelper
import com.lawmobile.body_cameras.utils.FileInformationHelper
import com.lawmobile.body_cameras.utils.MetadataHelper
import com.lawmobile.body_cameras.utils.NotificationCameraHelper
import com.lawmobile.body_cameras.utils.NotificationCameraHelper.Companion.canReadNotification
import com.lawmobile.body_cameras.x1.entities.XCameraCommand
import com.safefleet.mobile.kotlin_commons.extensions.convertToObject
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class X2CameraServiceImpl(
    private val notificationCameraHelper: NotificationCameraHelper,
    private val fileInformationHelper: FileInformationHelper,
    private val commandHelper: CommandHelper,
    private val metadataHelper: MetadataHelper
) : CameraServiceImpl(
    fileInformationHelper,
    commandHelper,
    metadataHelper
) {

    override suspend fun getBatteryLevel(): Result<Int> {
        canReadNotification = false
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.GET_BATTERY.commandValue).build()

        val response = commandHelper.getResponseParam(command)
        canReadNotification = true
        response.doIfSuccess {
            return Result.Success(it.toInt())
        }

        return Result.Error(Exception("Command doesn't work"))
    }

    override suspend fun getCameraSettings(messageId: Int): Result<Int> {
        canReadNotification = false
        val command =
            XCameraCommand.Builder().addMsgId(messageId).build()

        val response = commandHelper.getResponseParam(command)
        canReadNotification = true
        response.doIfSuccess {
            return Result.Success(it.toInt())
        }

        return Result.Error(Exception("Command doesn't work"))
    }

    override suspend fun getBodyWornDiagnosis(): Result<Boolean> {
        delay(DIAGNOSIS_DELAY)
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.GET_DIAGNOSIS.commandValue)
                .build()
        val commandResponse = commandHelper.isCommandSuccess(command)
        commandResponse.doIfSuccess {
            var attempts = 1
            do {
                delay(DIAGNOSIS_LOOP_DELAY)
                val notificationResponse =
                    commandHelper.readInputStream().convertToObject<NotificationResponse>()
                notificationResponse.doIfSuccess {
                    return getResultOfDiagnosis(it)
                }

                attempts++
            } while (attempts <= 3)
            Result.Error(Exception("Error getting body-worn diagnosis"))
        }
        return Result.Error(Exception("Error getting body-worn diagnosis"))
    }

    private fun getResultOfDiagnosis(it: NotificationResponse): Result<Boolean> {
        return if (it.param == XCameraStatus.DIAGNOSIS_PASS.value) {
            Result.Success(true)
        } else {
            Result.Success(false)
        }
    }

    override suspend fun getLogEvents(): Result<List<LogEvent>> {
        canReadNotification = false
        val response = fileInformationHelper.getWarningsLog()
        canReadNotification = true
        return response
    }

    override suspend fun getNotificationDictionary(): Result<List<NotificationDictionary>> {
        return fileInformationHelper.getNotificationDictionary()
    }

    override fun reviewIfArriveNotificationInCMDSocket() {
        notificationCameraHelper.reviewIfSocketHasBytesAvailableForNotification(
            notificationCallback = {
                arriveNotificationFromCamera?.invoke(it)
            }
        )
    }

    override suspend fun savePhotoMetadata(photoInformation: PhotoInformation): Result<Unit> {
        canReadNotification = false
        val cameraFile = CameraFile(
            name = photoInformation.fileName,
            nameFolder = photoInformation.nameFolder!!,
            path = "",
            date = ""
        )
        val photoMetadataResponse = getPhotoMetadata(cameraFile)
        delay(SAVE_PHOTO_METADATA_DELAY)
        photoMetadataResponse.doIfSuccess { oldInformation ->
            oldInformation.metadata = photoInformation.metadata
            oldInformation.nameFolder = photoInformation.nameFolder
            return saveMetadataWithPhotoInformation(oldInformation)
        }

        return saveMetadataWithPhotoInformation(photoInformation)
    }

    private suspend fun saveMetadataWithPhotoInformation(oldInformation: PhotoInformation): Result<Unit> {
        val response = metadataHelper.savePhotoInformation(oldInformation)
        canReadNotification = true
        return response
    }

    override suspend fun setSetupConfiguration(setupConfiguration: SetupConfiguration): Result<Unit> {
        val responseWrite = metadataHelper.saveConfigurationFile(setupConfiguration)
        responseWrite.doIfSuccess {
            delay(SETUP_CONFIGURATION_DELAY)
            val response = fileInformationHelper.getInformationSetupConfiguration()
            response.doIfSuccess { saveSetupConfiguration ->
                if (saveSetupConfiguration == setupConfiguration) return Result.Success(Unit)
            }

            return Result.Error(Exception("Error in get file"))
        }

        return responseWrite
    }

    override suspend fun turnOnBluetooth(): Result<Unit> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.TURN_ON_BLUETOOTH.commandValue)
                .build()
        return commandHelper.isCommandSuccess(command)
    }

    override suspend fun turnOffBluetooth(): Result<Unit> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.TURN_OFF_BLUETOOTH.commandValue)
                .build()
        return commandHelper.isCommandSuccess(command)
    }

    companion object {
        private const val DIAGNOSIS_DELAY = 200L
        private const val DIAGNOSIS_LOOP_DELAY = 500L
        private const val SETUP_CONFIGURATION_DELAY = 1000L
        private const val SAVE_PHOTO_METADATA_DELAY = 250L
    }
}
