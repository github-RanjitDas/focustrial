package com.lawmobile.data.repository.validateOfficerId

import com.lawmobile.data.datasource.remote.validateOfficerId.ValidateOfficerIdRemoteDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class ValidateOfficerIdRepositoryImplTest {

    private val dataSource: ValidateOfficerIdRemoteDataSource = mockk()
    private val validateOfficerIdRepositoryImpl = ValidateOfficerIdRepositoryImpl(dataSource)

    @Test
    fun validateOfficerIdSuccessTrue() = runBlockingTest {
        coEvery { dataSource.validateOfficerId(any()) } returns Result.Success(true)
        val result = validateOfficerIdRepositoryImpl.validateOfficerId("")
        coVerify { dataSource.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertTrue(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdSuccessFalse() = runBlockingTest {
        coEvery { dataSource.validateOfficerId(any()) } returns Result.Success(false)
        val result = validateOfficerIdRepositoryImpl.validateOfficerId("")
        coVerify { dataSource.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertFalse(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdError() = runBlockingTest {
        coEvery { dataSource.validateOfficerId(any()) } returns Result.Error(Exception())
        val result = validateOfficerIdRepositoryImpl.validateOfficerId("")
        coVerify { dataSource.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Error)
    }
}
