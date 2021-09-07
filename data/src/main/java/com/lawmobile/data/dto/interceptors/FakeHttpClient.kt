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
                when {
                    isValidateOfficerIdApiRequest(request.url.toString()) ->
                        respond(VALIDATE_USER_JSON, HttpStatusCode.OK, responseHeaders)
                    else -> error("Unhandled ${request.url}")
                }
            }
        }
    }

    private fun isValidateOfficerIdApiRequest(request: String) =
        request.contains("validateOfficerId")

    private const val VALIDATE_USER_JSON = """{"isValidUser": false}"""
}
