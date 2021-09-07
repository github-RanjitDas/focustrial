package com.lawmobile.data.datasource.remote.validateOfficerId

import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerIdRemoteDataSource {
    suspend fun validateOfficerId(officerId: String): Result<Boolean>
}
