package com.lawmobile.domain.usecase.bodyWornSettings

import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface BodyWornSettingsUseCase : BaseUseCase {
    suspend fun changeStatusSettings(typesOfBodyWornSettings: TypesOfBodyWornSettings, isEnable: Boolean): Result<Unit>
    suspend fun getParametersEnable(): Result<ParametersBodyWornSettings>
}
