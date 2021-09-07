package com.lawmobile.domain.usecase.validateOfficerId

import com.lawmobile.domain.repository.validateOfficerId.ValidateOfficerIdRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class ValidateOfficerIdUseCaseImplTest {

    private val repository: ValidateOfficerIdRepository = mockk()
    private val validateOfficerIdUseCaseImpl = ValidateOfficerIdUseCaseImpl(repository)

    @Test
    fun validateOfficerIdSuccessTrue() = runBlockingTest {
        coEvery { repository.validateOfficerId(any()) } returns Result.Success(true)
        val result = validateOfficerIdUseCaseImpl.validateOfficerId("")
        coVerify { repository.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertTrue(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdSuccessFalse() = runBlockingTest {
        coEvery { repository.validateOfficerId(any()) } returns Result.Success(false)
        val result = validateOfficerIdUseCaseImpl.validateOfficerId("")
        coVerify { repository.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertFalse(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdError() = runBlockingTest {
        coEvery { repository.validateOfficerId(any()) } returns Result.Error(Exception())
        val result = validateOfficerIdUseCaseImpl.validateOfficerId("")
        coVerify { repository.validateOfficerId(any()) }
        Assert.assertTrue(result is Result.Error)
    }
}
