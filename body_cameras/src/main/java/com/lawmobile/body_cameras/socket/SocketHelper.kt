package com.lawmobile.body_cameras.socket

import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.ceil

class SocketHelper(private var socket: Socket) {

    var isSocketAvailable = true

    suspend fun connectSocket(port: Int, hostname: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            waitWhileSocketAvailable()
            socket = recreateSocket(socket)
            try {
                socket.connect(InetSocketAddress(hostname, port), 3000)
                socket.soTimeout = 3000
                isSocketAvailable = true
                Result.Success(Unit)
            } catch (e: Exception) {
                println("Unable to connect with Camera Socket:" + e.message)
                isSocketAvailable = true
                Result.Error(e)
            }
        }

    suspend fun writeBytesToGetStringResponse(bytes: ByteArray) =
        withContext(Dispatchers.IO) {
            try {
                if (!socket.isConnected || socket.isClosed) return@withContext ""
                waitWhileSocketAvailable()
                cleanSocket()
                socket.getOutputStream().write(bytes)
                socket.getOutputStream().flush()
                val response = readInputStream()
                isSocketAvailable = true
                return@withContext response
            } catch (e: Exception) {
                isSocketAvailable = true
                return@withContext ""
            }
        }

    private suspend fun cleanSocket() {
        if (isInputStreamAvailable()) {
            readInputStream()
        }
    }

    suspend fun getBytesWithSize(size: Long) = withContext(Dispatchers.IO) {
        waitWhileSocketAvailable()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var totalBytesWroteInFile = 0
        var isTotalFileWrote = size <= totalBytesWroteInFile
        try {
            while (!isTotalFileWrote) {
                val buffer = ByteArray(getOptimumBufferSize())
                val bytesWroteInBuffer = socket.getInputStream().read(buffer, 0, buffer.size)
                totalBytesWroteInFile += bytesWroteInBuffer
                byteArrayOutputStream.write(buffer, 0, bytesWroteInBuffer)
                isTotalFileWrote = size <= totalBytesWroteInFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isSocketAvailable = true
            return@withContext Result.Error(e)
        }
        isSocketAvailable = true
        return@withContext Result.Success(byteArrayOutputStream.toByteArray())
    }

    private fun getOptimumBufferSize(): Int {
        return if (socket.getInputStream().available() != 0) {
            socket.getInputStream().available()
        } else BUFFER_SIZE
    }

    suspend fun writeInOutputStream(bytes: ByteArray) = withContext(Dispatchers.IO) {
        waitWhileSocketAvailable()
        val bufferedOutputStream = BufferedOutputStream(socket.getOutputStream())
        val fileInputStream = ByteArrayInputStream(bytes)
        val totalChunks = ceil(bytes.size.toDouble() / BUFFER_SIZE).toInt()

        val bytesBuffer = ByteArray(BUFFER_SIZE)
        for (i in 0 until totalChunks) {
            val startIndex: Int = i * BUFFER_SIZE
            val endIndex: Int =
                if (startIndex + BUFFER_SIZE > bytes.size) bytes.size else startIndex + BUFFER_SIZE
            val length = endIndex - startIndex
            fileInputStream.read(bytesBuffer, 0, length)
            try {
                bufferedOutputStream.write(bytesBuffer, 0, length)
                bufferedOutputStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fileInputStream.close()
        bufferedOutputStream.close()
        isSocketAvailable = true
    }

    suspend fun readInputStream() = withContext(Dispatchers.IO) {
        return@withContext readInformationInputStream()
    }

    fun readInformationInputStream(): String {
        var response = ""
        try {
            do {
                val buffer = ByteArray(getOptimumBufferSize())
                val length = socket.getInputStream().read(buffer, 0, buffer.size)
                response += String(buffer, 0, length)
            } while (isInputStreamAvailable())
        } catch (e: Exception) {
            response = ""
        }
        return response
    }

    fun isInputStreamAvailable(): Boolean {
        return try {
            return socket.isConnected && !socket.isClosed && socket.getInputStream()
                .available() != 0
        } catch (e: Exception) {
            false
        }
    }

    fun isConnected(): Boolean = socket.isConnected

    private suspend fun waitWhileSocketAvailable() {
        var timeOutInMillis = 0
        while (!isSocketAvailable) {
            delay(200)
            timeOutInMillis += 200
            if (timeOutInMillis > TIMEOUT_LIMIT_IN_MILLISECONDS) {
                cleanSocket()
                isSocketAvailable = true
                break
            }
        }
        isSocketAvailable = false
    }

    private fun recreateSocket(socket: Socket): Socket {
        socket.close()
        return Socket()
    }

    companion object {
        const val BUFFER_SIZE = 1024 * 8
        const val TIMEOUT_LIMIT_IN_MILLISECONDS = 5000
    }
}
