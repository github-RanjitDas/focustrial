package com.lawmobile.presentation.ui.fileList

import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class FileListViewModelTest {

    private val fileListUseCase: FileListUseCase = mockk()
    private val fileListViewModel: FileListViewModel by lazy {
        FileListViewModel(fileListUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testAssociatePartnerIdToVideoListSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdVideos(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToVideoList(listOf(mockk()), "")
        Assert.assertEquals(result, fileListViewModel.associatePartnerIdResult.value)
        coVerify { fileListUseCase.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToVideoListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdVideos(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToVideoList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.associatePartnerIdResult.value, result)
        coVerify { fileListUseCase.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToSnapshotListSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdSnapshot(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToSnapshotList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.associatePartnerIdResult.value, result)
        coVerify { fileListUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToSnapshotListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdSnapshot(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToSnapshotList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.associatePartnerIdResult.value, result)
        coVerify { fileListUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToAudiosListSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdAudios(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToAudioList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.associatePartnerIdResult.value, result)
        coVerify { fileListUseCase.savePartnerIdAudios(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToAudiosListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdAudios(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToAudioList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.associatePartnerIdResult.value, result)
        coVerify { fileListUseCase.savePartnerIdAudios(any(), any()) }
    }
}
