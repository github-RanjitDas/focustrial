package com.lawmobile.data.datasource.remote.typeOfCamera

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.enums.CameraType
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TypeOfCameraRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }

    private val typeOfCameraRemoteDataSourceImpl by lazy {
        TypeOfCameraRemoteDataSourceImpl(cameraServiceFactory)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
        every { cameraServiceFactory.create() } returns cameraService
    }

    @Test
    fun testGetTypeOfCameraFlow() {
        coEvery { cameraService.getCameraType() } returns Result.Success(CameraType.X2)
        runBlocking { typeOfCameraRemoteDataSourceImpl.getTypeOfCamera() }
        coVerify { cameraService.getCameraType() }
    }

    @Test
    fun testGetTypeOfCameraSuccessX2() {
        coEvery { cameraService.getCameraType() } returns Result.Success(CameraType.X2)
        runBlocking {
            val response = typeOfCameraRemoteDataSourceImpl.getTypeOfCamera()
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals((response as Result.Success).data, CameraType.X2)
        }
        coVerify { cameraService.getCameraType() }
    }

    @Test
    fun testGetTypeOfCameraSuccessX1() {
        coEvery { cameraService.getCameraType() } returns Result.Success(CameraType.X1)
        runBlocking {
            val response = typeOfCameraRemoteDataSourceImpl.getTypeOfCamera()
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals((response as Result.Success).data, CameraType.X1)
        }
        coVerify { cameraService.getCameraType() }
    }

    @Test
    fun testGetTypeOfCameraError() {
        coEvery { cameraService.getCameraType() } returns Result.Error(Exception(""))
        runBlocking {
            val response = typeOfCameraRemoteDataSourceImpl.getTypeOfCamera()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { cameraService.getCameraType() }
    }
}
