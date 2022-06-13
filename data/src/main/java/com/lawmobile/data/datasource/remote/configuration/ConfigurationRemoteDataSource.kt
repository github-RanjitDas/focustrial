package com.lawmobile.data.datasource.remote.configuration

import com.lawmobile.body_cameras.entities.Config
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ConfigurationRemoteDataSource {
    suspend fun getConfiguration(): Result<Config>
}
