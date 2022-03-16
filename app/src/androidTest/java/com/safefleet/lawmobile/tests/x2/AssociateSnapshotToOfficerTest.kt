package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.SnapshotDetailScreen
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AssociateSnapshotToOfficerTest : EspressoBaseTest() {

    private val snapshotDetailScreen = SnapshotDetailScreen()
    private val liveViewScreen = LiveViewScreen()
    private val fileListScreen = FileListScreen()
    private val loginScreen = LoginScreen()
    private val officer = "OfficerT"
    private val snapshotName = "11021800.JPG"

    @get:Rule
    var baristaRule = BaristaRule.create(LoginX2Activity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        loginScreen.loginWithoutSSO()
        FeatureSupportHelper.supportAssociateOfficerID = true
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2914
     */
    @Test
    fun linkSnapshotToPoliceOfficer() {
        with(snapshotDetailScreen) {
            liveViewScreen.openSnapshotList()
            fileListScreen.clickOnItemInPosition(1)
            isPhotoNameDisplayed(snapshotName)
            clickAssociateOfficer()
            typeOfficerIdToAssociate(officer)
            clickOnAssociateOfficerPopUp()
            isOfficerAssociateSuccessDisplayed()
            isOfficerNameDisplayed(officer)
            clickOnBack()
            fileListScreen.clickOnItemInPosition(1)
            isPhotoNameDisplayed(snapshotName)
            isOfficerNameDisplayed(officer)
        }
    }
}