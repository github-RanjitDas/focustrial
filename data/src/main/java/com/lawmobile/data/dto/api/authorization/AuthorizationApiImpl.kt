package com.lawmobile.data.dto.api.authorization

import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.lawmobile.data.dto.entities.discoveryUrl.DiscoveryResponseDto
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header

class AuthorizationApiImpl(private val baseUrl: String, private val httpClient: HttpClient) :
    AuthorizationApi {

    private suspend fun getDiscoveryUrl(tenantID: String): String {
        val response = httpClient.get<DiscoveryResponseDto>(baseUrl) {
            header(HEADER, tenantID)
        }
        return response.ssoDiscovery
    }

    override suspend fun getAuthorizationEndpoints(tenantID: String): Result<AuthorizationEndpointsResponseDto> {
        return try {
            val discoveryUrl = getDiscoveryUrl(tenantID)
            val response = httpClient.get<AuthorizationEndpointsResponseDto>(discoveryUrl)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    companion object {
        private const val HEADER = "x-tenant-id"
    }
}
