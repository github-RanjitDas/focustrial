package com.lawmobile.data.datasource.remote.bodyCameraStatus

import com.lawmobile.data.utils.CameraServiceFactory

class BodyCameraStatusDataSourceImpl(
    private val bodyCameraFactory: CameraServiceFactory
) : BodyCameraStatusDataSource {
    private val bodyCamera by lazy { bodyCameraFactory.create() }
    override suspend fun isRecording(): Boolean = bodyCamera.isRecording()
}
