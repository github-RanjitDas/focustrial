package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class PairingPhoneWithCameraRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk(relaxed = true)

    private val pairingPhoneWithCameraRemoteDataSourceImpl by lazy {
        PairingPhoneWithCameraRemoteDataSourceImpl(cameraConnectService)
    }


    @Test
    fun testLoadPairingCamera() {
        val progressPairingCamera: ((Result<Int>) -> Unit) = { }
        coEvery { cameraConnectService.loadPairingCamera(any(), any()) } just Runs

        runBlocking {
            pairingPhoneWithCameraRemoteDataSourceImpl.loadPairingCamera(
                "",
                "",
                progressPairingCamera
            )
        }
        Assert.assertTrue(cameraConnectService.progressPairingCamera != null)
        coVerify { cameraConnectService.loadPairingCamera("", "") }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { cameraConnectService.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { pairingPhoneWithCameraRemoteDataSourceImpl.isPossibleTheConnection("10.10.10.4") }
        coVerify { cameraConnectService.isPossibleTheConnection("10.10.10.4") }
    }
}