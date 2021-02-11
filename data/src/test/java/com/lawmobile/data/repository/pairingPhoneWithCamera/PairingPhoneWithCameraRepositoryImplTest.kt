package com.lawmobile.data.repository.pairingPhoneWithCamera

import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraRepositoryImplTest {

    private val progressPairingCamera: ((Result<Int>) -> Unit) = { }
    private val pairingPhoneWithCameraRemoteDataSource: PairingPhoneWithCameraRemoteDataSource =
        mockk {
            coEvery { loadPairingCamera(any(), any(), any()) } just Runs
        }

    private val pairingPhoneWithCameraRepositoryImpl by lazy {
        PairingPhoneWithCameraRepositoryImpl(pairingPhoneWithCameraRemoteDataSource)
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        runBlocking {
            pairingPhoneWithCameraRepositoryImpl.loadPairingCamera("", "", progressPairingCamera)
        }

        coVerify {
            pairingPhoneWithCameraRemoteDataSource.loadPairingCamera("", "", progressPairingCamera)
        }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { pairingPhoneWithCameraRemoteDataSource.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { pairingPhoneWithCameraRepositoryImpl.isPossibleTheConnection("10.10.10.4") }
        coVerify { pairingPhoneWithCameraRemoteDataSource.isPossibleTheConnection("10.10.10.4") }
    }

    @Test
    fun testCleanCacheFiles(){
        coEvery { pairingPhoneWithCameraRemoteDataSource.cleanCacheFiles() } just Runs

        runBlocking {
            pairingPhoneWithCameraRepositoryImpl.cleanCacheFiles()
        }
        coVerify { pairingPhoneWithCameraRemoteDataSource.cleanCacheFiles() }

    }
}