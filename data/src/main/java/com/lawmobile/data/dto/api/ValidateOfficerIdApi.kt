package com.lawmobile.data.dto.api

import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerIdApi {
    suspend fun validateOfficerId(officerId: String): Result<Boolean>
}
