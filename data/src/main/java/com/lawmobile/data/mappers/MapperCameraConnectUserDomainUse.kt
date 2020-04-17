package com.lawmobile.data.mappers

import com.lawmobile.domain.entity.DomainUser
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse

object MapperCameraConnectUserDomainUse {
    fun cameraConnectUserResponseToDomainUser(cameraConnectUserResponse: CameraConnectUserResponse): DomainUser =
        DomainUser(
            cameraConnectUserResponse.officerId,
            cameraConnectUserResponse.name,
            cameraConnectUserResponse.password
        )

}