package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.x2

interface StartPairingX2FragmentListener {
    var officerId: String
    fun onValidRequirements()
    fun onEditOfficerId(officerId: String)
}
