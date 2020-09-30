package com.safefleet.lawmobile.tests

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
open class EspressoStartActivityBaseTest<T : Activity>(testActivityClass: Class<T>) :
    EspressoBaseTest() {

    @get:Rule
    var baristaRule = BaristaRule.create(testActivityClass)

    @get:Rule
    val activityRule = ActivityScenarioRule(testActivityClass)

    //Comment this @Before statement if you want to disable barista defaults
//    @Before
//    fun startActivity() {
//        baristaRule.launchActivity()
//    }

}