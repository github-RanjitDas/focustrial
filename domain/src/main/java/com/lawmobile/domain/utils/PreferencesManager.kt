package com.lawmobile.domain.utils

interface PreferencesManager {
    suspend fun getSerialNumber(): String
    suspend fun getTenantId(): String
    suspend fun getDiscoveryEndpointUrl(): String
    suspend fun getToken(): String
    suspend fun saveToken(token: String)
    suspend fun getSafeFleetIdConfigUrl(): String
    suspend fun saveSafeFleetIdConfigUrl(url: String)
    suspend fun getUsersEndpointUrl(): String
    suspend fun saveUsersEndpointUrl(url: String)
    suspend fun getAuthorizationEndpointUrl(): String
    suspend fun saveAuthorizationEndpointUrl(url: String)
    suspend fun getTokenEndpointUrl(): String
    suspend fun saveTokenEndpointUrl(url: String)
}
