package com.safefleet.lawmobile.screens.integration

import com.safefleet.lawmobile.screens.LoginScreen
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep

class LoginScreen : LoginScreen() {

    override fun login(officerPassword: String) {
        sleep(1500)
        super.login(officerPassword)
        sleep(1500)
    }
}
