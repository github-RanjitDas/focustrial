package com.lawmobile.data.datasource.remote.user

import com.lawmobile.body_cameras.entities.CameraUser
import com.lawmobile.data.dto.entities.UserDto

interface UserRemoteDataSource {
    suspend fun getUserFromNetwork(uuid: String): UserDto
    suspend fun getUserFromCamera(): CameraUser
}
