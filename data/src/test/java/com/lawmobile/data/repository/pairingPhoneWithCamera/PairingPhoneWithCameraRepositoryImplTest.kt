package com.lawmobile.data.repository.pairingPhoneWithCamera

import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraRepositoryImplTest {

    private val progressPairingCamera: ((Result<Int>) -> Unit) = { }
    private val pairingRemoteDataSource: PairingPhoneWithCameraRemoteDataSource =
        mockk {
            coEvery { loadPairingCamera(any(), any(), any()) } just Runs
        }
    private val dispatcher = TestCoroutineDispatcher()

    private val pairingRepositoryImpl by lazy {
        PairingPhoneWithCameraRepositoryImpl(pairingRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() = runBlockingTest {
        pairingRepositoryImpl.loadPairingCamera("", "", progressPairingCamera)
        coVerify {
            pairingRemoteDataSource.loadPairingCamera("", "", progressPairingCamera)
        }
    }

    @Test
    fun testIsPossibleTheConnectionSuccess() = runBlockingTest {
        coEvery {
            pairingRemoteDataSource.isPossibleTheConnection(any())
        } returns Result.Success(Unit)
        val result = pairingRepositoryImpl.isPossibleTheConnection("10.10.10.4")
        coVerify { pairingRemoteDataSource.isPossibleTheConnection("10.10.10.4") }
        Assert.assertTrue(result is Result.Success)
    }

    @Test
    fun testIsPossibleTheConnectionError() = runBlockingTest {
        coEvery {
            pairingRemoteDataSource.isPossibleTheConnection(any())
        } returns Result.Error(Exception())
        val result = pairingRepositoryImpl.isPossibleTheConnection("10.10.10.4")
        coVerify { pairingRemoteDataSource.isPossibleTheConnection("10.10.10.4") }
        Assert.assertTrue(result is Result.Error)
    }

    @Test
    fun testCleanCacheFiles() {
        every { pairingRemoteDataSource.cleanCacheFiles() } just Runs
        pairingRepositoryImpl.cleanCacheFiles()
        verify { pairingRemoteDataSource.cleanCacheFiles() }
    }
}
