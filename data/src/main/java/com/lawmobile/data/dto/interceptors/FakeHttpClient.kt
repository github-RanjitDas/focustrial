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
                    else -> error("Unhandled $requestUrl")
                }
            }
        }
    }

    private fun isValidUser(officerIdParam: String?) = officerIdParam == VALID_USER

    private fun isValidateOfficerIdApiRequest(request: String) =
        request.contains(VALIDATE_OFFICER_ID_URL)

    private const val OFFICER_ID = "OfficerId"
    private const val VALIDATE_OFFICER_ID_URL = "validateOfficerId"
    private const val VALID_USER = "LuchoQA"
    private const val INVALID_USER_JSON = """{"isValidUser": false}"""
    private const val VALID_USER_JSON = """{"isValidUser": true}"""
}
