package com.safefleet.lawmobile.helpers

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceMock
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.testData.TestLoginData
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject

class MockUtils {

    companion object {
        var wifiEnabled = true
        var cameraSSID = TestLoginData.SSID_X1.value
        var bodyWornDiagnosisResult: Result<Boolean> = Result.Success(true)
        var progressBatteryCamera: Result<Int> = Result.Success(90)
        var totalStorageCamera = 60000000
        var freeStorageCamera = 50000000
        var cameraConnectServiceX1Mock = CameraConnectServiceMock()
    }

    fun disconnectCamera() {
        val cameraHelperMock: CameraHelper = mockk {
            every { checkWithAlertIfTheCameraIsConnected() } returns false
        }

        // Assign CameraHelper Mock to CameraHelper object in runtime
        mockkObject(CameraHelper)
        every { CameraHelper.getInstance() } returns cameraHelperMock

        CameraConnectServiceMock.result = Result.Error(Exception())
    }

    fun restoreCameraConnection() {
        unmockkObject(CameraHelper)
        CameraConnectServiceMock.result = Result.Success(100)
    }

    fun clearSnapshotsOnX1() {
        CameraConnectServiceMock.snapshotsList = FileResponseWithErrors()
        CameraConnectServiceMock.takenPhotos = 0
    }

    fun restoreSnapshotsOnX1() {
        CameraConnectServiceMock.snapshotsList =
            CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
    }

    fun clearVideosOnX1() {
        CameraConnectServiceMock.videoList = FileResponseWithErrors()
        CameraConnectServiceMock.takenVideos = 0
    }

    fun restoreVideosOnX1() {
        CameraConnectServiceMock.videoList =
            CameraFilesData.DEFAULT_VIDEO_LIST.value
    }

    fun turnWifiOff() {
//      This function only works when used before launching an app Activity
        wifiEnabled = false
    }

    fun turnWifiOn() {
        wifiEnabled = true
    }

    fun setCameraType(cameraType: CameraType) {
        if (cameraType == CameraType.X1) {
            cameraSSID = TestLoginData.SSID_X1.value
        } else {
            cameraSSID = TestLoginData.SSID_X2.value
        }
    }

    fun setBodyWornDiagnosisResult(result: Result<Boolean>) {
        bodyWornDiagnosisResult = result
    }

    fun setBatteryProgressCamera(progress: Int) {
        progressBatteryCamera = Result.Success(progress)
    }

    fun setBatteryProgressCameraX2(progress: Int) {
        val notification = NotificationResponse(
            "7",
            "battery_level",
            progress.toString()
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)
    }

    fun setStorageProgressCameraX2(percentage: Int) {
        val notification = NotificationResponse(
            "7",
            "storage_remain",
            percentage.toString()
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)
    }

    fun setStorageProgressCamera(totalStorage: Int, freeStorage: Int) {
        totalStorageCamera = totalStorage
        freeStorageCamera = freeStorage
    }
}
