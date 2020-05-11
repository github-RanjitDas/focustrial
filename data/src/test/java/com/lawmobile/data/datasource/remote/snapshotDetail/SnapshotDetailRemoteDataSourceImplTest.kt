package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnapshotDetailRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()
    private val snapshotDetailRemoteDataSourceImpl by lazy {
        SnapshotDetailRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetImageBytesSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val byte = ByteArray(1)
        coEvery { cameraConnectService.getImageBytes(cameraConnectFile) } returns Result.Success(byte)
        runBlocking {
            val result =
                snapshotDetailRemoteDataSourceImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { cameraConnectService.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraConnectService.getImageBytes(cameraConnectFile) } returns Result.Error(mockk())
        runBlocking {
            val result =
                snapshotDetailRemoteDataSourceImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { cameraConnectService.getImageBytes(cameraConnectFile) }
    }
}