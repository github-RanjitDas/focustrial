package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist

class LiveViewScreen {

    fun isLiveViewNotDisplayed() = assertNotExist(R.id.buttonSwitchLiveView)

    fun isLiveViewDisplayed() = assertDisplayed(R.id.textLiveViewSwitch, R.string.live_view_label)


}
