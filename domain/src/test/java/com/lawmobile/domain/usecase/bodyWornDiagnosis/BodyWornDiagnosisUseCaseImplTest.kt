package com.lawmobile.domain.usecase.bodyWornDiagnosis

import com.lawmobile.domain.repository.bodyWornDiagnosis.BodyWornDiagnosisRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class BodyWornDiagnosisUseCaseImplTest {

    private val repository: BodyWornDiagnosisRepository = mockk()
    private val bodyWornDiagnosisUseCaseImpl by lazy {
        BodyWornDiagnosisUseCaseImpl(repository)
    }

    @Test
    fun testIsDiagnosisSuccessFlowResponseTrue() {
        coEvery { repository.isDiagnosisSuccess() } returns Result.Success(true)
        runBlocking {
            val response = bodyWornDiagnosisUseCaseImpl.isDiagnosisSuccess()
            Assert.assertEquals(response, Result.Success(true))
        }
        coVerify { repository.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisSuccessFlowResponseFalse() {
        coEvery { repository.isDiagnosisSuccess() } returns Result.Success(false)
        runBlocking {
            val response = bodyWornDiagnosisUseCaseImpl.isDiagnosisSuccess()
            Assert.assertEquals(response, Result.Success(false))
        }
        coVerify { repository.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisSuccessFlowError() {
        coEvery { repository.isDiagnosisSuccess() } returns Result.Error(Exception(""))
        runBlocking {
            val response = bodyWornDiagnosisUseCaseImpl.isDiagnosisSuccess()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { repository.isDiagnosisSuccess() }
    }
}
