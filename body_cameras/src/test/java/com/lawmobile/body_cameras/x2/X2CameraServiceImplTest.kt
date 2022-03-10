package com.lawmobile.body_cameras.x2

import com.lawmobile.body_cameras.utils.CommandHelper
import com.lawmobile.body_cameras.utils.FileInformationHelper
import com.lawmobile.body_cameras.utils.MetadataHelper
import com.lawmobile.body_cameras.utils.NotificationCameraHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class X2CameraServiceImplTest {

    private val notificationCameraHelper: NotificationCameraHelper = mockk(relaxed = true)
    private val fileInformationHelper: FileInformationHelper = mockk(relaxed = true)
    private val commandHelper: CommandHelper = mockk(relaxed = true)
    private val metadataHelper: MetadataHelper = mockk(relaxed = true)
    private val x2CameraService = X2CameraServiceImpl(
        notificationCameraHelper,
        fileInformationHelper,
        commandHelper,
        metadataHelper
    )

    @Test
    fun getBodyWornDiagnosisFailCommand() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(Exception(""))
        runBlocking {
            val result = x2CameraService.getBodyWornDiagnosis()
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun getBodyWornDiagnosisFailedWithNotDiagnosisInLog() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.readInputStream() } returns ""
        runBlocking {
            val result = x2CameraService.getBodyWornDiagnosis()
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun getBodyWornDiagnosisSuccessWithDiagnosisFailed() {

        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.readInputStream() } returns "{'msg_id':7, 'type': 'self-test completed', 'param':'fail'}"
        runBlocking {
            val result = x2CameraService.getBodyWornDiagnosis()
            Assert.assertFalse((result as Result.Success).data)
        }
    }

    @Test
    fun getBodyWornDiagnosisSuccessWithDiagnosisPass() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.readInputStream() } returns "{'msg_id':7, 'type': 'self-test completed', 'param':'pass'}"
        runBlocking {
            val result = x2CameraService.getBodyWornDiagnosis()
            Assert.assertTrue((result as Result.Success).data)
        }
    }
}
