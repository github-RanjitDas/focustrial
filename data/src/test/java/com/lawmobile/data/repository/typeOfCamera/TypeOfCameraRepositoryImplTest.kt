package com.lawmobile.data.repository.typeOfCamera

import com.lawmobile.data.datasource.remote.typeOfCamera.TypeOfCameraRemoteDataSource
import com.safefleet.mobile.external_hardware.cameras.enums.CameraType
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.lawmobile.domain.enums.CameraType.X1 as X1
import com.lawmobile.domain.enums.CameraType.X2 as X2

internal class TypeOfCameraRepositoryImplTest {

    private val typeOfCameraRemoteDataSource: TypeOfCameraRemoteDataSource = mockk(relaxed = true)
    private val typeOfCameraRepositoryImpl: TypeOfCameraRepositoryImpl by lazy {
        TypeOfCameraRepositoryImpl(typeOfCameraRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetTypeOfCameraFlow() {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        runBlocking {
            typeOfCameraRepositoryImpl.getTypeOfCamera()
        }
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraSuccessX2() {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        runBlocking {
            val response = typeOfCameraRepositoryImpl.getTypeOfCamera()
            Assert.assertEquals((response as Result.Success).data, X2)
        }
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraSuccessX1() {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Success(CameraType.X1)
        runBlocking {
            val response = typeOfCameraRepositoryImpl.getTypeOfCamera()
            Assert.assertEquals((response as Result.Success).data, X1)
        }
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraError() {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Error(Exception(""))
        runBlocking {
            val response = typeOfCameraRepositoryImpl.getTypeOfCamera()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }
}
