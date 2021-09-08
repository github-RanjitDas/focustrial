package com.lawmobile.data.datasource.remote.bodyWornSettings

import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface BodyWornSettingsDataSource {
    suspend fun changeStatusSettings(typesOfBodyWornSettings: TypesOfBodyWornSettings, isEnable: Boolean): Result<Unit>
    suspend fun getParametersEnable(): Result<ParametersBodyWornSettings>
}
