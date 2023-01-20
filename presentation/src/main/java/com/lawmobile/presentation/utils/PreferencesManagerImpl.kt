package com.lawmobile.presentation.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.utils.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesManagerImpl(private val dataStore: DataStore<Preferences>) : PreferencesManager {
    override suspend fun getSerialNumber(): String {
        return dataStore.data.map { preferences ->
            preferences[SERIAL_NUMBER] ?: CameraInfo.officerId
        }.first()
    }

    override suspend fun getTenantId(): String {
        return dataStore.data.map { preferences ->
            preferences[TENANT_ID] ?: CameraInfo.tenantId
        }.first()
    }

    override suspend fun getDiscoveryEndpointUrl(): String {
        return dataStore.data.map { preferences ->
            preferences[DISCOVERY_ENDPOINT] ?: CameraInfo.discoveryUrl
        }.first()
    }

    override suspend fun getToken(): String {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }.first()
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    override suspend fun saveSafeFleetIdConfigUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SAFE_FLEET_ID_ENDPOINT] = url
        }
    }

    override suspend fun saveUsersEndpointUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[USERS_ENDPOINT] = url
        }
    }

    override suspend fun saveAuthorizationEndpointUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[AUTHORIZATION_ENDPOINT] = url
        }
    }

    override suspend fun saveTokenEndpointUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_ENDPOINT] = url
        }
    }

    override suspend fun getSafeFleetIdConfigUrl(): String {
        return dataStore.data.map { preferences ->
            preferences[SAFE_FLEET_ID_ENDPOINT] ?: ""
        }.first()
    }

    override suspend fun getUsersEndpointUrl(): String {
        return dataStore.data.map { preferences ->
            preferences[USERS_ENDPOINT] ?: ""
        }.first()
    }

    override suspend fun getAuthorizationEndpointUrl(): String {
        return dataStore.data.map { preferences ->
            preferences[AUTHORIZATION_ENDPOINT] ?: ""
        }.first()
    }

    override suspend fun getTokenEndpointUrl(): String {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_ENDPOINT] ?: ""
        }.first()
    }

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val SAFE_FLEET_ID_ENDPOINT = stringPreferencesKey("safe_fleet_id_endpoint")
        private val USERS_ENDPOINT = stringPreferencesKey("users_endpoint")
        private val AUTHORIZATION_ENDPOINT = stringPreferencesKey("authorization_endpoint")
        private val TOKEN_ENDPOINT = stringPreferencesKey("token_endpoint")
        private val DISCOVERY_ENDPOINT = stringPreferencesKey("discovery_endpoint")
        private val TENANT_ID = stringPreferencesKey("tenant_id")
        private val SERIAL_NUMBER = stringPreferencesKey("serial_number")
        // TODO: Hard coded TENANT_ID_VALUE
        private const val TENANT_ID_VALUE = "staging01"
        // TODO: Hard coded SSID
        const val X2_SSID = "x2-22201760"
        // const val X2_SSID = "X22032578"
        // TODO: Hard coded DISCOVERY_URL
        private const val DISCOVERY_URL = "https://stg.safefleetcloud.us/tenant-settings/api/hardware/discovery"
    }
}
