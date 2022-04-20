package com.lawmobile.body_cameras

import com.lawmobile.body_cameras.cache.CameraServiceCache
import com.lawmobile.body_cameras.constants.CameraConstants
import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.BWCConnectionParams
import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.CameraUser
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.SetupConfiguration
import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.enums.CameraType
import com.lawmobile.body_cameras.enums.FileListType
import com.lawmobile.body_cameras.enums.XCameraCommandCodes
import com.lawmobile.body_cameras.enums.XCameraCommandTypes
import com.lawmobile.body_cameras.enums.XCameraStatus
import com.lawmobile.body_cameras.utils.CommandHelper
import com.lawmobile.body_cameras.utils.FileInformationHelper
import com.lawmobile.body_cameras.utils.MetadataHelper
import com.lawmobile.body_cameras.utils.NotificationCameraHelper.Companion.canReadNotification
import com.lawmobile.body_cameras.x1.entities.XCameraCommand
import com.safefleet.mobile.kotlin_commons.extensions.convertToObject
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts

open class CameraServiceImpl(
    private val fileInformationHelper: FileInformationHelper,
    private val commandHelper: CommandHelper,
    private val metadataHelper: MetadataHelper
) : CameraService {

    override var progressPairingCamera: ((Result<Int>) -> Unit)? = null
    override var arriveNotificationFromCamera: ((NotificationResponse) -> Unit)? = null

    override suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String) {
        when {
            !connectCameraCMDSocket(hostnameToConnect) -> return
            !isPossibleGetSessionToken() -> return
            !resetViewFinder(ipAddressClient) -> return
            !verifyDeviceInfo(ipAddressClient) -> return
            !verifyClientInfo(ipAddressClient) -> return
        }
        BWCConnectionParams.saveConnectionParams(hostnameToConnect, ipAddressClient)
    }

    private suspend fun verifyClientInfo(
        ipAddressClient: String
    ) = verifyCommandWithProgress(
        XCameraCommandCodes.VERIFY_CLIENT_INFO,
        CameraConstants.PROGRESS_CLIENT_INFO, ipAddressClient
    )

    private suspend fun verifyDeviceInfo(
        ipAddressClient: String
    ) = verifyCommandWithProgress(
        XCameraCommandCodes.VERIFY_DEVICE_INFO,
        CameraConstants.PROGRESS_DEVICE_INFO, ipAddressClient
    )

    override suspend fun resetViewFinder(
        ipAddressClient: String
    ): Boolean {
        return if (!isRecording()) {
            verifyCommandWithProgress(
                XCameraCommandCodes.RESET_VIEW_FINDER,
                CameraConstants.PROGRESS_LIVE_STREAM_INFO,
                ipAddressClient
            )
        } else true
    }

    override suspend fun isRecording(): Boolean {
        val command = XCameraCommand {
            addMsgId(XCameraCommandCodes.GET_SETTING.commandValue)
            addType(XCameraCommandTypes.APP_STATUS.value)
        }

        return when (val response = commandHelper.getResponseParam(command)) {
            is Result.Success -> response.data == XCameraStatus.RECORDING.value
            is Result.Error -> false
        }
    }

    override suspend fun getUserResponse(): Result<CameraUser> {
        canReadNotification = false
        val fileInformation =
            fileInformationHelper.getFileInformation(CameraConstants.PATH_DOWNLOAD_OFFICER_INFO)
        canReadNotification = true
        return when (fileInformation) {
            is Result.Success -> CameraUser.buildFromString(String(fileInformation.data))
            is Result.Error -> fileInformation
        }
    }

    override fun getUrlForLiveStream(): String {
        return CameraConstants.PROTOCOL_LIVE + BWCConnectionParams.hostnameToConnect + CameraConstants.COMPLEMENT_LIVE
    }

    override suspend fun takePhoto(): Result<Unit> {
        canReadNotification = false
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.TAKE_PHOTO.commandValue).build()
        val response = commandHelper.isCommandSuccess(command)
        if (response is Result.Error) {
            val numberOfPhotosResult =
                getResultWithAttempts(CameraConstants.ATTEMPTS_RETRY_REQUEST) { commandHelper.getNumberOfSnapshots() }

            canReadNotification = true

            numberOfPhotosResult.run {
                doIfSuccess {
                    return if (it > numberOfPhotosInCamera) {
                        numberOfPhotosInCamera = it
                        Result.Success(Unit)
                    } else response
                }
            }
        }

        canReadNotification = true
        return response
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.START_RECORD_VIDEO.commandValue)
                .build()
        return commandHelper.isCommandSuccess(command)
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.STOP_RECORD_VIDEO.commandValue)
                .build()
        return commandHelper.isCommandSuccess(command)
    }

    override fun isCameraConnected(gatewayConnection: String): Boolean {
        if (CommandHelper.isTokenInvalid) return false
        return BWCConnectionParams.hostnameToConnect == gatewayConnection
    }

    override suspend fun disconnectCamera(): Result<Unit> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.DISCONNECT_X_CAMERA.commandValue)
                .build()
        return commandHelper.isCommandSuccess(command)
    }

    override fun cleanCacheFiles() = CameraServiceCache.cleanCache()

    override suspend fun getListOfVideos(): Result<FileResponseWithErrors> {
        return commandHelper.getMediaListOfType(FileListType.VIDEO)
    }

    override suspend fun getListOfImages(): Result<FileResponseWithErrors> {
        return commandHelper.getMediaListOfType(FileListType.SNAPSHOT)
    }

    override suspend fun getListOfAudios(): Result<FileResponseWithErrors> {
        return commandHelper.getMediaListOfType(FileListType.AUDIO)
    }

    override suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo> {
        canReadNotification = false
        val response = commandHelper.getInfoMediaFromCMDSocket(cameraFile.path + cameraFile.name)
        canReadNotification = true
        return response
    }

    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> =
        getFileBytes(cameraFile)

    private suspend fun getFileBytes(cameraFile: CameraFile): Result<ByteArray> {
        canReadNotification = false
        val result = getResultWithAttempts(ATTEMPTS_IN_RETRY, FILE_BYTES_ERROR_DELAY) {
            fileInformationHelper.getFileInformation(cameraFile.getCompletePath())
        }
        canReadNotification = true
        result.doIfSuccess {
            if (it.isEmpty()) return Result.Error(Exception("Could not retrieve the requested file"))
        }
        return result
    }

    override suspend fun getCatalogInfo(): Result<List<CameraCatalog>> {
        canReadNotification = false
        getResultWithAttempts(ATTEMPTS_IN_RETRY) {
            commandHelper.getNumberOfSnapshots()
        }.doIfSuccess {
            numberOfPhotosInCamera = it
        }

        getResultWithAttempts(ATTEMPTS_IN_RETRY) {
            fileInformationHelper.getFileInformation(CameraConstants.PATH_DOWNLOAD_CATALOGS)
        }.doIfSuccess {
            canReadNotification = true
            return CameraCatalog.createInstanceListWithStringX1(String(it))
        }
        canReadNotification = true
        return Result.Error(Exception("Error in get events"))
    }

    override suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit> {
        canReadNotification = false
        val response = getResultWithAttempts(ATTEMPTS_IN_RETRY) {
            metadataHelper.saveVideoInformation(videoInformation)
        }
        canReadNotification = true

        return response
    }

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<VideoInformation> {
        canReadNotification = false
        val actualName = fileName.replace("MP4", "JSON")
        val fileInformation = getResultWithAttempts(ATTEMPTS_IN_RETRY) {
            fileInformationHelper.getFileInformation("${CameraConstants.FILES_MAIN_PATH_FOLDER}${folderName}$actualName")
        }
        canReadNotification = true

        fileInformation.doIfSuccess {
            return if (it.isEmpty()) Result.Success(VideoInformation(fileName))
            else {
                val jsonString = String(it)
                CameraServiceCache.updateVideosInformationJson(jsonString)
                jsonString.convertToObject()
            }
        }
        return Result.Error(Exception("Error in get file"))
    }

    override suspend fun getMetadataOfPhotos(): Result<List<PhotoInformation>> {
        canReadNotification = false
        val photoMetadataBytes =
            fileInformationHelper.getFileInformation(CameraConstants.FILES_MISC_PATH_FOLDER + CameraConstants.PHOTO_NAME_JSON)
        canReadNotification = true
        photoMetadataBytes.doIfSuccess {
            if (it.isEmpty()) return Result.Success(emptyList())
            return metadataHelper.getPhotoInformationList(it)
        }
        return photoMetadataBytes as Result.Error
    }

    override suspend fun getPhotoMetadata(cameraFile: CameraFile): Result<PhotoInformation> {
        canReadNotification = false
        val fileInformation =
            fileInformationHelper.getFileInformation(cameraFile.getJsonPathOfImage())
        canReadNotification = true
        fileInformation.doIfSuccess {
            if (it.isEmpty()) return Result.Success(PhotoInformation(cameraFile.name))
            return String(it).convertToObject()
        }
        return fileInformation as Result.Error
    }

    override suspend fun saveAllPhotoMetadata(list: List<PhotoInformation>): Result<Unit> {
        canReadNotification = false
        val response = getResultWithAttempts(ATTEMPTS_IN_RETRY, FILE_BYTES_ERROR_DELAY) {
            metadataHelper.savePhotoInformationInOneFile(list)
        }
        canReadNotification = true
        return response
    }

    override suspend fun savePhotoMetadata(photoInformation: PhotoInformation): Result<Unit> {
        canReadNotification = false
        val response = getResultWithAttempts(ATTEMPTS_IN_RETRY, FILE_BYTES_ERROR_DELAY) {
            metadataHelper.savePhotoInformation(photoInformation)
        }
        canReadNotification = true
        return response
    }

    override suspend fun getFreeStorage(): Result<String> {
        val command = XCameraCommand {
            addMsgId(XCameraCommandCodes.GET_SPACE.commandValue)
            addType(XCameraCommandTypes.FREE_SPACE.value)
        }
        return commandHelper.getResponseParam(command)
    }

    override suspend fun getTotalStorage(): Result<String> {
        val command = XCameraCommand {
            addMsgId(XCameraCommandCodes.GET_SPACE.commandValue)
            addType(XCameraCommandTypes.TOTAL_SPACE.value)
        }
        return commandHelper.getResponseParam(command)
    }

    override suspend fun deleteFile(fileName: String): Result<Unit> {
        val commandX1 = XCameraCommand {
            addMsgId(XCameraCommandCodes.DEL_FILE.commandValue)
            addParam(fileName)
        }
        return commandHelper.isCommandSuccess(commandX1)
    }

    override suspend fun getBatteryLevel(): Result<Int> {
        canReadNotification = false
        val response = fileInformationHelper.getBatteryLevelFromLog()
        canReadNotification = true
        return response
    }

    override suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit> =
        commandHelper.connectCMDSocket(hostnameToConnect)

    override suspend fun getLogEvents(): Result<List<LogEvent>> {
        return Result.Error(Exception(FEATURE_NOT_SUPPORTED))
    }

    override fun getCanReadNotification(): Boolean {
        return canReadNotification
    }

    override suspend fun getBodyWornDiagnosis(): Result<Boolean> {
        return Result.Error(Exception(FEATURE_NOT_SUPPORTED))
    }

    override suspend fun setSetupConfiguration(setupConfiguration: SetupConfiguration): Result<Unit> {
        return Result.Error(Exception(FEATURE_NOT_SUPPORTED))
    }

    override fun reviewIfArriveNotificationInCMDSocket() {
        // Implemented in every Camera
    }

    override suspend fun getCameraType(): Result<CameraType> {
        val response = getResultWithAttempts(3) {
            fileInformationHelper.getFileInformation(CameraConstants.PATH_VERSION)
        }

        var fileBytes = ByteArray(0)
        response.doIfSuccess { fileBytes = it }

        return if (fileBytes.isEmpty()) Result.Success(CameraType.X1)
        else Result.Success(CameraType.X2)
    }

    private suspend fun connectCameraCMDSocket(hostnameToConnect: String): Boolean {
        return when (
            val connectCameraCMD = commandHelper.connectCMDSocket(hostnameToConnect)
        ) {
            is Result.Success -> {
                progressPairingCamera?.invoke(Result.Success(CameraConstants.PROGRESS_CONNECT_CAMERA))
                true
            }
            is Result.Error -> {
                progressPairingCamera?.invoke(Result.Error(connectCameraCMD.exception))
                false
            }
        }
    }

    private suspend fun isPossibleGetSessionToken(): Boolean {
        var result = false
        with(commandHelper.getSessionToken()) {
            doIfSuccess {
                BWCConnectionParams.sessionToken = it
                progressPairingCamera?.invoke(Result.Success(CameraConstants.PROGRESS_SESSION_TOKEN))
                result = true
            }
            doIfError {
                progressPairingCamera?.invoke(Result.Error(it))
                result = false
            }
        }
        return result
    }

    private suspend fun verifyCommandWithProgress(
        xCameraCommandCodes: XCameraCommandCodes,
        progress: Int,
        ipAddressClient: String
    ): Boolean {
        val command = XCameraCommand {
            addMsgId(xCameraCommandCodes.commandValue)
            if (xCameraCommandCodes == XCameraCommandCodes.VERIFY_CLIENT_INFO) {
                addType(XCameraCommandTypes.CLIENT_CONNECTION_TCP.value)
                addParam(ipAddressClient)
            }
        }

        val verification = commandHelper.isCommandSuccess(command)

        verification.doIfSuccess {
            progressPairingCamera?.invoke(Result.Success(progress))
            return true
        }

        progressPairingCamera?.invoke(Result.Error(Exception("Error in step ${xCameraCommandCodes.commandValue}")))
        return false
    }

    override suspend fun getAudioBytes(cameraFile: CameraFile): Result<ByteArray> =
        getFileBytes(cameraFile)

    override suspend fun getAudioMetadata(cameraFile: CameraFile): Result<AudioInformation> {
        canReadNotification = false
        val fileInformation =
            fileInformationHelper.getFileInformation(cameraFile.getJsonPathOfAudio())
        canReadNotification = true
        fileInformation.doIfSuccess {
            if (it.isEmpty()) return Result.Success(AudioInformation(cameraFile.name))
            return String(it).convertToObject()
        }
        return Result.Error(Exception("Error getting audio metadata"))
    }

    override suspend fun saveAudioMetadata(audioInformation: AudioInformation): Result<Unit> {
        canReadNotification = false
        val response = metadataHelper.saveAudioInformation(audioInformation)
        canReadNotification = true
        return response
    }

    override suspend fun getAssociatedVideos(cameraFile: CameraFile): Result<List<CameraFile>> {
        return try {
            if (CameraServiceCache.isVideosInformationEmpty()) getAssociatedVideosFromCamera(
                cameraFile
            )
            else getAssociatedVideosFromCache(cameraFile)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun getAssociatedVideosFromCamera(cameraFile: CameraFile): Result<List<CameraFile>> {
        getListOfVideos().run {
            doIfSuccess { fileList ->
                fileList.items.forEach { file ->
                    getVideoMetadata(file.name, file.nameFolder).doIfError { throw it }
                }
            }
            doIfError { throw it }
        }
        return getAssociatedVideos(cameraFile)
    }

    private fun getAssociatedVideosFromCache(cameraFile: CameraFile): Result<List<CameraFile>> {
        val associatedVideos = mutableListOf<CameraFile>()
        val videoInformationStringList =
            CameraServiceCache.filterVideoInformationList(cameraFile.name)
        videoInformationStringList.forEach { videoInformationString ->
            videoInformationString.convertToObject<VideoInformation>().run {
                doIfSuccess {
                    val filteredVideos = CameraServiceCache.filterVideos(it.fileName)
                    associatedVideos.addAll(filteredVideos)
                }
                doIfError { throw it }
            }
        }
        return Result.Success(associatedVideos)
    }

    override suspend fun startCovertMode(): Result<Unit> {
        val command = XCameraCommand.Builder().addMsgId(XCameraCommandCodes.COVERT_MODE_START.commandValue).build()
        return commandHelper.isCommandSuccess(command)
    }

    override suspend fun stopCovertMode(): Result<Unit> {
        val command = XCameraCommand.Builder().addMsgId(XCameraCommandCodes.COVER_MODE_STOP.commandValue).build()
        return commandHelper.isCommandSuccess(command)
    }

    override suspend fun turnOnBluetooth(): Result<Unit> {
        val command = XCameraCommand.Builder().addMsgId(XCameraCommandCodes.TURN_ON_BLUETOOTH.commandValue).build()
        return commandHelper.isCommandSuccess(command)
    }

    override suspend fun turnOffBluetooth(): Result<Unit> {
        val command = XCameraCommand.Builder().addMsgId(XCameraCommandCodes.TURN_OFF_BLUETOOTH.commandValue).build()
        return commandHelper.isCommandSuccess(command)
    }

    companion object {
        private var numberOfPhotosInCamera = 0
        private const val ATTEMPTS_IN_RETRY = 5
        private const val FILE_BYTES_ERROR_DELAY = 500L
        private const val FEATURE_NOT_SUPPORTED = "This feature is not supported yet"
    }
}
