package com.lawmobile.data.datasource.remote.validateOfficerId

import com.lawmobile.data.dto.api.validateOfficerId.ValidateOfficerIdApi

class ValidateOfficerIdRemoteDataSourceImpl(
    private val validateOfficerIdApi: ValidateOfficerIdApi
) : ValidateOfficerIdRemoteDataSource {
    override suspend fun validateOfficerId(officerId: String) =
        validateOfficerIdApi.validateOfficerId(officerId)
}
