package com.lawmobile.data.datasource.remote.bodyWornSettings

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BodyWornSettingsDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val bodyWornSettingsDataSourceImpl = BodyWornSettingsDataSourceImpl(cameraServiceFactory)

    @Test
    fun testChangeStatusSettingsCovertModeEnable() {
        coEvery { cameraService.startCovertMode() } returns Result.Success(Unit)
        coEvery { cameraService.stopCovertMode() } returns Result.Success(Unit)
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
        coEvery { cameraService.turnOnBluetooth() } returns Result.Success(Unit)
        coEvery { cameraService.turnOffBluetooth() } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, true)
            Assert.assertTrue(response is Result.Success)
            Assert.assertTrue(BodyWornSettingsDataSourceImpl.isBluetoothEnable)
            bodyWornSettingsDataSourceImpl.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, false)
            Assert.assertFalse(BodyWornSettingsDataSourceImpl.isBluetoothEnable)
        }
    }

    @Test
    fun testGetParametersEnable() {
        runBlocking {
            val response = bodyWornSettingsDataSourceImpl.getParametersEnable()
            Assert.assertTrue(response is Result.Error)
        }
    }
}
