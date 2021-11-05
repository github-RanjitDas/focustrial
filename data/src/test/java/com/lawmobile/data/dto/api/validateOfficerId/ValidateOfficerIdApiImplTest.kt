package com.lawmobile.data.dto.api.validateOfficerId

import com.lawmobile.data.dto.interceptors.FakeHttpClient
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class ValidateOfficerIdApiImplTest {

    private val httpClient: HttpClient = FakeHttpClient.create()
    private val validateOfficerIdApiImpl = ValidateOfficerIdApiImpl("validateOfficerId", httpClient)

    @Test
    fun validateOfficerIdSuccessFalse() = runBlocking {
        val result = validateOfficerIdApiImpl.validateOfficerId("")
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertFalse(resultSuccess.data)
    }
}
