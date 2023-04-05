package com.lawmobile.presentation.utils

interface SFLogs {

    fun log(
        logLevel: SFConsoleLogs.Level = SFConsoleLogs.Level.DEBUG,
        errorTag: SFConsoleLogs.Tags = SFConsoleLogs.Tags.TAG_COMMON_ERRORS,
        exception: Throwable? = null,
        message: String?
    )
}
