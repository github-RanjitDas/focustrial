package com.lawmobile.data.repository.notification

import com.lawmobile.data.datasource.remote.notification.NotificationRemoteDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class NotificationRepositoryImplTest {

    private val notificationRemoteDataSource: NotificationRemoteDataSource = mockk()

    private val notificationRepositoryImpl: NotificationRepositoryImpl by lazy {
        NotificationRepositoryImpl(notificationRemoteDataSource)
    }

    @Test
    fun getLogEventsFlow() {
        coEvery { notificationRemoteDataSource.getLogEvents() } returns Result.Success(mockk(relaxed = true))
        runBlocking { notificationRepositoryImpl.getLogEvents() }
        coVerify { notificationRemoteDataSource.getLogEvents() }
    }

    @Test
    fun getLogEventsSuccess() {
        coEvery { notificationRemoteDataSource.getLogEvents() } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            Assert.assertTrue(
                notificationRepositoryImpl.getLogEvents() is Result.Success
            )
        }
    }

    @Test
    fun getLogEventsError() {
        coEvery { notificationRemoteDataSource.getLogEvents() } returns Result.Error(mockk(relaxed = true))
        runBlocking {
            Assert.assertTrue(
                notificationRepositoryImpl.getLogEvents() is Result.Error
            )
        }
    }

    @Test
    fun isPossibleToReadLogFlow() {
        every { notificationRemoteDataSource.isPossibleToReadLog() } returns true
        notificationRepositoryImpl.isPossibleToReadLog()
        verify { notificationRemoteDataSource.isPossibleToReadLog() }
    }

    @Test
    fun isPossibleToReadLogTrue() {
        every { notificationRemoteDataSource.isPossibleToReadLog() } returns true
        Assert.assertTrue(notificationRepositoryImpl.isPossibleToReadLog())
    }

    @Test
    fun isPossibleToReadLogFalse() {
        every { notificationRemoteDataSource.isPossibleToReadLog() } returns false
        Assert.assertFalse(notificationRepositoryImpl.isPossibleToReadLog())
    }
}
