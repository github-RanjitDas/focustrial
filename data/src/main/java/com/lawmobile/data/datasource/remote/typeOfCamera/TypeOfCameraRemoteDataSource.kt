package com.lawmobile.data.datasource.remote.typeOfCamera

import com.lawmobile.body_cameras.enums.CameraType
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface TypeOfCameraRemoteDataSource {
    suspend fun getTypeOfCamera(): Result<CameraType>
}
