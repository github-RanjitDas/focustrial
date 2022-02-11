package com.lawmobile.presentation.utils

import com.lawmobile.domain.utils.Logger
import com.newrelic.agent.android.NewRelic

object NewRelicLogger : Logger {
    private const val LOW_WIFI_SIGNAL = "Low Wi-Fi Signal"
    private const val WIFI_STATUS = "Wi-Fi Status"
    private const val VIEW_NAME = "View Name"
    private const val PARENT_NAME = "Parent Name"

    fun updateLowWifiSignal(low: Boolean) {
        NewRelic.setAttribute(LOW_WIFI_SIGNAL, low)
    }

    fun updateWifiStatus(active: Boolean) {
        NewRelic.setAttribute(WIFI_STATUS, active)
    }

    fun updateActiveView(viewName: String) {
        NewRelic.setAttribute(VIEW_NAME, viewName)
    }

    fun updateActiveParent(parentName: String) {
        NewRelic.setAttribute(PARENT_NAME, parentName)
    }

    fun setUserId(name: String) {
        NewRelic.setUserId(name)
    }
}
