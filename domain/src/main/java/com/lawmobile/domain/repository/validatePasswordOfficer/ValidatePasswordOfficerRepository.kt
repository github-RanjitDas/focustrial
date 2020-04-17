package com.lawmobile.domain.repository.validatePasswordOfficer

import com.lawmobile.domain.entity.DomainUser
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.commons.helpers.Result

interface ValidatePasswordOfficerRepository: BaseRepository {
    suspend fun getUserInformation(): Result<DomainUser>
}