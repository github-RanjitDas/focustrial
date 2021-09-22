package com.lawmobile.domain.usecase.validateOfficerPassword

import com.lawmobile.domain.repository.validateOfficerPassword.ValidateOfficerPasswordRepository

class ValidateOfficerPasswordUseCaseImpl(private val validateOfficerPasswordRepository: ValidateOfficerPasswordRepository) :
    ValidateOfficerPasswordUseCase {
    override suspend fun getUserInformation() =
        validateOfficerPasswordRepository.getUserInformation()
}
