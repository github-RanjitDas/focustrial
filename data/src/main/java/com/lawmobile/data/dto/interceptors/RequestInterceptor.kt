package com.lawmobile.data.dto.interceptors

import com.lawmobile.domain.utils.PreferencesManager
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.util.pipeline.PipelineContext

class RequestInterceptor(private val preferencesManager: PreferencesManager) {
    fun start(httpClient: HttpClient) {
        httpClient.sendPipeline.intercept(HttpSendPipeline.State) {
            val request = context.url.buildString()
            when {
                isDiscoveryRequest(request) -> {
                    interceptDiscoveryRequest()
                    return@intercept
                }
                isUserRequest(request) -> {
                    interceptUserRequest()
                    return@intercept
                }
            }
        }
    }

    private suspend fun PipelineContext<Any, HttpRequestBuilder>.interceptUserRequest() {
        val token = preferencesManager.getToken()
        val tenantId = preferencesManager.getTenantId()
        val serialNumber = preferencesManager.getSerialNumber()

        context.headers {
            append(AUTHORIZATION, "Bearer $token")
            append(TENANT_ID, tenantId)
            append(SERIAL_NUMBER, serialNumber)
        }
    }

    private suspend fun PipelineContext<Any, HttpRequestBuilder>.interceptDiscoveryRequest() {
        val tenantId = preferencesManager.getTenantId()
        context.header(TENANT_ID, tenantId)
    }

    private suspend fun isDiscoveryRequest(request: String) =
        request.contains(preferencesManager.getDiscoveryEndpointUrl())

    private suspend fun isUserRequest(request: String) =
        request.contains(preferencesManager.getUsersEndpointUrl())

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val TENANT_ID = "x-tenant-id"
        private const val SERIAL_NUMBER = "x-serial-number"
    }
}
