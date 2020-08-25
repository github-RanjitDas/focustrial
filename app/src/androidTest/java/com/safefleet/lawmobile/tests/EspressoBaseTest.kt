package com.safefleet.lawmobile.tests

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.lawmobile.helpers.MockUtils
import com.safefleet.lawmobile.rules.EspressoIdlingResourceRule
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
open class EspressoBaseTest<T : Activity>(testActivityClass: Class<T>) {

    companion object {
        val mockUtils = MockUtils()
    }

    @get:Rule
//    var baristaRule = BaristaRule.create(testActivityClass)
    // Uncomment line below and comment the one above if you want to disable barista defaults
    val activityTestRule = ActivityTestRule(testActivityClass)

    @get:Rule
    var espressoIdlingResourcesRule = EspressoIdlingResourceRule()

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

    //Comment this @Before statement if you want to disable barista defaults
    /*@Before
    fun startActivity() = baristaRule.launchActivity()*/

    @After
    fun restoreData() {
        mockUtils.restoreCameraConnection()
        mockUtils.restoreSnapshotsOnX1()
        mockUtils.restoreVideosOnX1()
        CameraConnectServiceX1Mock.takenPhotos = 0
        CameraConnectServiceX1Mock.takenVideos = 0
    }
}
