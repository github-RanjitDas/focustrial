package com.lawmobile.domain.usecase.getCameraConfig

import com.lawmobile.domain.entities.Config
import com.lawmobile.domain.repository.configuration.ConfigurationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class GetCameraConfigImpl(
    private val configurationRepository: ConfigurationRepository
) : GetCameraConfig {
    override suspend fun invoke(): Result<Config> = configurationRepository.getConfiguration()
}
