package com.lawmobile.presentation.ui.snapshotDetail

import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class SnapshotDetailViewModelTest {

    private val snapshotDetailUseCase: SnapshotDetailUseCase = mockk()

    private val snapshotDetailViewModel by lazy {
        SnapshotDetailViewModel(snapshotDetailUseCase)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetImageBytesSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val byte = ByteArray(1)
        coEvery { snapshotDetailUseCase.getImageBytes(cameraConnectFile) } returns Result.Success(
            byte
        )
        runBlocking {
            snapshotDetailViewModel.getImageBytes(cameraConnectFile)
            Assert.assertTrue(snapshotDetailViewModel.imageBytesLiveData.value is Result.Success)
        }

        coVerify { snapshotDetailUseCase.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { snapshotDetailUseCase.getImageBytes(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            snapshotDetailViewModel.getImageBytes(cameraConnectFile)
            Assert.assertTrue(snapshotDetailViewModel.imageBytesLiveData.value is Result.Error)
        }

        coVerify { snapshotDetailUseCase.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdSuccess() {
        coEvery {
            snapshotDetailUseCase.savePartnerIdSnapshot(
                any(),
                any()
            )
        } returns Result.Success(Unit)
        runBlocking {
            snapshotDetailViewModel.savePartnerId(mockk(relaxed = true), "partnerId")
            val valueLiveData = snapshotDetailViewModel.savePartnerIdLiveData.value
            Assert.assertTrue(valueLiveData is Result.Success)
        }

        coVerify { snapshotDetailUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testSavePartnerIdError() {
        coEvery {
            snapshotDetailUseCase.savePartnerIdSnapshot(
                any(),
                any()
            )
        } returns Result.Error(mockk())
        runBlocking {
            snapshotDetailViewModel.savePartnerId(mockk(relaxed = true), "partnerId")
            val valueLiveData = snapshotDetailViewModel.savePartnerIdLiveData.value
            Assert.assertTrue(valueLiveData is Result.Error)
        }
    }

    @Test
    fun testGetInformationImageMetadataSuccess() {
        coEvery {
            snapshotDetailUseCase.getInformationOfPhoto(any())
        } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            snapshotDetailViewModel.getInformationImageMetadata(mockk())
            val valueLiveData = snapshotDetailViewModel.informationImageLiveData.value
            Assert.assertTrue(valueLiveData is Result.Success)
        }

        coVerify { snapshotDetailUseCase.getInformationOfPhoto(any()) }
    }

    @Test
    fun testGetInformationImageMetadataError() {
        coEvery {
            snapshotDetailUseCase.getInformationOfPhoto(any())
        } returns Result.Error(mockk())
        runBlocking {
            snapshotDetailViewModel.getInformationImageMetadata(mockk())
            val valueLiveData = snapshotDetailViewModel.informationImageLiveData.value
            Assert.assertTrue(valueLiveData is Result.Error)
        }
    }
}