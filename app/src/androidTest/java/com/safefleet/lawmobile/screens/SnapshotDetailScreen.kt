package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class SnapshotDetailScreen : BaseScreen() {

    fun goBack() = waitUntil { clickOn(R.id.imageButtonBackArrow) }
}
