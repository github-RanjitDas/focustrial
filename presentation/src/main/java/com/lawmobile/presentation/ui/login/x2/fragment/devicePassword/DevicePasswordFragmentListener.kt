package com.lawmobile.presentation.ui.login.x2.fragment.devicePassword

interface DevicePasswordFragmentListener {
    var officerId: String
    fun onValidRequirements()
    fun onEditOfficerId(officerId: String)
}
