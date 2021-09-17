package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser

object UserMapper : DomainMapper<CameraUser, DomainUser> {
    override fun CameraUser.toDomain(): DomainUser = DomainUser(officerId, name, password)
}
