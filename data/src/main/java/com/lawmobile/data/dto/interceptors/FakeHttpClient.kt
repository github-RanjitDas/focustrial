package com.lawmobile.data.dto.interceptors

import com.lawmobile.data.extensions.installKotlinxSerializer
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

object FakeHttpClient {
    private val responseHeaders =
        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

    fun create(): HttpClient {
        return HttpClient(MockEngine) {
            installKotlinxSerializer()
            configureMockEngine()
        }
    }

    private fun HttpClientConfig<MockEngineConfig>.configureMockEngine() {
        engine {
            addHandler { request ->
                val requestUrl = request.url.toString()
                val officerIdParam = request.url.parameters[OFFICER_ID]
                when {
                    isValidateOfficerIdApiRequest(requestUrl) -> {
                        val jsonResponse =
                            if (isValidUser(officerIdParam)) VALID_USER_JSON else INVALID_USER_JSON
                        respond(jsonResponse, HttpStatusCode.OK, responseHeaders)
                    }
                    isDiscoveryApiRequest(requestUrl) -> {
                        respond(DISCOVERY_RESPONSE, HttpStatusCode.OK, responseHeaders)
                    }
                    isAuthorizationApiRequest(requestUrl) -> {
                        respond(AUTHORIZATION_RESPONSE, HttpStatusCode.OK, responseHeaders)
                    }

                    else -> error("Unhandled $requestUrl")
                }
            }
        }
    }

    private fun isAuthorizationApiRequest(requestUrl: String) =
        requestUrl.contains(AUTHORIZATION_URL)

    private fun isDiscoveryApiRequest(requestUrl: String) = requestUrl.contains(DISCOVERY_URL)

    private fun isValidUser(officerIdParam: String?) = officerIdParam == VALID_USER

    private fun isValidateOfficerIdApiRequest(request: String) =
        request.contains(VALIDATE_OFFICER_ID_URL)

    private const val OFFICER_ID = "OfficerId"
    private const val VALIDATE_OFFICER_ID_URL = "validateOfficerId"
    private const val VALID_USER = "LuchoQA"
    private const val INVALID_USER_JSON = """{"isValidUser": false}"""
    private const val VALID_USER_JSON = """{"isValidUser": true}"""
    private const val AUTHORIZATION_RESPONSE =
        """{"authorization_endpoint":"https://dev-id.safefleetcloud.com/connect/authorize", "token_endpoint":"https://dev-id.safefleetcloud.com/connect/token"}"""
    private const val AUTHORIZATION_URL = ".well-known/openid-configuration"
    private const val DISCOVERY_URL = "tenant-settings/api/hardware/discovery"
    private const val DISCOVERY_RESPONSE =
        """{"sf_id_endpoint": "https://dev-id.safefleetcloud.us","firmware_endpoint": "https://dev-commander-api.safefleetcloud.us/hardware/firmware","heartbeat_endpoint": "https://dev-commander-api.safefleetcloud.us/hardware/heartbeat","categories_endpoint": "https://dev.safefleetcloud.us/tenant-settings/api/hardware/categories","checkouts_endpoint": "https://dev-commander-api.safefleetcloud.us/checkouts","current_checkout_endpoint": "https://dev-commander-api.safefleetcloud.us/hardware/currentCheckout","file_receiver_endpoint": "https://dev-file-receiver.safefleetcloud.us/files","config_endpoint": "https://dev-commander-api.safefleetcloud.us/hardware/config","users_endpoint": "https://dev.safefleetcloud.us/tenant-security/api/hardware/users","sso_discovery": "https://dev-id.safefleetcloud.com/.well-known/openid-configuration"}"""
}
