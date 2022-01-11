package com.lawmobile.presentation.ui.fileList

import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.ui.fileList.state.FileListState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FileListBaseViewModelTest {

    private val fileListUseCase: FileListUseCase = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val viewModel: FileListBaseViewModel by lazy {
        FileListBaseViewModel(fileListUseCase)
    }

    private val job by lazy { Job() }
    private val testScope: TestCoroutineScope by lazy { TestCoroutineScope(job + dispatcher) }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel.filesToAssociate = listOf(mockk())
    }

    @AfterEach
    fun clean() = job.cancel()

    @Test
    fun testAssociatePartnerIdToVideoListSuccess() = runBlockingTest {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdVideos(any(), any()) } returns result
        viewModel.associateOfficerToVideos("")
        testScope.launch { Assert.assertEquals(result, viewModel.associationResult.first()) }
        coVerify { fileListUseCase.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToVideoListError() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdVideos(any(), any()) } returns result
        viewModel.associateOfficerToVideos("")
        testScope.launch { Assert.assertEquals(viewModel.associationResult.first(), result) }
        coVerify { fileListUseCase.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToSnapshotListSuccess() = runBlockingTest {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdSnapshot(any(), any()) } returns result
        viewModel.associateOfficerToSnapshots("")
        testScope.launch { Assert.assertEquals(viewModel.associationResult.first(), result) }
        coVerify { fileListUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToSnapshotListError() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdSnapshot(any(), any()) } returns result
        viewModel.associateOfficerToSnapshots("")
        testScope.launch { Assert.assertEquals(viewModel.associationResult.first(), result) }
        coVerify { fileListUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun setSelectActive() {
        viewModel.isSelectActive = true
        Assert.assertTrue(viewModel.isSelectActive)
    }

    @Test
    fun setFilterDialogOpen() {
        viewModel.isFilterDialogOpen = true
        Assert.assertTrue(viewModel.isFilterDialogOpen)
    }

    @Test
    fun setAssociateDialogOpen() {
        viewModel.isAssociateDialogOpen = true
        Assert.assertTrue(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun setFilesToAssociate() {
        viewModel.filesToAssociate = null
        Assert.assertTrue(viewModel.filesToAssociate.isNullOrEmpty())
    }

    @Test
    fun setFileListState() {
        viewModel.setFileListState(FileListState.Simple)
        Assert.assertTrue(viewModel.getFileListState() is FileListState.Simple)
    }

    @Test
    fun setFileListStateFlow() = runBlockingTest {
        viewModel.setFileListState(FileListState.Simple)
        testScope.launch {
            Assert.assertTrue(viewModel.fileListState.first() is FileListState.Simple)
        }
    }
}
