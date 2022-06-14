package com.lawmobile.data.datasource.remote.configuration

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class ConfigurationRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val configurationRemoteDataSourceImpl by lazy {
        ConfigurationRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun getConfiguration() {
        coEvery { cameraService.getConfiguration() } returns Result.Success(mockk())
        runBlocking {
            val result = configurationRemoteDataSourceImpl.getConfiguration()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getConfiguration() }
    }
}
