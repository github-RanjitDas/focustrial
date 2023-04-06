package com.lawmobile.presentation.utils

import android.util.Log
import com.lawmobile.presentation.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter

object SFConsoleLogs : SFLogs {

    // enable and disable logs
    private var isLogsShown = true

    enum class Tags(val tag: String) {
        TAG_CAMERA_ERRORS("CAMERA_ERRORS"),
        TAG_END_POINTS_ERRORS("END_POINTS_ERRORS"),
        TAG_INTERNET_CONNECTION_ERRORS("INTERNET_CONNECTION_ERRORS"),
        TAG_BLUETOOTH_CONNECTION_ERRORS("BLUETOOTH_CONNECTION_ERRORS"),
        TAG_SOCKET_CONNECTION_ERRORS("SOCKET_CONNECTION_ERRORS"),
        TAG_HOTSPOT_CONNECTION_ERRORS("HOTSPOT_CONNECTION_ERRORS"),
        TAG_COMMON_ERRORS("COMMON_ERRORS")
    }

    enum class Level(val priority: Int) {
        VERBOSE(2),
        DEBUG(3),
        INFO(4),
        WARN(5),
        ERROR(6),
        ASSERT(7);
    }

    override fun log(logLevel: Level, errorTag: Tags, exception: Throwable?, message: String?) {
        if (!isLogsShown) return

        val result = StringBuilder()
        if (message != null) {
            result.append(message)
        }
        addExceptionIfNotNull(exception, result)

        if (BuildConfig.DEBUG) {
            // handle debug variant logs
            Log.println(logLevel.priority, errorTag.tag, result.toString())
        } else {
            // handle logs for release variant.
        }
    }

    private fun addExceptionIfNotNull(t: Throwable?, result: StringBuilder) {
        if (t != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.flush()
            result.append("\n Throwable: ")
            result.append(sw.toString())
        }
    }
}
