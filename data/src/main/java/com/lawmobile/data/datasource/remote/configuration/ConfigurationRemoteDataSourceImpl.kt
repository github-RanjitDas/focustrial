package com.lawmobile.data.datasource.remote.configuration

import com.lawmobile.body_cameras.entities.Config
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ConfigurationRemoteDataSourceImpl(
    private val bodyCameraFactory: CameraServiceFactory
) : ConfigurationRemoteDataSource {
    private val bodyCamera by lazy { bodyCameraFactory.create() }

    override suspend fun getConfiguration(): Result<Config> =
        bodyCamera.getConfiguration()
}
