package com.safefleet.lawmobile.helpers

import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager

class SimpleTestNetworkManager : ListenableNetworkManager() {

    override fun isInternetOn(): Boolean {
        return true
    }
}
