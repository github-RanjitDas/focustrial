package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser

object UserMapper {
    fun cameraToDomain(cameraUser: CameraUser): DomainUser =
        cameraUser.run {
            DomainUser(officerId, name, password)
        }
}