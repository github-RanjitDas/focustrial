package net.safefleet.focus.screens.integration

import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import net.safefleet.focus.screens.LoginScreen

class LoginScreen : LoginScreen() {

    override fun login(officerPassword: String) {
        sleep(1500)
        super.login(officerPassword)
        sleep(1500)
    }
}
