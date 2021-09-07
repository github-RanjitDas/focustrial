package com.lawmobile.domain.usecase.validateOfficerId

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerIdUseCase : BaseUseCase {
    suspend fun validateOfficerId(officerId: String): Result<Boolean>
}
