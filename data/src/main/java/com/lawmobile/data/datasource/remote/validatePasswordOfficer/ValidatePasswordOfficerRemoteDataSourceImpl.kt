package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.safefleet.mobile.avml.cameras.external.CameraDataSource
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import com.safefleet.mobile.commons.helpers.Result

class ValidatePasswordOfficerRemoteDataSourceImpl(private val cameraDataSource: CameraDataSource) :
    ValidatePasswordOfficerRemoteDataSource {
    override suspend fun getUserInformation(): Result<CameraConnectUserResponse> {
        return try {
            cameraDataSource.getUserInformation()
        } catch (e: Exception) {
            Result.Error(Exception(ERROR_IN_INFORMATION_USER))
        }
    }

    companion object {
        const val ERROR_IN_INFORMATION_USER = "Error getting information of user"
    }
}
