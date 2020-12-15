package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
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
        val domainCameraFile: DomainCameraFile = mockk()
        val byte = ByteArray(1)
        coEvery { snapshotDetailRepository.getImageBytes(domainCameraFile) } returns Result.Success(
            byte
        )
        runBlocking {
            val result =
                snapshotDetailUseCaseImpl.getImageBytes(domainCameraFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { snapshotDetailRepository.getImageBytes(domainCameraFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val domainCameraFile: DomainCameraFile = mockk()
        coEvery { snapshotDetailRepository.getImageBytes(domainCameraFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                snapshotDetailUseCaseImpl.getImageBytes(domainCameraFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { snapshotDetailRepository.getImageBytes(domainCameraFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery {
            snapshotDetailRepository.saveSnapshotPartnerId(
                any(),
                "partnerId"
            )
        } returns Result.Success(Unit)
        runBlocking {
            val result = snapshotDetailUseCaseImpl.savePartnerIdSnapshot(mockk(), "partnerId")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify {
            snapshotDetailRepository.saveSnapshotPartnerId(any(), "partnerId")
        }
    }

    @Test
    fun testGetInformationOfPhoto() {
        coEvery { snapshotDetailRepository.getInformationOfPhoto(any()) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result = snapshotDetailUseCaseImpl.getInformationOfPhoto(mockk())
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            snapshotDetailRepository.getInformationOfPhoto(any())
        }
    }
}