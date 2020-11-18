package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class ValidatePasswordOfficerRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    ValidatePasswordOfficerRemoteDataSource {
    override suspend fun getUserInformation(): Result<CameraConnectUserResponse> {
        return try {
            cameraConnectService.getUserResponse()
        } catch (e: Exception) {
            Result.Error(Exception(ERROR_IN_INFORMATION_USER))
        }
    }

    companion object {
        const val ERROR_IN_INFORMATION_USER = "Error getting information of user"
    }
}
