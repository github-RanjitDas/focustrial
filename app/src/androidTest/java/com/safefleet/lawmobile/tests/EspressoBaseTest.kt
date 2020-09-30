package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.lawmobile.helpers.MockUtils
import com.safefleet.lawmobile.rules.EspressoIdlingResourceRule
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
open class EspressoBaseTest {

    companion object {
        val mockUtils = MockUtils()
    }

    @get:Rule
    val espressoIdlingResourcesRule = EspressoIdlingResourceRule()

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
