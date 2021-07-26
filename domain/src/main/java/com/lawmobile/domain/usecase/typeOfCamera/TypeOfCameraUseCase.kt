package com.lawmobile.domain.usecase.typeOfCamera

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface TypeOfCameraUseCase : BaseUseCase {
    suspend fun getTypeOfCamera(): Result<CameraType>
}
