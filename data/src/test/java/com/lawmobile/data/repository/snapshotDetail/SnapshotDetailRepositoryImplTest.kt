package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class SnapshotDetailRepositoryImplTest {

    private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource = mockk()
    private val snapshotDetailRepositoryImpl by lazy {
        SnapshotDetailRepositoryImpl(snapshotDetailRemoteDataSource)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val byte = ByteArray(1)
        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns Result.Success(byte)
        runBlocking {
            val result =
                snapshotDetailRepositoryImpl.getImageBytes(cameraConnectFile) as Result.Success
            Assert.assertEquals(result.data, byte)
        }

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                snapshotDetailRepositoryImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }
}