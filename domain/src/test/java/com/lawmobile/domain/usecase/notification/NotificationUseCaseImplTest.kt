package com.lawmobile.domain.usecase.notification

import com.lawmobile.domain.repository.notification.NotificationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class NotificationUseCaseImplTest {

    private val notificationRepository: NotificationRepository = mockk()

    private val notificationUseCaseImpl: NotificationUseCaseImpl by lazy {
        NotificationUseCaseImpl(notificationRepository)
    }

    @Test
    fun getLogEventsFlow() {
        coEvery { notificationRepository.getLogEvents() } returns mockk()
        runBlocking { notificationUseCaseImpl.getLogEvents() }
        coVerify { notificationRepository.getLogEvents() }
    }

    @Test
    fun getLogEventsSuccess() {
        coEvery { notificationRepository.getLogEvents() } returns Result.Success(mockk())
        runBlocking {
            Assert.assertTrue(notificationUseCaseImpl.getLogEvents() is Result.Success)
        }
    }

    @Test
    fun getLogEventsError() {
        coEvery { notificationRepository.getLogEvents() } returns Result.Error(mockk())
        runBlocking {
            Assert.assertTrue(notificationUseCaseImpl.getLogEvents() is Result.Error)
        }
    }

    @Test
    fun isPossibleToReadLogFlow() {
        every { notificationRepository.isPossibleToReadLog() } returns true
        notificationUseCaseImpl.isPossibleToReadLog()
        verify { notificationRepository.isPossibleToReadLog() }
    }

    @Test
    fun isPossibleToReadLogTrue() {
        every { notificationRepository.isPossibleToReadLog() } returns true
        Assert.assertTrue(notificationUseCaseImpl.isPossibleToReadLog())
    }

    @Test
    fun isPossibleToReadLogFalse() {
        every { notificationRepository.isPossibleToReadLog() } returns false
        Assert.assertFalse(notificationUseCaseImpl.isPossibleToReadLog())
    }
}
