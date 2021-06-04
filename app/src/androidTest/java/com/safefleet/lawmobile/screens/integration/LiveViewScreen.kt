package com.safefleet.lawmobile.screens.integration

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.interaction.BaristaClickInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep

class LiveViewScreen : LiveViewScreen() {

    override fun startRecording() {
        super.startRecording()
        sleep(1500)
    }

    override fun stopRecording() {
        super.stopRecording()
        sleep(1500)
    }

    override fun openSnapshotList() {
        super.openSnapshotList()
        sleep(1500)
    }

    override fun openVideoList() {
        super.openVideoList()
        sleep(1000)
    }

    override fun takeSnapshot() {
        super.takeSnapshot()
        sleep(1000)
    }

    fun takeSnapshotIntegrationTest() {
        waitUntil { BaristaClickInteractions.clickOn(R.id.buttonSnapshot) }
        waitUntil { BaristaVisibilityAssertions.assertNotExist(R.string.live_view_take_photo_success) }
        waitUntil { BaristaVisibilityAssertions.assertNotExist(R.string.live_view_take_photo_failed) }
        sleep(1000)
    }
}
