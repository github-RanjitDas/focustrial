package com.lawmobile.data.mappers.impl

import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.AuthorizationEndpoints

object AuthorizationEndpointsMapper :
    DomainMapper<AuthorizationEndpointsDto, AuthorizationEndpoints> {
    override fun AuthorizationEndpointsDto.toDomain(): AuthorizationEndpoints {
        return AuthorizationEndpoints(authorizationEndpoint, tokenEndpoint)
    }
}
