package com.lawmobile.domain.usecase.validateOfficerId

import com.lawmobile.domain.repository.validateOfficerId.ValidateOfficerIdRepository

class ValidateOfficerIdUseCaseImpl(
    private val validateOfficerIdRepository: ValidateOfficerIdRepository
) : ValidateOfficerIdUseCase {
    override suspend fun validateOfficerId(officerId: String) =
        validateOfficerIdRepository.validateOfficerId(officerId)
}
