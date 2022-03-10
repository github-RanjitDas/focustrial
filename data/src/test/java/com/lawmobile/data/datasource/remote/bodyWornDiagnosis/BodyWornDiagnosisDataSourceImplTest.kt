package com.lawmobile.data.datasource.remote.bodyWornDiagnosis

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BodyWornDiagnosisDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }

    private val bodyWornDiagnosisDataSourceImpl =
        BodyWornDiagnosisDataSourceImpl(cameraServiceFactory)

    @Test
    fun testIsDiagnosisSuccess() {
        coEvery { bodyWornDiagnosisDataSourceImpl.isDiagnosisSuccess() } returns Result.Success(true)
        runBlocking {
            val firstAttempt = bodyWornDiagnosisDataSourceImpl.isDiagnosisSuccess()
            Assert.assertEquals(firstAttempt, Result.Success(true))
        }
    }

    @Test
    fun testIsDiagnosisSuccessFail() {
        coEvery { bodyWornDiagnosisDataSourceImpl.isDiagnosisSuccess() } returns Result.Success(
            false
        )
        runBlocking {
            val firstAttempt = bodyWornDiagnosisDataSourceImpl.isDiagnosisSuccess()
            Assert.assertTrue(firstAttempt is Result.Success)
        }
    }
}
