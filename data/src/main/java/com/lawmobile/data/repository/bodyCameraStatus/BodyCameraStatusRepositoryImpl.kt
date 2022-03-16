package com.lawmobile.data.repository.bodyCameraStatus

import com.lawmobile.data.datasource.remote.bodyCameraStatus.BodyCameraStatusDataSource
import com.lawmobile.domain.repository.bodyCameraStatus.BodyCameraStatusRepository

class BodyCameraStatusRepositoryImpl(
    private val bodyCameraStatusDataSource: BodyCameraStatusDataSource
) : BodyCameraStatusRepository {
    override suspend fun isRecording(): Boolean = bodyCameraStatusDataSource.isRecording()
}
