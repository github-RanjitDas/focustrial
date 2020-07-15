package com.safefleet.lawmobile.helpers

import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject

class MockUtils {

    fun disconnectCamera() {
        val cameraHelperMock: CameraHelper = mockk {
            every { checkWithAlertIfTheCameraIsConnected() } returns false
        }

        //Assign CameraHelper Mock to CameraHelper object in runtime
        mockkObject(CameraHelper)
        every { CameraHelper.getInstance() } returns cameraHelperMock
    }

    fun restoreCameraConnection() {
        unmockkObject(CameraHelper)
    }

    fun clearSnapshotsOnX1() {
        CameraConnectServiceX1Mock.snapshotsList = CameraConnectFileResponseWithErrors()
        CameraConnectServiceX1Mock.takenPhotos = 0
    }

    fun restoreSnapshotsOnX1() {
        CameraConnectServiceX1Mock.snapshotsList =
            CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
    }

    fun clearVideosOnX1() {
        CameraConnectServiceX1Mock.videoList = CameraConnectFileResponseWithErrors()
        CameraConnectServiceX1Mock.takenVideos = 0
    }

    fun restoreVideosOnX1() {
        CameraConnectServiceX1Mock.videoList =
            CameraFilesData.DEFAULT_VIDEO_LIST.value
    }
}
