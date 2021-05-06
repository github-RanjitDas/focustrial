package com.lawmobile.domain.repository.validatePasswordOfficer

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidatePasswordOfficerRepository : BaseRepository {
    suspend fun getUserInformation(): Result<DomainUser>
}
