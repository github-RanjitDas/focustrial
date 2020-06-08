package com.safefleet.lawmobile.helpers

import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.lawmobile.testData.CameraFilesData
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
        CameraConnectServiceX1Mock.snapshotsList = mutableListOf()
    }

    fun restoreSnapshotsOnX1() {
        CameraConnectServiceX1Mock.snapshotsList =
            CameraFilesData.DEFAULT_SNAPSHOTS_LIST.value.toMutableList()
    }

}