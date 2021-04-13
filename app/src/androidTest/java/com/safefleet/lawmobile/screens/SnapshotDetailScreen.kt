package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class SnapshotDetailScreen : BaseScreen() {

    fun goBack() = clickOn(R.id.imageButtonBackArrow)
}
