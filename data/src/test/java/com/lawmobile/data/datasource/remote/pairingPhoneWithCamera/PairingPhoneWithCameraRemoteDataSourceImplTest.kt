package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
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

    private val progressCamera: LiveData<Result<Int>> = mockk()
    private val cameraConnectService: CameraConnectService = mockk {
        every { progressPairingCamera } returns progressCamera
    }

    private val pairingPhoneWithCameraRemoteDataSourceImpl by lazy {
        PairingPhoneWithCameraRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testLoadPairingCamera() {
        coEvery { cameraConnectService.loadPairingCamera(any(), any()) } just Runs
        runBlocking {
            pairingPhoneWithCameraRemoteDataSourceImpl.loadPairingCamera("", "")
        }

        Assert.assertEquals(
            pairingPhoneWithCameraRemoteDataSourceImpl.progressPairingCamera,
            progressCamera
        )

        coVerify { cameraConnectService.loadPairingCamera("", "") }
    }
}