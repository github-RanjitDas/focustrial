package com.lawmobile.data.datasource.remote.typeOfCamera

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.enums.CameraType
import com.safefleet.mobile.kotlin_commons.helpers.Result

class TypeOfCameraRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    TypeOfCameraRemoteDataSource {
    private var cameraService = cameraServiceFactory.create()

    override suspend fun getTypeOfCamera(): Result<CameraType> {
        return cameraService.getCameraType()
    }
}
