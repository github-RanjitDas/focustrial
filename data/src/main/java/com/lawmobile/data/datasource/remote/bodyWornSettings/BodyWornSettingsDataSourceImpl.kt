package com.lawmobile.data.datasource.remote.bodyWornSettings

import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornSettingsDataSourceImpl : BodyWornSettingsDataSource {

    override suspend fun changeStatusSettings(typesOfBodyWornSettings: TypesOfBodyWornSettings, isEnable: Boolean): Result<Unit> {
        return when (typesOfBodyWornSettings) {
            TypesOfBodyWornSettings.CovertMode -> changeCovertModeEnable(isEnable)
            TypesOfBodyWornSettings.Bluetooth -> changeBluetoothEnable(isEnable)
            TypesOfBodyWornSettings.GPS -> changeGPSEnable(isEnable)
        }
    }

    override suspend fun getParametersEnable(): Result<ParametersBodyWornSettings> {
        // Change this with cameraService
        return Result.Success(
            ParametersBodyWornSettings(
                isCovertModeEnable,
                isBluetoothEnable,
                isGPSEnable
            )
        )
    }

    private fun changeCovertModeEnable(isEnable: Boolean): Result<Unit> {
        // Change this with cameraService
        isCovertModeEnable = isEnable
        return Result.Success(Unit)
    }

    private fun changeBluetoothEnable(isEnable: Boolean): Result<Unit> {
        // Change this with cameraService
        isBluetoothEnable = isEnable
        return Result.Success(Unit)
    }

    private fun changeGPSEnable(isEnable: Boolean): Result<Unit> {
        // Change this with cameraService
        isGPSEnable = isEnable
        return Result.Success(Unit)
    }

    companion object {
        var isCovertModeEnable: Boolean = false
        var isBluetoothEnable: Boolean = false
        var isGPSEnable: Boolean = false
    }
}
