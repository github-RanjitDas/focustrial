package com.lawmobile.data.datasource.remote.bodyCameraStatus

interface BodyCameraStatusDataSource {
    suspend fun isRecording(): Boolean
}
