package com.lawmobile.presentation.ui.login.model

sealed class LoginState {
    sealed class X1 : LoginState() {
        object SplashAnimation : X1()
        object StartPairing : X1()
        object OfficerPassword : X1()
    }

    object PairingResult : LoginState()

    inline fun onSplashAnimation(callback: () -> Unit, callback2: () -> Unit) {
        if (this is X1.SplashAnimation) callback()
        else callback2()
    }

    inline fun onStartPairing(callback: () -> Unit) {
        if (this is X1.StartPairing) callback()
    }

    inline fun onOfficerPassword(callback: () -> Unit) {
        if (this is X1.OfficerPassword) callback()
    }

    inline fun onPairingResult(callback: () -> Unit) {
        if (this is PairingResult) callback()
    }
}
