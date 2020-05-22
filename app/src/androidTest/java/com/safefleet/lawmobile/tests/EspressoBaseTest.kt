package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.lawmobile.presentation.ui.login.LoginActivity
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
open class EspressoBaseTest {
    @get:Rule
    var baristaRule = BaristaRule.create(LoginActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

    @Before
    fun startActivity() = baristaRule.launchActivity()

}
