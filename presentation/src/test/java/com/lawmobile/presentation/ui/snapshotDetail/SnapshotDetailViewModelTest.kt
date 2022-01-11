package com.lawmobile.presentation.ui.snapshotDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.ui.snapshotDetail.state.SnapshotDetailState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SnapshotDetailViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val job: Job by lazy { Job() }
    private val testScope: CoroutineScope by lazy { TestCoroutineScope(job + dispatcher) }

    private val snapshotDetailUseCase: SnapshotDetailUseCase = mockk()
    private val viewModel by lazy { SnapshotDetailViewModel(snapshotDetailUseCase) }

    @BeforeEach
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterEach
    fun clean() = job.cancel()

    @Test
    fun testGetImageBytesSuccess() {
        val domainCameraFile: DomainCameraFile = mockk()
        val byte = ByteArray(1)

        coEvery { snapshotDetailUseCase.getImageBytes(any()) } returns Result.Success(byte)
        viewModel.getImageBytes(domainCameraFile)

        testScope.launch {
            val response = viewModel.imageBytes.first()
            Assert.assertTrue(response is Result.Success)
        }

        coVerify { snapshotDetailUseCase.getImageBytes(any()) }
    }

    @Test
    fun testGetImageBytesError() {
        val domainCameraFile: DomainCameraFile = mockk()

        coEvery { snapshotDetailUseCase.getImageBytes(any()) } returns Result.Error(mockk())

        testScope.launch {
            viewModel.getImageBytes(domainCameraFile)
            dispatcher.advanceTimeBy(1001)
            val response = viewModel.imageBytes.first()
            Assert.assertTrue(response is Result.Error)
        }

        coVerify { snapshotDetailUseCase.getImageBytes(any()) }
    }

    @Test
    fun testSavePartnerIdSuccess() {
        coEvery {
            snapshotDetailUseCase.savePartnerIdSnapshot(any(), any())
        } returns Result.Success(Unit)

        testScope.launch {
            viewModel.savePartnerId(mockk(relaxed = true), "partnerId")
            val valueLiveData = viewModel.associationResult.first()
            Assert.assertTrue(valueLiveData is Result.Success)
        }

        coVerify { snapshotDetailUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testSavePartnerIdError() {
        coEvery {
            snapshotDetailUseCase.savePartnerIdSnapshot(any(), any())
        } returns Result.Error(mockk())

        testScope.launch {
            viewModel.savePartnerId(mockk(relaxed = true), "partnerId")
            val valueLiveData = viewModel.associationResult.first()
            Assert.assertTrue(valueLiveData is Result.Error)
        }
    }

    @Test
    fun testGetInformationImageMetadataSuccess() {
        coEvery {
            snapshotDetailUseCase.getInformationOfPhoto(any())
        } returns Result.Success(mockk(relaxed = true))

        testScope.launch {
            viewModel.getImageInformation(mockk())
            val valueLiveData = viewModel.imageInformation.first()
            Assert.assertTrue(valueLiveData is Result.Success)
        }

        coVerify { snapshotDetailUseCase.getInformationOfPhoto(any()) }
    }

    @Test
    fun testGetInformationImageMetadataError() {
        coEvery {
            snapshotDetailUseCase.getInformationOfPhoto(any())
        } returns Result.Error(mockk())
        testScope.launch {
            viewModel.getImageInformation(mockk())
            dispatcher.advanceTimeBy(5100)
            val valueLiveData = viewModel.imageInformation.first()
            Assert.assertTrue(valueLiveData is Result.Error)
        }
    }

    @Test
    fun isAssociateDialogOpen() {
        Assert.assertFalse(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun setAssociateDialogOpen() {
        viewModel.isAssociateDialogOpen = true
        Assert.assertTrue(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun getState() {
        Assert.assertTrue(viewModel.getState() is SnapshotDetailState.Default)
    }

    @Test
    fun setState() {
        viewModel.setState(SnapshotDetailState.FullScreen)
        Assert.assertTrue(viewModel.getState() is SnapshotDetailState.FullScreen)
    }
}
