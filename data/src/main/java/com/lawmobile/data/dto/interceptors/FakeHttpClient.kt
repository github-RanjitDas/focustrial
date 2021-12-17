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
                when {
                    isDiscoveryApiRequest(requestUrl) -> {
                        respond(DISCOVERY_RESPONSE, HttpStatusCode.OK, responseHeaders)
                    }
                    isAuthorizationApiRequest(requestUrl) -> {
                        respond(AUTHORIZATION_RESPONSE, HttpStatusCode.OK, responseHeaders)
                    }
                    isUserApiRequest(requestUrl) -> {
                        respond(USERS_RESPONSE, HttpStatusCode.OK, responseHeaders)
                    }
                    else -> error("Unhandled $requestUrl")
                }
            }
        }
    }

    private fun isUserApiRequest(requestUrl: String): Boolean = requestUrl.contains(USERS_URL)

    private fun isAuthorizationApiRequest(requestUrl: String) =
        requestUrl.contains(AUTHORIZATION_URL)

    private fun isDiscoveryApiRequest(requestUrl: String) = requestUrl.contains(DISCOVERY_URL)

    const val AUTHORIZATION_URL = "authorization_url"
    const val DISCOVERY_URL = "discovery_url"
    const val USERS_URL = "users_url"

    private const val USERS_RESPONSE =
        """[{"id": "e1f66203-ff65-41fc-bdc0-b1b2818acbc6","email": "Nicolas.CaceresR@perficient.com","devicePassword": null},{"id": "e423802f-5e99-454c-a6af-95f3b5c17914","email": "kevin.menesesp@perficient.com","devicePassword": "AAAAACAAAYagAAAAEDjK6fuGQI28lLVB3ZX5T0CsNy1gXYOAPSie90sRq+VRiHKBCKwaI46La6OGJJdOvw=="}]"""
    private const val AUTHORIZATION_RESPONSE =
        """{"authorization_endpoint":"https://dev-id.safefleetcloud.com/connect/authorize", "token_endpoint":"https://dev-id.safefleetcloud.com/connect/token"}"""
    private const val DISCOVERY_RESPONSE =
        """{"sf_id_configuration": "https://dev-id.safefleetcloud.com/.well-known/openid-configuration", "users_endpoint": "https://dev.safefleetcloud.us/tenant-security/api/hardware/users"}"""
}
