package com.lawmobile.domain.usecase.getCameraConfig

import com.lawmobile.domain.repository.configuration.ConfigurationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class GetCameraConfigImplTest {

    private val configurationRepository: ConfigurationRepository = mockk {
        coEvery { getConfiguration() } returns Result.Success(mockk())
    }

    private val getCameraConfigImpl: GetCameraConfigImpl by lazy {
        GetCameraConfigImpl(configurationRepository)
    }

    @Test
    fun invoke() {
        coEvery { configurationRepository.getConfiguration() } returns Result.Success(mockk())
        runBlocking {
            val result = getCameraConfigImpl.invoke()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { configurationRepository.getConfiguration() }
    }
}
