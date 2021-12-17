package com.lawmobile.domain.usecase.bodyWornSettings

import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.repository.bodyWornSettings.BodyWornSettingsRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornSettingsUseCaseImpl(private val bodyWornSettingsRepository: BodyWornSettingsRepository) :
    BodyWornSettingsUseCase {

    override suspend fun changeStatusSettings(
        typesOfBodyWornSettings: TypesOfBodyWornSettings,
        isEnable: Boolean
    ): Result<Unit> {
        return bodyWornSettingsRepository.changeStatusSettings(typesOfBodyWornSettings, isEnable)
    }

    override suspend fun getParametersEnable(): Result<ParametersBodyWornSettings> {
        return bodyWornSettingsRepository.getParametersEnable()
    }
}
