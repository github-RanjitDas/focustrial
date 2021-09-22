package com.lawmobile.data.repository.validateOfficerPassword

import com.lawmobile.data.datasource.remote.validateOfficerPassword.ValidateOfficerPasswordRemoteDataSource
import com.lawmobile.data.mappers.impl.UserMapper.toDomain
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.repository.validateOfficerPassword.ValidateOfficerPasswordRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidateOfficerPasswordRepositoryImpl(
    private val validateOfficerPasswordRemoteDataSource: ValidateOfficerPasswordRemoteDataSource
) : ValidateOfficerPasswordRepository {
    override suspend fun getUserInformation(): Result<DomainUser> {
        return when (
            val cameraUser = validateOfficerPasswordRemoteDataSource.getUserInformation()
        ) {
            is Result.Success -> {
                val user = cameraUser.data.toDomain()
                Result.Success(user)
            }
            is Result.Error -> cameraUser
        }
    }
}
