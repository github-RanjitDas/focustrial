package com.lawmobile.data.datasource.remote.validateOfficerPassword

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidateOfficerPasswordRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    ValidateOfficerPasswordRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getUserInformation(): Result<CameraUser> {
        return try {
            cameraService.getUserResponse()
        } catch (e: Exception) {
            Result.Error(Exception(USER_INFORMATION_ERROR))
        }
    }

    companion object {
        const val USER_INFORMATION_ERROR = "Error getting user information"
    }
}
