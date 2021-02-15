package com.lawmobile.domain.usecase.validatePasswordOfficer

import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository

class ValidatePasswordOfficerUseCaseImpl(private val validatePasswordOfficerRepository: ValidatePasswordOfficerRepository) :
    ValidatePasswordOfficerUseCase {
    override suspend fun getUserInformation() =
        validatePasswordOfficerRepository.getUserInformation()
}
