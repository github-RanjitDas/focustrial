package com.lawmobile.data.mappers.impl

import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.mappers.impl.AuthorizationEndpointsMapper.toDomain
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class AuthorizationEndpointsMapperTest {

    @Test
    fun toDomain() {
        val authorizationEndpointsDto = AuthorizationEndpointsDto("", "")
        val authorizationEndpoints = authorizationEndpointsDto.toDomain()
        Assert.assertEquals(
            authorizationEndpointsDto.authorizationEndpoint,
            authorizationEndpoints.authorizationEndpoint
        )
        Assert.assertEquals(
            authorizationEndpointsDto.tokenEndpoint,
            authorizationEndpoints.tokenEndpoint
        )
    }
}
