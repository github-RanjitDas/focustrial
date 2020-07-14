package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaListInteractions

class LinkSnapshotsScreen : BaseScreen() {

    fun clickOnItemInPosition(position: Int) {
        BaristaListInteractions.clickListItem(R.id.linkSnapshotRecyclerView, position)
    }

    fun clickOnAdd() = clickOn(R.id.addButtonSnapshotLink)

    fun clickOnCancel() = clickOn(R.id.cancelButtonSnapshotLink)

}