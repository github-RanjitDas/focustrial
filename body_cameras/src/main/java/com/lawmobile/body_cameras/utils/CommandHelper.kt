package com.lawmobile.body_cameras.utils

import com.google.gson.Gson
import com.lawmobile.body_cameras.cache.CameraServiceCache
import com.lawmobile.body_cameras.constants.CameraConstants
import com.lawmobile.body_cameras.entities.BWCConnectionParams
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.body_cameras.enums.FileListType
import com.lawmobile.body_cameras.enums.XCameraCommandCodes
import com.lawmobile.body_cameras.enums.XCameraCommandTypes
import com.lawmobile.body_cameras.socket.SocketHelper
import com.lawmobile.body_cameras.utils.NotificationCameraHelper.Companion.canReadNotification
import com.lawmobile.body_cameras.x1.entities.X1CameraResponse
import com.lawmobile.body_cameras.x1.entities.X1FileResponse
import com.lawmobile.body_cameras.x1.entities.XCameraCommand
import com.safefleet.mobile.kotlin_commons.extensions.convertToObject
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CommandHelper(
    private val format: Gson,
    private var commandSocketHelper: SocketHelper
) {

    fun isInputStreamAvailable() = commandSocketHelper.isInputStreamAvailable()

    suspend fun readInputStream() = commandSocketHelper.readInputStream()

    fun readInformationInputStream() = commandSocketHelper.readInformationInputStream()

    fun isSocketAvailable() = commandSocketHelper.isSocketAvailable

    fun isConnected() = commandSocketHelper.isConnected()

    suspend fun connectCMDSocket(hostnameToConnect: String): Result<Unit> =
        commandSocketHelper.connectSocket(CMD_PORT, hostnameToConnect)

    suspend fun getSessionToken(): Result<Int> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.START_SESSION.commandValue)
                .build()
        return when (
            val cameraConnectSessionX1Response: Result<X1CameraResponse> =
                getInformationByCommand(command)
        ) {
            is Result.Success -> {
                val id = cameraConnectSessionX1Response.data.param?.toInt() ?: return Result.Error(
                    Exception("Error in get token user")
                )
                Result.Success(id)
            }
            is Result.Error -> {
                cameraConnectSessionX1Response
            }
        }
    }

    suspend fun requestCommandDownloadFile(pathCamera: String): Result<X1FileResponse> {
        val command =
            XCameraCommand.Builder().addMsgId(XCameraCommandCodes.DOWNLOAD_FILE.commandValue)
                .addParam(pathCamera).addOffset(0).addFetchSize(0).build()
        return getInformationByCommand(command)
    }

    suspend fun isCommandSuccess(connectCommandCommand: XCameraCommand): Result<Unit> {
        canReadNotification = false
        val cameraConnectSessionX1Response: Result<X1CameraResponse> =
            getInformationByCommand(connectCommandCommand)
        val response = reviewIfCommandIsSuccess(cameraConnectSessionX1Response)
        canReadNotification = true
        return response
    }

    suspend fun getResponseParam(commandXCommand: XCameraCommand): Result<String> {
        canReadNotification = false
        val responseInformation = getInformationByCommand<X1CameraResponse>(commandXCommand)
        val response = getResponseParamWithResponseInformation(responseInformation)
        canReadNotification = true
        return response
    }

    suspend fun getInfoMediaFromCMDSocket(path: String): Result<VideoFileInfo> {
        val command = XCameraCommand.Builder()
            .addMsgId(XCameraCommandCodes.GET_MEDIA_INFO.commandValue)
            .addParam(path)
            .build()

        return when (val responseInformation = getInformationByCommand<VideoFileInfo>(command)) {
            is Result.Success -> {
                responseInformation.data.urlVideo =
                    CameraConstants.PROTOCOL_LIVE + BWCConnectionParams.hostnameToConnect + path
                responseInformation
            }
            is Result.Error -> responseInformation
        }
    }

    suspend fun getMediaListOfType(type: FileListType): Result<FileResponseWithErrors> {
        canReadNotification = false
        var numberOfFiles = 0

        getFileCountOfType(type).doIfSuccess { fileCount ->
            val allFilesWereRetrieved = checkIfAllFilesWereRetrieved(type, fileCount)
            if (allFilesWereRetrieved) {
                val files = getFilesOfType(type)
                return Result.Success(files)
            }
            numberOfFiles = fileCount
        }

        return requestFilesFromCameraWithAttempts(type, numberOfFiles)
    }

    private suspend fun getFilesWithFolders(folderNames: List<String>): FileResponseWithErrors {
        val responseFilesWithErrors = FileResponseWithErrors()
        folderNames.forEach { folderName ->
            val listItems = getResultWithAttempts(ATTEMPTS_RETRY_REQUEST, DELAY_ON_RETRY) {
                getFilesInFolder(CameraConstants.FILES_MAIN_PATH_FOLDER + folderName, folderName)
            }
            with(listItems) {
                doIfSuccess { responseFilesWithErrors.items.addAll(it) }
                doIfError { responseFilesWithErrors.errors.add(folderName) }
            }
        }

        return responseFilesWithErrors
    }

    suspend fun isFolderOnCamera(folderName: String): Boolean {
        var operationResult = false
        val foldersResponse = getResultWithAttempts(ATTEMPTS_RETRY_REQUEST, DELAY_ON_RETRY) {
            getFilesInFolder(CameraConstants.FILES_MAIN_PATH_FOLDER, folderName)
        }
        with(foldersResponse) {
            doIfSuccess { if (it.isNotEmpty()) operationResult = true }
        }
        return operationResult
    }

    private suspend fun getMediaInCamera(): Result<FileResponseWithErrors> =
        suspendCancellableCoroutine { coroutineTask ->
            CoroutineScope(Dispatchers.IO).launch {
                val foldersResponse = getFilesInFolder(CameraConstants.FILES_MAIN_PATH_FOLDER, "")
                with(foldersResponse) {
                    doIfSuccess { folderList ->
                        val folderNameList = folderList.map { it.name }
                        val cameraConnectFileResponseError = getFilesWithFolders(folderNameList)
                        coroutineTask.resume(Result.Success(cameraConnectFileResponseError))
                    }
                    doIfError { coroutineTask.resume(Result.Error(it)) }
                }
            }
        }

    private suspend fun getFilesInFolder(
        path: String,
        nameFolder: String
    ): Result<List<CameraFile>> {
        val command = XCameraCommand.Builder()
            .addMsgId(XCameraCommandCodes.COMMAND_LS_IN_FOLDER.commandValue)
            .addParam(path)
            .build()

        return when (
            val responseFilesOrFolders =
                getInformationByCommand<X1CameraResponse>(command)
        ) {
            is Result.Success -> {
                val itemsInFolder = responseFilesOrFolders.data.getItemsFile(path, nameFolder)
                Result.Success(itemsInFolder)
            }
            is Result.Error -> {
                responseFilesOrFolders
            }
        }
    }

    private fun reviewIfCommandIsSuccess(cameraConnectSessionX1Response: Result<X1CameraResponse>): Result<Unit> {
        return when (cameraConnectSessionX1Response) {
            is Result.Success -> {
                if (cameraConnectSessionX1Response.data.isCommandSuccess()) {
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception("Error retrieving data from camera"))
                }
            }
            is Result.Error -> cameraConnectSessionX1Response
        }
    }

    private suspend fun requestFilesFromCameraWithAttempts(
        type: FileListType,
        numberOfItems: Int
    ): Result<FileResponseWithErrors> {
        var cameraResponse = FileResponseWithErrors()
        var currentAttempt = 0
        var mediaResponse: Result<FileResponseWithErrors>
        var isResponseSuccess = false

        do {
            mediaResponse = getMediaInCamera()
            mediaResponse.doIfSuccess { files ->
                isResponseSuccess = true
                cameraResponse = updateMediaListByType(files, type)
            }

            currentAttempt++
        } while (isCurrentMediaOutdated(currentAttempt, numberOfItems, type))

        canReadNotification = true

        if (isResponseSuccess) return Result.Success(cameraResponse)
        return mediaResponse
    }

    private fun updateMediaListByType(
        fileResponse: FileResponseWithErrors,
        type: FileListType
    ): FileResponseWithErrors {
        val cameraResponse = FileResponseWithErrors()

        val snapshotItems = fileResponse.items.filter { cameraFile -> cameraFile.isSnapshot() }
        val videoItems = fileResponse.items.filter { cameraFile -> cameraFile.isVideoHighQuality() }
        val audioItems = fileResponse.items.filter { cameraFile -> cameraFile.isAudio() }

        cameraResponse.errors.addAll(fileResponse.errors)

        CameraServiceCache.snapshots = snapshotItems
        CameraServiceCache.videos = videoItems
        CameraServiceCache.audios = audioItems

        when (type) {
            FileListType.SNAPSHOT -> cameraResponse.items.addAll(snapshotItems)
            FileListType.VIDEO -> cameraResponse.items.addAll(videoItems)
            FileListType.AUDIO -> cameraResponse.items.addAll(audioItems)
        }

        return cameraResponse
    }

    private fun isCurrentMediaOutdated(
        currentAttempt: Int,
        numberOfItems: Int,
        type: FileListType
    ): Boolean {
        return currentAttempt < CameraConstants.ATTEMPTS_RETRY_REQUEST && when (type) {
            FileListType.SNAPSHOT -> CameraServiceCache.snapshots.size != numberOfItems
            FileListType.VIDEO -> CameraServiceCache.videos.size != numberOfItems
            FileListType.AUDIO -> false // pending to change when X2 firmware implements get number of audios
        }
    }

    private fun checkIfAllFilesWereRetrieved(type: FileListType, numberOfItems: Int): Boolean {
        return when (type) {
            FileListType.VIDEO -> numberOfItems == CameraServiceCache.videos.size
            FileListType.SNAPSHOT -> numberOfItems == CameraServiceCache.snapshots.size
            FileListType.AUDIO -> false // pending to change when X2 firmware implements get number of audios
        }
    }

    private fun getFilesOfType(type: FileListType): FileResponseWithErrors {
        val cameraResponse = FileResponseWithErrors()
        when (type) {
            FileListType.VIDEO -> cameraResponse.items.addAll(CameraServiceCache.videos)
            FileListType.SNAPSHOT -> cameraResponse.items.addAll(CameraServiceCache.snapshots)
            FileListType.AUDIO -> cameraResponse.items.addAll(CameraServiceCache.audios)
        }
        return cameraResponse
    }

    private suspend fun getFileCountOfType(type: FileListType) =
        getResultWithAttempts(CameraConstants.ATTEMPTS_RETRY_REQUEST) {
            when (type) {
                FileListType.VIDEO -> getNumberOfVideos()
                FileListType.SNAPSHOT -> getNumberOfSnapshots()
                FileListType.AUDIO -> getNumberOfAudios()
            }
        }

    private fun getResponseParamWithResponseInformation(responseInformation: Result<X1CameraResponse>): Result<String> {
        return when (responseInformation) {
            is Result.Success -> {
                responseInformation.data.param.let {
                    canReadNotification = true
                    return if (it.isNullOrEmpty()) Result.Error(Exception("null response parameter")) else Result.Success(
                        it
                    )
                }
            }
            is Result.Error -> responseInformation
        }
    }

    private suspend inline fun <reified T : Any> getInformationByCommand(command: XCameraCommand): Result<T> {
        val commandBytes = format.toJson(command).toByteArray()
        var response = commandSocketHelper.writeBytesToGetStringResponse(commandBytes)

        verifyIfTokenStillValid(response)

        response.convertToObject<T>().doIfSuccess { return Result.Success(it) }

        var attempts = 0
        do {
            delay(100)
            if (isInputStreamAvailable()) response += commandSocketHelper.readInputStream()
            attempts++
        } while (attempts < ATTEMPTS_RETRY_REQUEST && response.convertToObject<T>() is Result.Error)

        return response.convertToObject()
    }

    private fun verifyIfTokenStillValid(response: String) {
        response.convertToObject<X1CameraResponse>().doIfSuccess {
            isTokenInvalid = it.isTokenInvalid()
        }
    }

    suspend fun getNumberOfSnapshots(): Result<Int> {
        val command = XCameraCommand {
            addMsgId(XCameraCommandCodes.GET_NUM_FILES.commandValue)
            addType(XCameraCommandTypes.PHOTO_COUNT.value)
        }

        return when (val response = getResponseParam(command)) {
            is Result.Success -> {
                val snapshotCount = try {
                    response.data.toInt()
                } catch (e: Exception) {
                    return Result.Error(e)
                }
                Result.Success(snapshotCount)
            }
            is Result.Error -> response
        }
    }

    private suspend fun getNumberOfVideos(): Result<Int> {
        val command = XCameraCommand {
            addMsgId(XCameraCommandCodes.GET_NUM_FILES.commandValue)
            addType(XCameraCommandTypes.VIDEO_COUNT.value)
        }

        return when (val response = getResponseParam(command)) {
            is Result.Success -> {
                val videoCount = try {
                    response.data.toInt() / 2
                } catch (e: Exception) {
                    return Result.Error(e)
                }
                Result.Success(videoCount)
            }
            is Result.Error -> response
        }
    }

    // pending to change when X2 firmware implements get number of audios
    private suspend fun getNumberOfAudios(): Result<Int> {
        val command = XCameraCommand {
            addMsgId(XCameraCommandCodes.GET_NUM_FILES.commandValue)
            addType(XCameraCommandTypes.FILES_COUNT.value)
        }

        return when (val response = getResponseParam(command)) {
            is Result.Success -> {
                val audioCount = try {
                    response.data.toInt()
                } catch (e: Exception) {
                    return Result.Error(e)
                }
                Result.Success(audioCount)
            }
            is Result.Error -> response
        }
    }

    companion object {
        private const val CMD_PORT = 7878
        private const val ATTEMPTS_RETRY_REQUEST = 5
        private const val DELAY_ON_RETRY = 500L
        var isTokenInvalid = false
    }
}
