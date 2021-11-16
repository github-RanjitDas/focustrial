package com.lawmobile.presentation.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class PreferencesManagerImplTest {

    private val preference = "123"
    private val preferences: Preferences = mockk {
        every { get(any<Preferences.Key<String>>()) } returns preference
        every { toMutablePreferences()[any<Preferences.Key<String>>()] = any() } returns Unit
    }

    private val dataStore: DataStore<Preferences> = mockk(relaxed = true) {
        val transform: suspend (MutablePreferences) -> Unit = { }
        coEvery { data } returns flowOf(preferences)
        coEvery { edit(transform) } answers { preferences }
    }

    private val preferencesManager = PreferencesManagerImpl(dataStore)

    private fun generalGetAssert(result: String) {
        Assert.assertEquals(preference, result)
        coVerify { preferences[any<Preferences.Key<String>>()] }
    }

    private fun generalSetVerification() {
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun getSerialNumber() = runBlocking {
        val result = preferencesManager.getSerialNumber()
        generalGetAssert(result)
    }

    @Test
    fun getTenantId() = runBlocking {
        val result = preferencesManager.getTenantId()
        generalGetAssert(result)
    }

    @Test
    fun getDiscoveryEndpointUrl() = runBlocking {
        val result = preferencesManager.getDiscoveryEndpointUrl()
        generalGetAssert(result)
    }

    @Test
    fun getToken() = runBlocking {
        val result = preferencesManager.getToken()
        generalGetAssert(result)
    }

    @Test
    fun saveToken() = runBlocking {
        preferencesManager.saveToken("")
        generalSetVerification()
    }

    @Test
    fun saveSafeFleetIdConfigUrl() = runBlocking {
        preferencesManager.saveSafeFleetIdConfigUrl("")
        generalSetVerification()
    }

    @Test
    fun saveUsersEndpointUrl() = runBlocking {
        preferencesManager.saveUsersEndpointUrl("")
        generalSetVerification()
    }

    @Test
    fun saveAuthorizationEndpointUrl() = runBlocking {
        preferencesManager.saveAuthorizationEndpointUrl("")
        generalSetVerification()
    }

    @Test
    fun saveTokenEndpointUrl() = runBlocking {
        preferencesManager.saveTokenEndpointUrl("")
        generalSetVerification()
    }

    @Test
    fun getSafeFleetIdConfigUrl() = runBlocking {
        val result = preferencesManager.getSafeFleetIdConfigUrl()
        generalGetAssert(result)
    }

    @Test
    fun getUsersEndpointUrl() = runBlocking {
        val result = preferencesManager.getUsersEndpointUrl()
        generalGetAssert(result)
    }

    @Test
    fun getAuthorizationEndpointUrl() = runBlocking {
        val result = preferencesManager.getAuthorizationEndpointUrl()
        generalGetAssert(result)
    }

    @Test
    fun getTokenEndpointUrl() = runBlocking {
        val result = preferencesManager.getTokenEndpointUrl()
        generalGetAssert(result)
    }
}
