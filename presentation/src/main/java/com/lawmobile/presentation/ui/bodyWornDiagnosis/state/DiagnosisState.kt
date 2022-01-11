package com.lawmobile.presentation.ui.bodyWornDiagnosis.state

sealed class DiagnosisState {

    object Start : DiagnosisState()
    object Progress : DiagnosisState()
    data class Finished(val isSuccess: Boolean) : DiagnosisState()

    inline fun onStartDiagnosis(callback: () -> Unit) {
        if (this is Start) callback()
    }

    inline fun onProgressDiagnosis(callback: () -> Unit) {
        if (this is Progress) callback()
    }

    inline fun onFinishedDiagnosis(callback: (Boolean) -> Unit) {
        if (this is Finished) callback(isSuccess)
    }
}
