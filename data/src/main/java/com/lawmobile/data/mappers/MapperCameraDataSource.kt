package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse

object MapperCameraDataSource {
    fun cameraConnectUserResponseToDomainUser(cameraConnectUserResponse: CameraConnectUserResponse): DomainUser =
        cameraConnectUserResponse.run {
            DomainUser(officerId, name, password)
        }
}