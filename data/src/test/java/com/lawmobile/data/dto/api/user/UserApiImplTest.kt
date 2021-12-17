package com.lawmobile.data.dto.api.user

import com.lawmobile.data.dto.entities.UserDto
import com.lawmobile.data.dto.interceptors.FakeHttpClient
import com.lawmobile.data.dto.interceptors.FakeHttpClient.USERS_URL
import com.lawmobile.domain.utils.PreferencesManager
import io.ktor.client.request.get
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class UserApiImplTest {

    private val httpClient = FakeHttpClient.create()
    private val preferencesManager: PreferencesManager = mockk()
    private val userApiImpl = UserApiImpl(httpClient, preferencesManager)

    @Test
    fun getUserSuccess() = runBlocking {
        val uuid = "kevin.menesesp@perficient.com"
        coEvery { preferencesManager.getUsersEndpointUrl() } returns USERS_URL
        val response = httpClient.get<List<UserDto>>(USERS_URL).find { it.email == uuid }
        val result = userApiImpl.getUser(uuid)
        Assert.assertEquals(response, result)
        coVerify { preferencesManager.getUsersEndpointUrl() }
    }

    @Test
    fun getUserFail() = runBlocking {
        val uuid = "kevin.menesesp@perficient.com"
        coEvery { preferencesManager.getUsersEndpointUrl() } returns USERS_URL
        val response = httpClient.get<List<UserDto>>(USERS_URL).find { it.email == uuid }
        val result = try {
            userApiImpl.getUser("")
        } catch (e: Exception) {
            e
        }
        Assert.assertNotEquals(response, result)
        coVerify { preferencesManager.getUsersEndpointUrl() }
    }
}
