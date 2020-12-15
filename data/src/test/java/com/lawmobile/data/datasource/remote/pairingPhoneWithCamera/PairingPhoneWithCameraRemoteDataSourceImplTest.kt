package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class PairingPhoneWithCameraRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk(relaxed = true)

    private val pairingPhoneWithCameraRemoteDataSourceImpl by lazy {
        PairingPhoneWithCameraRemoteDataSourceImpl(cameraService)
    }


    @Test
    fun testLoadPairingCamera() {
        val progressPairingCamera: ((Result<Int>) -> Unit) = { }
        coEvery { cameraService.loadPairingCamera(any(), any()) } just Runs

        runBlocking {
            pairingPhoneWithCameraRemoteDataSourceImpl.loadPairingCamera(
                "",
                "",
                progressPairingCamera
            )
        }
        Assert.assertTrue(cameraService.progressPairingCamera != null)
        coVerify { cameraService.loadPairingCamera("", "") }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { cameraService.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { pairingPhoneWithCameraRemoteDataSourceImpl.isPossibleTheConnection("10.10.10.4") }
        coVerify { cameraService.isPossibleTheConnection("10.10.10.4") }
    }
}