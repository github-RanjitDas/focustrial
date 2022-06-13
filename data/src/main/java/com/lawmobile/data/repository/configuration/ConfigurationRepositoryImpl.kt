package com.lawmobile.data.repository.configuration

import com.lawmobile.data.datasource.remote.configuration.ConfigurationRemoteDataSource
import com.lawmobile.data.mappers.impl.ConfigurationMapper.toDomain
import com.lawmobile.domain.entities.Config
import com.lawmobile.domain.repository.configuration.ConfigurationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ConfigurationRepositoryImpl(
    private val configurationRemoteDataSource: ConfigurationRemoteDataSource
) : ConfigurationRepository {
    override suspend fun getConfiguration(): Result<Config> =
        when (val result = configurationRemoteDataSource.getConfiguration()) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> result
        }
}
