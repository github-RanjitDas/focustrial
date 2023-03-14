package com.lawmobile.data.datasource.remote.bodyWornSettings

import com.lawmobile.body_cameras.enums.XCameraCommandCodes
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornSettingsDataSourceImpl(private val bodyCameraFactory: CameraServiceFactory) :
    BodyWornSettingsDataSource {

    private val bodyCamera by lazy {
        bodyCameraFactory.create()
    }

    override suspend fun changeStatusSettings(
        typesOfBodyWornSettings: TypesOfBodyWornSettings,
        isEnable: Boolean
    ): Result<Unit> {
        return when (typesOfBodyWornSettings) {
            TypesOfBodyWornSettings.CovertMode -> changeCovertModeEnable(isEnable)
            TypesOfBodyWornSettings.Bluetooth -> changeBluetoothEnable(isEnable)
        }
    }

    override suspend fun getParametersEnable(): Result<ParametersBodyWornSettings> {
        val resultIsCovertModeEnable =
            bodyCamera.getCameraSettings(XCameraCommandCodes.GET_COVERT_MODE_SETTING.commandValue)
        val resultIsBluetoothEnable =
            bodyCamera.getCameraSettings(XCameraCommandCodes.GET_BLUETOOTH_SETTING.commandValue)
        val resultIsGpsEnable =
            bodyCamera.getCameraSettings(XCameraCommandCodes.GET_GPS_SETTING.commandValue)
        val parametersBodyWornSettings = ParametersBodyWornSettings(
            isSettingEnable(resultIsCovertModeEnable),
            isSettingEnable(resultIsBluetoothEnable),
            isSettingEnable(resultIsGpsEnable)
        )
        return Result.Success(parametersBodyWornSettings)
    }

    private fun isSettingEnable(resultSettings: Result<Int>): Boolean {
        resultSettings.doIfSuccess {
            if (it == 1) {
                return true
            }
        }
        return false
    }

    private suspend fun changeCovertModeEnable(isEnable: Boolean): Result<Unit> {
        val result = if (isEnable) bodyCamera.startCovertMode()
        else bodyCamera.stopCovertMode()
        result.doIfSuccess { isCovertModeEnable = isEnable }
        return result
    }

    private suspend fun changeBluetoothEnable(isEnable: Boolean): Result<Unit> {
        val result = if (isEnable) bodyCamera.turnOnBluetooth()
        else bodyCamera.turnOffBluetooth()
        result.doIfSuccess { isBluetoothEnable = isEnable }
        return result
    }

    companion object {
        var isCovertModeEnable: Boolean = false
        var isBluetoothEnable: Boolean = false
        var isGPSEnable: Boolean = false
    }
}
