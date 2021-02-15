package com.lawmobile.domain.usecase.simpleList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.repository.simpleList.SimpleListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class SimpleListUseCaseImplTest {

    private val simpleListRepository: SimpleListRepository = mockk()

    private val simpleListUseCaseImpl: SimpleListUseCaseImpl by lazy {
        SimpleListUseCaseImpl(simpleListRepository)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { simpleListRepository.getSnapshotList() } returns Result.Success(mockk())
        runBlocking {
            simpleListUseCaseImpl.getSnapshotList()
        }
        coVerify { simpleListRepository.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val domainInformation: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformation)
        coEvery { simpleListRepository.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(simpleListUseCaseImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { simpleListRepository.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(simpleListUseCaseImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        coEvery { simpleListRepository.getVideoList() } returns Result.Success(mockk())
        runBlocking {
            simpleListUseCaseImpl.getVideoList()
        }
        coVerify { simpleListRepository.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        val domainInformation: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformation)
        coEvery { simpleListRepository.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(simpleListUseCaseImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { simpleListRepository.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(simpleListUseCaseImpl.getVideoList(), result)
        }
    }
}
