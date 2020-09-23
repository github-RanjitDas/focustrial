package com.lawmobile.presentation.ui.fileList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class FileListViewModelTest {

    private val fileListUseCase: FileListUseCase = mockk()
    private val fileListViewModel: FileListViewModel by lazy {
        FileListViewModel(fileListUseCase)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val domainInformationFileResponse:DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { fileListUseCase.getSnapshotList() } returns result
        fileListViewModel.getSnapshotList()
        Assert.assertEquals(fileListViewModel.snapshotListLiveData.value, result)
        coVerify { fileListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        val domainInformationFileResponse:DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { fileListUseCase.getVideoList() } returns result
        fileListViewModel.getVideoList()
        Assert.assertEquals(fileListViewModel.videoListLiveData.value, result)
        coVerify { fileListUseCase.getVideoList() }
    }

    @Test
    fun testGetSnapshotListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.getSnapshotList() } returns result
        fileListViewModel.getSnapshotList()
        Assert.assertEquals(fileListViewModel.snapshotListLiveData.value, result)
        coVerify { fileListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetVideoListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.getVideoList() } returns result
        fileListViewModel.getVideoList()
        Assert.assertEquals(fileListViewModel.videoListLiveData.value, result)
        coVerify { fileListUseCase.getVideoList() }
    }

    @Test
    fun testAssociatePartnerIdToVideoListSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdVideos(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToVideoList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.videoPartnerIdLiveData.value, result)
        coVerify { fileListUseCase.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToVideoListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdVideos(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToVideoList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.videoPartnerIdLiveData.value, result)
        coVerify { fileListUseCase.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToSnapshotListSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListUseCase.savePartnerIdSnapshot(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToSnapshotList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.snapshotPartnerIdLiveData.value, result)
        coVerify { fileListUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testAssociatePartnerIdToSnapshotListError() {
        val result = Result.Error(mockk())
        coEvery { fileListUseCase.savePartnerIdSnapshot(any(), any()) } returns result
        fileListViewModel.associatePartnerIdToSnapshotList(listOf(mockk()), "")
        Assert.assertEquals(fileListViewModel.snapshotPartnerIdLiveData.value, result)
        coVerify { fileListUseCase.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testLoadingTimeout() {
        fileListViewModel.loadingTimeout()
        runBlocking { delay(15000) }
        Assert.assertEquals(true, fileListViewModel.timeoutLiveData.value)
    }
}