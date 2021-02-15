package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidatePasswordOfficerRemoteDataSourceImpl(private val cameraService: CameraService) :
    ValidatePasswordOfficerRemoteDataSource {
    override suspend fun getUserInformation(): Result<CameraUser> {
        return try {
            cameraService.getUserResponse()
        } catch (e: Exception) {
            Result.Error(Exception(ERROR_IN_INFORMATION_USER))
        }
    }

    companion object {
        const val ERROR_IN_INFORMATION_USER = "Error getting information of user"
    }
}
