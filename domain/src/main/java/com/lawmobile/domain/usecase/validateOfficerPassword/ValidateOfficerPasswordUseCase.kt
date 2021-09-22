package com.lawmobile.domain.usecase.validateOfficerPassword

import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerPasswordUseCase {
    suspend fun getUserInformation(): Result<DomainUser>
}
