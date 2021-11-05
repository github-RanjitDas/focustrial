package com.lawmobile.domain.usecase.authorization

import com.lawmobile.domain.repository.authorization.AuthorizationRepository

class AuthorizationUseCaseImpl(private val authorizationRepository: AuthorizationRepository) :
    AuthorizationUseCase {
    override suspend fun getAuthorizationEndpoints(tenantID: String) = authorizationRepository.getAuthorizationEndpoints(tenantID)
}
