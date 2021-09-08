package com.lawmobile.domain.repository.bodyWornSettings

import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface BodyWornSettingsRepository : BaseRepository {
    suspend fun changeStatusSettings(typesOfBodyWornSettings: TypesOfBodyWornSettings, isEnable: Boolean): Result<Unit>
    suspend fun getParametersEnable(): Result<ParametersBodyWornSettings>
}
