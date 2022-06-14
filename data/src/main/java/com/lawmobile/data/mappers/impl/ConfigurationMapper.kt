package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.Config
import com.lawmobile.data.mappers.DomainMapper

object ConfigurationMapper : DomainMapper<Config, com.lawmobile.domain.entities.Config> {
    override fun Config.toDomain(): com.lawmobile.domain.entities.Config = run {
        com.lawmobile.domain.entities.Config(
            encryption
        )
    }
}
