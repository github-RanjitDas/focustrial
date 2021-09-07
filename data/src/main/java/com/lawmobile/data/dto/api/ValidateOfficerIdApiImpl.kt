package com.lawmobile.data.dto.api

import com.lawmobile.data.dto.entities.OfficerIdResponseDto
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ValidateOfficerIdApiImpl(
    private val baseUrl: String,
    private val httpClient: HttpClient
) : ValidateOfficerIdApi {
    override suspend fun validateOfficerId(officerId: String): Result<Boolean> {
        return try {
            val response = httpClient.get<OfficerIdResponseDto>(baseUrl) {
                parameter(OFFICER_ID, officerId)
            }
            Result.Success(response.isValidUser)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    companion object {
        private const val OFFICER_ID = "OfficerId"
    }
}
