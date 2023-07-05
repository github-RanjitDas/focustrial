package net.safefleet.focus.screens.integration

import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import net.safefleet.focus.screens.FileListScreen

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
