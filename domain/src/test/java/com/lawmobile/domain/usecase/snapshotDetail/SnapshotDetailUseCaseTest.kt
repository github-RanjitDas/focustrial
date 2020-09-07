package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class SnapshotDetailUseCaseTest {

    private val snapshotDetailRepository: SnapshotDetailRepository = mockk()
    private val snapshotDetailUseCaseImpl by lazy {
        SnapshotDetailUseCaseImpl(snapshotDetailRepository)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val byte = ByteArray(1)
        coEvery { snapshotDetailRepository.getImageBytes(cameraConnectFile) } returns Result.Success(
            byte
        )
        runBlocking {
            val result =
                snapshotDetailUseCaseImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { snapshotDetailRepository.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { snapshotDetailRepository.getImageBytes(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                snapshotDetailUseCaseImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { snapshotDetailRepository.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow(){
        coEvery { snapshotDetailRepository.savePartnerIdSnapshot(any(), "partnerId") } returns Result.Success(Unit)
        runBlocking {
            val result = snapshotDetailUseCaseImpl.savePartnerIdSnapshot(mockk(),"partnerId")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify {
            snapshotDetailRepository.savePartnerIdSnapshot(any(), "partnerId")
        }
    }

    @Test
    fun testGetInformationOfPhoto() {
        coEvery { snapshotDetailRepository.getInformationOfPhoto(any()) } returns  Result.Success(mockk())
        runBlocking {
            val result = snapshotDetailUseCaseImpl.getInformationOfPhoto(mockk())
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            snapshotDetailRepository.getInformationOfPhoto(any())
        }
    }
}