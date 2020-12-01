package com.lawmobile.presentation.ui.simpleList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
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
internal class SimpleListViewModelTest {
    private val simpleListUseCase: SimpleListUseCase = mockk()
    private var simpleListViewModel: SimpleListViewModel = SimpleListViewModel(simpleListUseCase)

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        simpleListViewModel = SimpleListViewModel(simpleListUseCase)
        Dispatchers.setMain(Dispatchers.Unconfined)
        mockkObject(BaseViewModel)
        every { BaseViewModel.getLoadingTimeOut() } returns 1000
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getSnapshotList() } returns result
        simpleListViewModel.getSnapshotList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListError() {
        val result = Result.Error(mockk())
        coEvery { simpleListUseCase.getSnapshotList() } returns result
        simpleListViewModel.getSnapshotList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListErrorTimeOut() {
        mockkObject(BaseViewModel)
        every { BaseViewModel.getLoadingTimeOut() } returns 0
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getSnapshotList() } returns result
        runBlocking {
            simpleListViewModel.getSnapshotList()
            Assert.assertTrue(simpleListViewModel.fileListLiveData.value is Result.Error)
        }
    }

    @Test
    fun testGetVideoListSuccess() {
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getVideoList() } returns result
        simpleListViewModel.getVideoList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getVideoList() }
    }

    @Test
    fun testGetVideoListError() {
        val result = Result.Error(mockk())
        coEvery { simpleListUseCase.getVideoList() } returns result
        simpleListViewModel.getVideoList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getVideoList() }
    }

    @Test
    fun testGetVideoListErrorTimeout() {
        mockkObject(BaseViewModel)
        every { BaseViewModel.getLoadingTimeOut() } returns 0
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getVideoList() } returns result
        runBlocking {
            simpleListViewModel.getVideoList()
            Assert.assertTrue(simpleListViewModel.fileListLiveData.value is Result.Error)
        }
    }
}