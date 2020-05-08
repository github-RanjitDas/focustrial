package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.lawmobile.data.InstantExecutorExtension
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSourceImpl.Companion.DEFAULT_SERIAL_NUMBER
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSourceImpl.Companion.ERROR_DOES_NOT_SSID_SAVED
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSourceImpl.Companion.LAST_SERIAL_NUMBER_TO_CONNECTION
import com.safefleet.mobile.avml.cameras.external.CameraDataSource
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class PairingPhoneWithCameraRemoteDataSourceImplTest {

    private val progressCamera: LiveData<Result<Int>> = mockk()
    private val cameraDataSource: CameraDataSource = mockk {
        every { progressPairingCamera } returns progressCamera
    }

    private val editor: SharedPreferences.Editor = mockk()
    private val preferences: SharedPreferences = mockk {
        every { edit() } returns editor
    }

    private val pairingPhoneWithCameraRemoteDataSourceImpl by lazy {
        PairingPhoneWithCameraRemoteDataSourceImpl(preferences, cameraDataSource)
    }

    @Test
    fun testLoadPairingCamera() {
        coEvery { cameraDataSource.loadPairingCamera(any(), any()) } just Runs
        runBlocking {
            pairingPhoneWithCameraRemoteDataSourceImpl.loadPairingCamera("", "")
        }

        Assert.assertEquals(
            pairingPhoneWithCameraRemoteDataSourceImpl.progressPairingCamera,
            progressCamera
        )

        coVerify { cameraDataSource.loadPairingCamera("", "") }
    }

    @Test
    fun testGetSSIDSavedIfExistSuccess() {
        every {
            preferences.getString(LAST_SERIAL_NUMBER_TO_CONNECTION, DEFAULT_SERIAL_NUMBER)
        } returns "57014964"

        val serialNumber = pairingPhoneWithCameraRemoteDataSourceImpl.getSSIDSavedIfExist()
        Assert.assertEquals(serialNumber, Result.Success("57014964"))

    }

    @Test
    fun testGetSSIDSavedIfExistFailed() {
        every {
            preferences.getString(LAST_SERIAL_NUMBER_TO_CONNECTION, DEFAULT_SERIAL_NUMBER)
        } returns DEFAULT_SERIAL_NUMBER

        val error = pairingPhoneWithCameraRemoteDataSourceImpl.getSSIDSavedIfExist() as Result.Error
        Assert.assertEquals(error.exception.message, ERROR_DOES_NOT_SSID_SAVED)
    }

    @Test
    fun testGetSSIDSavedIfExistNull() {
        every {
            preferences.getString(LAST_SERIAL_NUMBER_TO_CONNECTION, DEFAULT_SERIAL_NUMBER)
        } returns null

        val error = pairingPhoneWithCameraRemoteDataSourceImpl.getSSIDSavedIfExist() as Result.Error
        Assert.assertEquals(error.exception.message, ERROR_DOES_NOT_SSID_SAVED)
    }

    @Test
    fun testSaveSerialNumberOfCamera() {
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } just Runs

        pairingPhoneWithCameraRemoteDataSourceImpl.saveSerialNumberOfCamera("123456789")
        verify { editor.putString(LAST_SERIAL_NUMBER_TO_CONNECTION, "123456789") }
        verify { editor.apply() }
    }
}