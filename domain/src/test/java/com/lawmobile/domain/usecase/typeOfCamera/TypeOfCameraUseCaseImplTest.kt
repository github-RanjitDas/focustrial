package com.lawmobile.domain.usecase.typeOfCamera

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.repository.typeOfCamera.TypeOfCameraRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TypeOfCameraUseCaseImplTest {

    private val typeOfCameraRepository: TypeOfCameraRepository = mockk()
    private val typeOfCameraUseCase by lazy { TypeOfCameraUseCaseImpl(typeOfCameraRepository) }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetTypeOfCameraFlow() {
        coEvery { typeOfCameraRepository.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        runBlocking { typeOfCameraUseCase.getTypeOfCamera() }
        coVerify { typeOfCameraRepository.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraSuccessX2() {
        coEvery { typeOfCameraRepository.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        runBlocking {
            val response = typeOfCameraUseCase.getTypeOfCamera()
            Assert.assertEquals((response as Result.Success).data, CameraType.X2)
        }
        coVerify { typeOfCameraRepository.getTypeOfCamera() }
    }

    @Test
    fun testGetTypeOfCameraError() {
        coEvery { typeOfCameraRepository.getTypeOfCamera() } returns Result.Error(Exception(""))
        runBlocking {
            val response = typeOfCameraUseCase.getTypeOfCamera()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { typeOfCameraRepository.getTypeOfCamera() }
    }
}
