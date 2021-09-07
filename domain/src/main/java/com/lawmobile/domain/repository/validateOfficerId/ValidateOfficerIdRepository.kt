package com.lawmobile.domain.repository.validateOfficerId

import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerIdRepository : BaseRepository {
    suspend fun validateOfficerId(officerId: String): Result<Boolean>
}
