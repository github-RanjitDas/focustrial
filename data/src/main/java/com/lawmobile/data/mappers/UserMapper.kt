package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse

object UserMapper {
    fun cameraToDomain(cameraConnectUserResponse: CameraConnectUserResponse): DomainUser =
        cameraConnectUserResponse.run {
            DomainUser(officerId, name, password)
        }
}