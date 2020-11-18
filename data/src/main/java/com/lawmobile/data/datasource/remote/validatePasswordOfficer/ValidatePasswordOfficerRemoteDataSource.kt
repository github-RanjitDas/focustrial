package com.lawmobile.data.datasource.remote.validatePasswordOfficer
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import com.safefleet.mobile.commons.helpers.Result

interface ValidatePasswordOfficerRemoteDataSource {
    suspend fun getUserInformation(): Result<CameraConnectUserResponse>
}