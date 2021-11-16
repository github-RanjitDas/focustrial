package com.lawmobile.data.dto.api.user

import com.lawmobile.data.dto.entities.UserDto
import com.lawmobile.domain.utils.PreferencesManager
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class UserApiImpl(
    private val httpClient: HttpClient,
    private val preferencesManager: PreferencesManager
) : UserApi {
    override suspend fun getUser(uuid: String): UserDto {
        val userUrl = preferencesManager.getUsersEndpointUrl()
        val userList = httpClient.get<List<UserDto>>(userUrl)
        return userList.find { it.email == uuid } ?: throw Exception("User not found")
    }
}
