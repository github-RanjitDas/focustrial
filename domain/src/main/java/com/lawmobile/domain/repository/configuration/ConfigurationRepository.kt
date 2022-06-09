package com.lawmobile.domain.repository.configuration

import com.lawmobile.domain.entities.Config
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ConfigurationRepository {
    suspend fun getConfiguration(): Result<Config>
}
