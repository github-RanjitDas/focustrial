package com.lawmobile.presentation.ui.login.state

sealed class LoginState {
    sealed class X1 : LoginState() {
        object StartPairing : X1()
        object OfficerPassword : X1()
    }

    sealed class X2 : LoginState() {
        object OfficerId : X2()
        object DevicePassword : X2()
    }

    object PairingResult : LoginState()

    inline fun onStartPairing(callback: () -> Unit) {
        if (this is X1.StartPairing) callback()
    }

    inline fun onOfficerPassword(callback: () -> Unit) {
        if (this is X1.OfficerPassword) callback()
    }

    inline fun onPairingResult(callback: () -> Unit) {
        if (this is PairingResult) callback()
    }

    inline fun onOfficerId(callback: () -> Unit) {
        if (this is X2.OfficerId) callback()
    }

    inline fun onDevicePassword(callback: () -> Unit) {
        if (this is X2.DevicePassword) callback()
    }
}
