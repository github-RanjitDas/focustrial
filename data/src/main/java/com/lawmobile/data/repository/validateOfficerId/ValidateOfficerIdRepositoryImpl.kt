package com.lawmobile.data.repository.validateOfficerId

import com.lawmobile.data.datasource.remote.validateOfficerId.ValidateOfficerIdRemoteDataSource
import com.lawmobile.domain.repository.validateOfficerId.ValidateOfficerIdRepository

class ValidateOfficerIdRepositoryImpl(
    private val validateOfficerIdRemoteDataSource: ValidateOfficerIdRemoteDataSource
) : ValidateOfficerIdRepository {
    override suspend fun validateOfficerId(officerId: String) =
        validateOfficerIdRemoteDataSource.validateOfficerId(officerId)
}
