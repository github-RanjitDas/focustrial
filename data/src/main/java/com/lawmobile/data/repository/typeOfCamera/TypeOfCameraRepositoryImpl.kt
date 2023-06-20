package com.lawmobile.data.repository.typeOfCamera

import com.lawmobile.body_cameras.enums.CameraType.X2
import com.lawmobile.data.datasource.remote.typeOfCamera.TypeOfCameraRemoteDataSource
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.repository.typeOfCamera.TypeOfCameraRepository
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class TypeOfCameraRepositoryImpl(
    private val typeOfCameraRemoteDataSource: TypeOfCameraRemoteDataSource
) : TypeOfCameraRepository {
    override suspend fun getTypeOfCamera(): Result<CameraType> {
        val response = typeOfCameraRemoteDataSource.getTypeOfCamera()
        response.doIfSuccess { cameraType ->
            return Result.Success(CameraType.X2)

        }
        return Result.Error(Exception("Exception to get type"))
    }
}
