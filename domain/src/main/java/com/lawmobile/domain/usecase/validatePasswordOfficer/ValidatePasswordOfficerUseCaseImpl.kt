package com.lawmobile.domain.usecase.validatePasswordOfficer

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository
import com.safefleet.mobile.commons.helpers.Result

class ValidatePasswordOfficerUseCaseImpl(private val validatePasswordOfficerRepository: ValidatePasswordOfficerRepository) :
    ValidatePasswordOfficerUseCase {
    override suspend fun getUserInformation(): Result<DomainUser> =
        validatePasswordOfficerRepository.getUserInformation()
}