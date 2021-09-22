package com.lawmobile.data.datasource.remote.validateOfficerPassword

import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidateOfficerPasswordRemoteDataSource {
    suspend fun getUserInformation(): Result<CameraUser>
}
