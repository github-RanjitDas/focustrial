package com.safefleet.lawmobile.tests

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
open class EspressoBaseTest<T : Activity>(testActivityClass: Class<T>) {

    @get:Rule
    var baristaRule = BaristaRule.create(testActivityClass)
    // Uncomment line below and comment the one above if you want to disable barista defaults
    //var activityTestRule = ActivityTestRule(testActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

    //Comment this @Before statement if you want to disable barista defaults
    @Before
    fun startActivity() = baristaRule.launchActivity()

}
