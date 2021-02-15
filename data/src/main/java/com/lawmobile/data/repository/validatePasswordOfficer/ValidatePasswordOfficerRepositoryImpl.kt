package com.lawmobile.data.repository.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSource
import com.lawmobile.data.mappers.UserMapper
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidatePasswordOfficerRepositoryImpl(
    private val validatePasswordOfficerRemoteDataSource: ValidatePasswordOfficerRemoteDataSource
) : ValidatePasswordOfficerRepository {
    override suspend fun getUserInformation(): Result<DomainUser> {
        return when (
            val cameraUser = validatePasswordOfficerRemoteDataSource.getUserInformation()
        ) {
            is Result.Success -> {
                val user = UserMapper.cameraToDomain(cameraUser.data)
                Result.Success(user)
            }
            is Result.Error -> cameraUser
        }
    }
}
