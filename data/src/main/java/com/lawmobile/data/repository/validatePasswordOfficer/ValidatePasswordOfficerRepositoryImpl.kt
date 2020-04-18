package com.lawmobile.data.repository.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSource
import com.lawmobile.data.mappers.MapperCameraConnectUserDomainUse
import com.lawmobile.domain.entity.DomainUser
import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository
import com.safefleet.mobile.commons.helpers.Result

class ValidatePasswordOfficerRepositoryImpl(
    private val validatePasswordOfficerRemoteDataSource: ValidatePasswordOfficerRemoteDataSource
) : ValidatePasswordOfficerRepository {
    override suspend fun getUserInformation(): Result<DomainUser> {
        return when (val cameraUser =
            validatePasswordOfficerRemoteDataSource.getUserInformation()) {
            is Result.Success -> {
                val user = MapperCameraConnectUserDomainUse
                    .cameraConnectUserResponseToDomainUser(cameraUser.data)
                Result.Success(user)
            }
            is Result.Error -> cameraUser
        }
    }
}