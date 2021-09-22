package com.lawmobile.domain.repository.validateOfficerPassword

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerPasswordRepository : BaseRepository {
    suspend fun getUserInformation(): Result<DomainUser>
}
