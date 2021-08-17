package com.lawmobile.data.repository.typeOfCamera

import com.lawmobile.data.datasource.remote.typeOfCamera.TypeOfCameraRemoteDataSource
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.repository.typeOfCamera.TypeOfCameraRepository
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.enums.CameraType.X2 as X2

class TypeOfCameraRepositoryImpl(
    private val typeOfCameraRemoteDataSource: TypeOfCameraRemoteDataSource
) : TypeOfCameraRepository {
    override suspend fun getTypeOfCamera(): Result<CameraType> {
        val response = typeOfCameraRemoteDataSource.getTypeOfCamera()
        response.doIfSuccess { cameraType ->
            return if (cameraType == X2) Result.Success(CameraType.X2)
            else Result.Success(CameraType.X1)
        }
        return Result.Error(Exception("Exception to get type"))
    }
}
