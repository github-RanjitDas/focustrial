package com.lawmobile.data.mappers.impl

import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.AuthorizationEndpoints

object AuthorizationEndpointsMapper :
    DomainMapper<AuthorizationEndpointsResponseDto, AuthorizationEndpoints> {
    override fun AuthorizationEndpointsResponseDto.toDomain(): AuthorizationEndpoints {
        return AuthorizationEndpoints(authorizationEndpoint, tokenEndpoint)
    }
}
