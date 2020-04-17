package com.lawmobile.data.repository.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraRepositoryImplTest {

    private val progressCamera: LiveData<Result<Int>> = mockk()
    private val pairingPhoneWithCameraRemoteDataSource: PairingPhoneWithCameraRemoteDataSource =
        mockk {
            every { progressPairingCamera } returns progressCamera
            coEvery { loadPairingCamera(any(), any()) } just Runs
            every { getSSIDSavedIfExist() } returns Result.Success("123456789")
            every { saveSerialNumberOfCamera(any()) } just Runs
        }

    private val pairingPhoneWithCameraRepositoryImpl by lazy {
        PairingPhoneWithCameraRepositoryImpl(pairingPhoneWithCameraRemoteDataSource)
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        runBlocking {
            pairingPhoneWithCameraRepositoryImpl.loadPairingCamera("", "")
        }

        coVerify {
            pairingPhoneWithCameraRemoteDataSource.loadPairingCamera("", "")
        }
    }

    @Test
    fun testToCheckPairingLiveData() {
        Assert.assertEquals(
            pairingPhoneWithCameraRepositoryImpl.progressPairingCamera,
            progressCamera
        )
    }

    @Test
    fun testGetSSIDSavedIfExist() {
        pairingPhoneWithCameraRepositoryImpl.getSSIDSavedIfExist()
        verify { pairingPhoneWithCameraRemoteDataSource.getSSIDSavedIfExist() }
    }

    @Test
    fun testSaveSerialNumberOfCamera() {
        pairingPhoneWithCameraRepositoryImpl.saveSerialNumberOfCamera("123456789")
        verify { pairingPhoneWithCameraRemoteDataSource.saveSerialNumberOfCamera("123456789") }
    }
}