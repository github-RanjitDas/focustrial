package com.lawmobile.data.datasource.remote.validateOfficerId

import com.lawmobile.data.dto.api.ValidateOfficerIdApi
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.lang.Exception

@ExperimentalCoroutinesApi
internal class ValidateOfficerIdRemoteDataSourceImplTest {

    private val validateOfficerIdApi: ValidateOfficerIdApi = mockk(relaxed = true)
    private val validateOfficerIdRemoteDataSourceImpl =
        ValidateOfficerIdRemoteDataSourceImpl(validateOfficerIdApi)

    @Test
    fun validateOfficerIdSuccessTrue() = runBlockingTest {
        coEvery { validateOfficerIdApi.validateOfficerId(any()) } returns Result.Success(true)
        val result = validateOfficerIdRemoteDataSourceImpl.validateOfficerId("")
        coVerify { validateOfficerIdApi.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertTrue(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdSuccessFalse() = runBlockingTest {
        coEvery { validateOfficerIdApi.validateOfficerId(any()) } returns Result.Success(false)
        val result = validateOfficerIdRemoteDataSourceImpl.validateOfficerId("")
        coVerify { validateOfficerIdApi.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertFalse(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdError() = runBlockingTest {
        coEvery { validateOfficerIdApi.validateOfficerId(any()) } returns Result.Error(Exception())
        val result = validateOfficerIdRemoteDataSourceImpl.validateOfficerId("")
        coVerify { validateOfficerIdApi.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Error)
    }
}
