package com.lawmobile.data.repository.bodyWornDiagnosis

import com.lawmobile.data.datasource.remote.bodyWornDiagnosis.BodyWornDiagnosisDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class BodyWornDiagnosisRepositoryImplTest {

    private val bodyWornDiagnosisDataSource: BodyWornDiagnosisDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val bodyWornDiagnosisRepositoryImpl by lazy {
        BodyWornDiagnosisRepositoryImpl(bodyWornDiagnosisDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testIsDiagnosisSuccessFlowResponseTrue() = runBlockingTest {
        coEvery { bodyWornDiagnosisDataSource.isDiagnosisSuccess() } returns Result.Success(true)
        val result = bodyWornDiagnosisRepositoryImpl.isDiagnosisSuccess()
        Assert.assertEquals(result, Result.Success(true))
        coVerify { bodyWornDiagnosisDataSource.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisSuccessFlowResponseFalse() = runBlockingTest {
        coEvery { bodyWornDiagnosisDataSource.isDiagnosisSuccess() } returns Result.Success(false)
        val result = bodyWornDiagnosisRepositoryImpl.isDiagnosisSuccess()
        Assert.assertEquals(result, Result.Success(false))
        coVerify { bodyWornDiagnosisDataSource.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisSuccessFlowError() = runBlockingTest {
        coEvery {
            bodyWornDiagnosisDataSource.isDiagnosisSuccess()
        } returns Result.Error(Exception(""))
        val result = bodyWornDiagnosisRepositoryImpl.isDiagnosisSuccess()
        Assert.assertTrue(result is Result.Error)
        coVerify { bodyWornDiagnosisDataSource.isDiagnosisSuccess() }
    }
}
