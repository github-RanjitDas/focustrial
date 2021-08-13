package com.lawmobile.domain.usecase.typeOfCamera

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.repository.typeOfCamera.TypeOfCameraRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class TypeOfCameraUseCaseImpl(private val typeOfCameraRepository: TypeOfCameraRepository) :
    TypeOfCameraUseCase {
    override suspend fun getTypeOfCamera(): Result<CameraType> {
        return typeOfCameraRepository.getTypeOfCamera()
    }
}
