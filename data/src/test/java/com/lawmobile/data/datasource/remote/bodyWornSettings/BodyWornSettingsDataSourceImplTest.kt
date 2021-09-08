package com.lawmobile.data.datasource.remote.bodyWornSettings

import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BodyWornSettingsDataSourceImplTest {

    private val bodyWornSettingsDataSourceImpl = BodyWornSettingsDataSourceImpl()

    @Test
    fun testChangeStatusSettingsCovertModeEnable() {
        // Change this test when with cameraService call
        runBlocking {
            val response = bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, true)
            Assert.assertTrue(response is Result.Success)
            Assert.assertTrue(BodyWornSettingsDataSourceImpl.isCovertModeEnable)
            bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, false)
            Assert.assertFalse(BodyWornSettingsDataSourceImpl.isCovertModeEnable)
        }
    }

    @Test
    fun testChangeStatusSettingsBluetoothEnable() {
        // Change this test when with cameraService call
        runBlocking {
            val response = bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, true)
            Assert.assertTrue(response is Result.Success)
            Assert.assertTrue(BodyWornSettingsDataSourceImpl.isBluetoothEnable)
            bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, false)
            Assert.assertFalse(BodyWornSettingsDataSourceImpl.isBluetoothEnable)
        }
    }

    @Test
    fun testChangeStatusSettingsGPSEnable() {
        // Change this test when with cameraService call
        runBlocking {
            val response = bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.GPS, true)
            Assert.assertTrue(response is Result.Success)
            Assert.assertTrue(BodyWornSettingsDataSourceImpl.isGPSEnable)
            bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.GPS, false)
            Assert.assertFalse(BodyWornSettingsDataSourceImpl.isGPSEnable)
        }
    }

    @Test
    fun testGetParametersEnable() {
        runBlocking {
            val response = bodyWornSettingsDataSourceImpl.getParametersEnable()
            Assert.assertTrue(response is Result.Success)
        }
    }
}
