package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.Suppress
import androidx.test.rule.GrantPermissionRule
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.lawmobile.helpers.MockUtils
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith


@LargeTest
@Suppress
@RunWith(AndroidJUnit4::class)
open class EspressoBaseTest {

    companion object {
        val mockUtils = MockUtils()
    }

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

    @After
    fun restoreData() {
        mockUtils.restoreCameraConnection()
        mockUtils.restoreSnapshotsOnX1()
        mockUtils.restoreVideosOnX1()
        mockUtils.turnWifiOn()
        CameraConnectServiceX1Mock.takenPhotos = 0
        CameraConnectServiceX1Mock.takenVideos = 0
    }
}
