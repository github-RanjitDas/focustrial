package com.lawmobile.data.datasource.remote.events

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class EventsRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val notificationRemoteDataSourceImpl by lazy {
        EventsRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun getLogEventsFlow() {
        coEvery { cameraService.getLogEvents() } returns mockk()
        runBlocking { notificationRemoteDataSourceImpl.getCameraEvents() }
        coVerify { cameraService.getLogEvents() }
    }

    @Test
    fun getLogEventsSuccess() {
        val result: Result<List<LogEvent>> = Result.Success(mockk())
        coEvery { cameraService.getLogEvents() } returns result
        runBlocking {
            Assert.assertEquals(
                result,
                notificationRemoteDataSourceImpl.getCameraEvents()
            )
        }
    }

    @Test
    fun getLogEventsError() {
        val result: Result<List<LogEvent>> = Result.Error(mockk())
        coEvery { cameraService.getLogEvents() } returns result
        runBlocking {
            Assert.assertEquals(
                result,
                notificationRemoteDataSourceImpl.getCameraEvents()
            )
        }
    }

    @Test
    fun isPossibleToReadLogFlow() {
        every { cameraService.getCanReadNotification() } returns true
        notificationRemoteDataSourceImpl.isPossibleToReadLog()
        verify { cameraService.getCanReadNotification() }
    }

    @Test
    fun isPossibleToReadLogTrue() {
        every { cameraService.getCanReadNotification() } returns true
        val isPossibleToRead = notificationRemoteDataSourceImpl.isPossibleToReadLog()
        Assert.assertTrue(isPossibleToRead)
    }

    @Test
    fun isPossibleToReadLogFalse() {
        every { cameraService.getCanReadNotification() } returns false
        val isPossibleToRead = notificationRemoteDataSourceImpl.isPossibleToReadLog()
        Assert.assertFalse(isPossibleToRead)
    }
}
