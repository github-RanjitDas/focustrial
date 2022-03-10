package com.lawmobile.data.datasource.remote.typeOfCamera

import com.lawmobile.body_cameras.enums.CameraType
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class TypeOfCameraRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    TypeOfCameraRemoteDataSource {
    private var cameraService = cameraServiceFactory.create()
    override suspend fun getTypeOfCamera(): Result<CameraType> = cameraService.getCameraType()
}
