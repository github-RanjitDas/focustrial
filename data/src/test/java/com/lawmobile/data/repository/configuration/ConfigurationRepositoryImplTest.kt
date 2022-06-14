package com.lawmobile.data.repository.configuration

import com.lawmobile.body_cameras.entities.Config
import com.lawmobile.data.datasource.remote.configuration.ConfigurationRemoteDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class ConfigurationRepositoryImplTest {

    private val configurationRemoteDataSource: ConfigurationRemoteDataSource = mockk()

    private val configurationRepositoryImpl by lazy {
        ConfigurationRepositoryImpl(configurationRemoteDataSource)
    }

    @Test
    fun getConfiguration() {
        val config = Config(0)
        coEvery { configurationRemoteDataSource.getConfiguration() } returns Result.Success(config)
        runBlocking {
            val result =
                configurationRepositoryImpl.getConfiguration() as Result.Success
            Assert.assertEquals(result.data.encryption, config.encryption)
        }
        coVerify { configurationRemoteDataSource.getConfiguration() }
    }
}
