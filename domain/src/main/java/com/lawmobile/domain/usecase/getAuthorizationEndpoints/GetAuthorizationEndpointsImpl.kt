package com.lawmobile.domain.usecase.getAuthorizationEndpoints

import com.lawmobile.domain.repository.authorization.AuthorizationRepository

class GetAuthorizationEndpointsImpl(
    private val authorizationRepository: AuthorizationRepository
) : GetAuthorizationEndpoints {
    override suspend fun invoke() =
        authorizationRepository.getAuthorizationEndpoints()
}
