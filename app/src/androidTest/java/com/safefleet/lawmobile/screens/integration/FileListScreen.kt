package com.safefleet.lawmobile.screens.integration

import com.safefleet.lawmobile.screens.FileListScreen
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep

class FileListScreen : FileListScreen() {

    override fun clickOnSelectFilesToAssociate() {
        super.clickOnSelectFilesToAssociate()
        sleep(2000)
    }

    override fun typeOfficerIdToAssociate(officerId: String) {
        super.typeOfficerIdToAssociate(officerId)
        sleep(1000)
    }
}
