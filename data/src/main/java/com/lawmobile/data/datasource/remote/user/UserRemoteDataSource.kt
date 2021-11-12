package com.lawmobile.data.datasource.remote.user

import com.lawmobile.data.dto.entities.UserDto
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser

interface UserRemoteDataSource {
    suspend fun getUserFromNetwork(uuid: String): UserDto
    suspend fun getUserFromCamera(): CameraUser
}
