package com.lawmobile.body_cameras.utils

import com.google.gson.Gson
import com.lawmobile.body_cameras.constants.CameraConstants
import com.lawmobile.body_cameras.entities.BWCConnectionParams
import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.SetupConfiguration
import com.lawmobile.body_cameras.socket.SocketHelper
import com.lawmobile.body_cameras.x1.entities.X1FileResponse
import com.safefleet.mobile.kotlin_commons.extensions.convertToObject
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class FileInformationHelper(
    private val format: Gson,
    private var commandHelper: CommandHelper,
    private var dataSocketHelper: SocketHelper
) {

    suspend fun connectDataSocket(): Result<Unit> {
        return dataSocketHelper.connectSocket(DATA_PORT, BWCConnectionParams.hostnameToConnect)
    }

    suspend fun getFileInformation(pathCamera: String): Result<ByteArray> =
        suspendCancellableCoroutine { coroutineTask ->
            CoroutineScope(Dispatchers.IO).launch {
                with(connectDataSocket()) {
                    doIfSuccess {
                        with(commandHelper.requestCommandDownloadFile(pathCamera)) {
                            doIfSuccess { responseDownloadFile ->
                                reviewResponseGetFileCommand(responseDownloadFile, coroutineTask)
                            }
                            doIfError {
                                coroutineTask.resume(Result.Error(it))
                            }
                        }
                    }
                    doIfError { coroutineTask.resume(Result.Error(it)) }
                }
            }
        }

    private suspend fun reviewResponseGetFileCommand(
        responseDownloadFile: X1FileResponse,
        coroutineTask: CancellableContinuation<Result<ByteArray>>
    ) {
        if (responseDownloadFile.isCommandSuccess()) {
            val bytesSendForCamera = responseDownloadFile.getBytesSent()
            if (isEmptyBytes(bytesSendForCamera)) {
                coroutineTask.resume(Result.Success(ByteArray(0)))
                return
            }

            delay(150)
            resumeBytesIfCameraSendCompleteInformation(bytesSendForCamera, coroutineTask)
            return
        }

        if (responseDownloadFile.isInvalidParam() || responseDownloadFile.isInvalidPath()) {
            coroutineTask.resume(Result.Success(ByteArray(0)))
            return
        }

        resumeErrorInGetFileInformation(coroutineTask)
    }

    suspend fun getWarningsLog(): Result<List<LogEvent>> {
        val nameLogJson = CameraConstants.FILES_MAIN_PATH + CameraConstants.NOTIFICATION_FILE_NAME
        return when (val response = getFileInformation(nameLogJson)) {
            is Result.Success -> {
                return try {
                    val logEventList = String(response.data)
                        .replace(" ", "")
                        .split("\n")
                        .filter { it.isNotEmpty() && it.isNotBlank() }
                        .map { LogEvent.fromCSV(it) }
                    Result.Success(logEventList)
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
            is Result.Error -> response
        }
    }

    suspend fun getBatteryLevelFromLog(): Result<Int> {
        when (val resultFile = getFileInformation(getNameOfLogToday())) {
            is Result.Success -> {
                return try {
                    val dateInLog = dateToString(Date(), "MM/dd/yyyy")
                    val linesInLog = String(resultFile.data).lines()
                    val linesReversedFilterWithDate =
                        linesInLog.filter { it.contains(dateInLog) }.asReversed()
                    val firstLineWithRightSize =
                        linesReversedFilterWithDate.map { it.split(",") }.first { it.size == 8 }
                    val percent =
                        firstLineWithRightSize[POSITION_IN_LOG_PERCENT].replace("%", "").toInt()
                    Result.Success(percent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(e)
                }
            }

            is Result.Error -> return resultFile
        }
    }

    suspend fun getInformationSetupConfiguration(): Result<SetupConfiguration> {
        val informationBytesResponse =
            getFileInformation(CameraConstants.FILES_MAIN_PATH + CameraConstants.NAME_SETUP_CONFIGURATION)
        informationBytesResponse.doIfSuccess { bytes ->
            return try {
                val configuration = format.fromJson(String(bytes), SetupConfiguration::class.java)
                Result.Success(configuration)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

        return Result.Error(Exception("Error in get File Information"))
    }

    suspend fun writeInOutputStream(bytes: ByteArray) = dataSocketHelper.writeInOutputStream(bytes)

    private fun getNameOfLogToday() =
        CameraConstants.FILES_MAIN_PATH + dateToString(Date(), "yyyyMMdd") + ".log"

    private fun isEmptyBytes(bytesSendForCamera: Long) = bytesSendForCamera == 0L

    private suspend fun resumeBytesIfCameraSendCompleteInformation(
        bytesSendForCamera: Long,
        coroutineTask: CancellableContinuation<Result<ByteArray>>
    ) {
        val resultDataFromDataSocket = dataSocketHelper.getBytesWithSize(bytesSendForCamera)
        var isCameraSendTheNotificationWithCompleteInformation: Boolean

        resultDataFromDataSocket.doIfSuccess { informationFromDataSocket ->
            var attemptsForNotification = 1
            do {
                isCameraSendTheNotificationWithCompleteInformation =
                    commandHelper.isInputStreamAvailable() && isCameraSendTheCompleteBytes(informationFromDataSocket)
                attemptsForNotification++
                delay(100)
            } while (attemptsForNotification < ATTEMPTS_RETRY_REQUEST && !isCameraSendTheNotificationWithCompleteInformation)

            if (isCameraSendTheNotificationWithCompleteInformation && coroutineTask.isActive) {
                coroutineTask.resume(resultDataFromDataSocket)
                return
            }
        }

        delay(250)
        resumeErrorInGetFileInformation(coroutineTask)
    }

    private suspend fun isCameraSendTheCompleteBytes(informationFromDataSocket: ByteArray): Boolean {
        val responseNotification = commandHelper.readInputStream().convertToObject<X1FileResponse>()
        return responseNotification is Result.Success && responseNotification.data.getBytesSent() == informationFromDataSocket.size.toLong()
    }

    private fun resumeErrorInGetFileInformation(coroutineTask: CancellableContinuation<Result<ByteArray>>) {
        if (coroutineTask.isActive) {
            coroutineTask.resume(Result.Error(Exception("Get file failed")))
        }
    }

    companion object {

        private const val DATA_PORT = 8787
        private const val POSITION_IN_LOG_PERCENT = 4
        private const val ATTEMPTS_RETRY_REQUEST = 5

        fun dateToString(
            date: Date,
            pattern: String = "dd/MM/yyyy",
            locale: Locale = Locale.getDefault()
        ): String {
            val format = SimpleDateFormat(pattern, locale)
            return format.format(date)
        }
    }
}
