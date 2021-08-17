package com.lawmobile.presentation.ui.snapshotDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class SnapshotDetailViewModelTest {

    private val snapshotDetailUseCase: SnapshotDetailUseCase = mockk()

    private val snapshotDetailViewModel by lazy {
        SnapshotDetailViewModel(snapshotDetailUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetImageBytesSuccess() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()
        val byte = ByteArray(1)

        coEvery { snapshotDetailUseCase.getImageBytes(any()) } returns Result.Success(byte)

        snapshotDetailViewModel.getImageBytes(domainCameraFile)
        val response = snapshotDetailViewModel.imageBytesLiveData.value?.getContent()
        Assert.assertTrue(response is Result.Success)

        coVerify { snapshotDetailUseCase.getImageBytes(any()) }
    }

    @Test
    fun testGetImageBytesError() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()

        coEvery { snapshotDetailUseCase.getImageBytes(any()) } returns Result.Error(mockk())

        snapshotDetailViewModel.getImageBytes(domainCameraFile)
        dispatcher.advanceTimeBy(1000)
        val response = snapshotDetailViewModel.imageBytesLiveData.value?.getContent()
        Assert.assertTrue(response is Result.Error)

        coVerify { snapshotDetailUseCase.getImageBytes(any()) }
    }

    @Test
    fun testSavePartnerIdSuccess() = runBlockingTest {
        coEvery {
            snapshotDetailUseCase.savePartnerIdSnapshot(any(), any())
        } returns Result.Success(Unit)

        snapshotDetailViewModel.savePartnerId(mockk(relaxed = true), "partnerId")
        val valueLiveData = snapshotDetailViewModel.savePartnerIdLiveData.value?.getContent()
        Assert.assertTrue(valueLiveData is Result.Success)

        coVerify { snapshotDetailUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testSavePartnerIdError() = runBlockingTest {
        coEvery {
            snapshotDetailUseCase.savePartnerIdSnapshot(any(), any())
        } returns Result.Error(mockk())

        snapshotDetailViewModel.savePartnerId(mockk(relaxed = true), "partnerId")
        val valueLiveData = snapshotDetailViewModel.savePartnerIdLiveData.value?.getContent()
        Assert.assertTrue(valueLiveData is Result.Error)
    }

    @Test
    fun testGetInformationImageMetadataSuccess() = runBlockingTest {
        coEvery {
            snapshotDetailUseCase.getInformationOfPhoto(any())
        } returns Result.Success(mockk(relaxed = true))

        snapshotDetailViewModel.getInformationImageMetadata(mockk())
        val valueLiveData = snapshotDetailViewModel.informationImageLiveData.value?.getContent()
        Assert.assertTrue(valueLiveData is Result.Success)

        coVerify { snapshotDetailUseCase.getInformationOfPhoto(any()) }
    }

    @Test
    fun testGetInformationImageMetadataError() = runBlockingTest {
        coEvery { snapshotDetailUseCase.getInformationOfPhoto(any()) } returns Result.Error(mockk())
        snapshotDetailViewModel.getInformationImageMetadata(mockk())
        dispatcher.advanceTimeBy(5100)
        val valueLiveData = snapshotDetailViewModel.informationImageLiveData.value?.getContent()
        Assert.assertTrue(valueLiveData is Result.Error)
    }
}
