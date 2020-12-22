package com.safefleet.lawmobile.tests

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.Suppress
import com.safefleet.lawmobile.rules.EspressoIdlingResourceRule
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@Suppress
@RunWith(AndroidJUnit4::class)
open class EspressoStartActivityBaseTest<T : Activity>(testActivityClass: Class<T>) :
    EspressoBaseTest() {

    @Rule
    @JvmField
    val espressoIdlingResourcesRule = EspressoIdlingResourceRule()

    //@Rule
    //@JvmField
    //var baristaRule = BaristaRule.create(testActivityClass)

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(testActivityClass)

    //Comment this @Before statement if you want to disable barista defaults
    /*@Before
    fun startActivity() {
        baristaRule.launchActivity()
    }*/

}