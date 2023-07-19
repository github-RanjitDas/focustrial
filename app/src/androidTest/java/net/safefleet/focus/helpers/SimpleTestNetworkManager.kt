package net.safefleet.focus.helpers

import com.safefleet.mobile.kotlin_commons.helpers.network_manager.ListenableNetworkManager

class SimpleTestNetworkManager : ListenableNetworkManager() {

    override fun isInternetOn(): Boolean {
        return true
    }
}