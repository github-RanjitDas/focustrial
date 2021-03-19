package com.lawmobile.data.repository.bodyWornDiagnosis

import com.lawmobile.data.datasource.remote.bodyWornDiagnosis.BodyWornDiagnosisDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class BodyWornDiagnosisRepositoryImplTest {

    private val bodyWornDiagnosisDataSource: BodyWornDiagnosisDataSource = mockk()
    private val bodyWornDiagnosisRepositoryImpl by lazy {
        BodyWornDiagnosisRepositoryImpl(bodyWornDiagnosisDataSource)
    }

    @Test
    fun testIsDiagnosisSuccessFlowResponseTrue() {
        coEvery { bodyWornDiagnosisDataSource.isDiagnosisSuccess() } returns Result.Success(true)
        runBlocking {
            val result = bodyWornDiagnosisRepositoryImpl.isDiagnosisSuccess()
            Assert.assertEquals(result, Result.Success(true))
        }
        coVerify { bodyWornDiagnosisDataSource.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisSuccessFlowResponseFalse() {
        coEvery { bodyWornDiagnosisDataSource.isDiagnosisSuccess() } returns Result.Success(false)
        runBlocking {
            val result = bodyWornDiagnosisRepositoryImpl.isDiagnosisSuccess()
            Assert.assertEquals(result, Result.Success(false))
        }
        coVerify { bodyWornDiagnosisDataSource.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisSuccessFlowError() {
        coEvery { bodyWornDiagnosisDataSource.isDiagnosisSuccess() } returns Result.Error(Exception(""))
        runBlocking {
            val result = bodyWornDiagnosisRepositoryImpl.isDiagnosisSuccess()
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { bodyWornDiagnosisDataSource.isDiagnosisSuccess() }
    }
}
