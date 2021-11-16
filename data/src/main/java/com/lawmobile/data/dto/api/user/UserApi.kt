package com.lawmobile.data.dto.api.user

import com.lawmobile.data.dto.entities.UserDto

interface UserApi {
    suspend fun getUser(uuid: String): UserDto
}
