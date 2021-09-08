package com.lawmobile.data.repository.bodyWornSettings

import com.lawmobile.data.datasource.remote.bodyWornSettings.BodyWornSettingsDataSource
import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.repository.bodyWornSettings.BodyWornSettingsRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornSettingsRepositoryImpl(private val bodyWornSettingsDataSource: BodyWornSettingsDataSource) :
    BodyWornSettingsRepository {

    override suspend fun changeStatusSettings(
        typesOfBodyWornSettings: TypesOfBodyWornSettings,
        isEnable: Boolean
    ): Result<Unit> {
        return bodyWornSettingsDataSource.changeStatusSettings(typesOfBodyWornSettings, isEnable)
    }

    override suspend fun getParametersEnable(): Result<ParametersBodyWornSettings> {
        return bodyWornSettingsDataSource.getParametersEnable()
    }
}
