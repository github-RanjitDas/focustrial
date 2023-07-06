package com.lawmobile.data.repository.typeOfCamera

import com.lawmobile.body_cameras.enums.CameraType
import com.lawmobile.data.datasource.remote.typeOfCamera.TypeOfCameraRemoteDataSource
import com.lawmobile.domain.enums.CameraType.X2
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class TypeOfCameraRepositoryImplTest {

    private val typeOfCameraRemoteDataSource: TypeOfCameraRemoteDataSource = mockk(relaxed = true)
    private val dispatcher = TestCoroutineDispatcher()

    private val typeOfCameraRepositoryImpl: TypeOfCameraRepositoryImpl by lazy {
        TypeOfCameraRepositoryImpl(typeOfCameraRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @Test
    fun testGetTypeOfCameraFlow() = runBlockingTest {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        typeOfCameraRepositoryImpl.getTypeOfCamera()
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraSuccessX2() = runBlockingTest {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        val response = typeOfCameraRepositoryImpl.getTypeOfCamera()
        Assert.assertEquals((response as Result.Success).data, X2)
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraError() = runBlockingTest {
        coEvery { typeOfCameraRemoteDataSource.getTypeOfCamera() } returns Result.Error(Exception(""))
        val response = typeOfCameraRepositoryImpl.getTypeOfCamera()
        Assert.assertTrue(response is Result.Error)
        coVerify { typeOfCameraRemoteDataSource.getTypeOfCamera() }
    }
}
