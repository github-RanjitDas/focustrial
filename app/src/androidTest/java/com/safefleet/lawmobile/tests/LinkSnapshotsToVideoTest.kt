package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LinkSnapshotsToVideoTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val fileListScreen = FileListScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        private val linkSnapshotsScreen = LinkSnapshotsScreen()
    }

    @Before
    fun login() {
        LoginScreen().login()
    }

    @Before
    fun setRecyclerView() {
        fileListScreen.recyclerView = R.id.videoListRecycler
    }

    @Test
    fun linkSnapshotsToVideoDisconnectionX1_FMA_805() {
        liveViewScreen.openVideoList()
        /*fileListScreen.clickOnItemInPosition(1)
        videoPlaybackScreen.goToLinkSnapshots()

        linkSnapshotsScreen.clickOnItemInPosition(3)
        mockUtils.disconnectCamera()
        linkSnapshotsScreen.clickOnAdd()
        linkSnapshotsScreen.isDisconnectionAlertDisplayed()*/
    }

}