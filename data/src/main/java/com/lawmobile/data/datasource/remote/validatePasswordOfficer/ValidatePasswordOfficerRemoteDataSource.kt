package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ValidatePasswordOfficerRemoteDataSource {
    suspend fun getUserInformation(): Result<CameraUser>
}