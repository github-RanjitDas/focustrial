package com.lawmobile.presentation.ui.bodyWornDiagnosis

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.bodyWornDiagnosis.BodyWornDiagnosisUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.bodyWornDiagnosis.state.DiagnosisState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyWornDiagnosisViewModel @Inject constructor(
    private val bodyWornDiagnosisUseCase: BodyWornDiagnosisUseCase
) : BaseViewModel() {

    private val _diagnosisResult = MutableSharedFlow<Result<Boolean>>()
    val diagnosisResult = _diagnosisResult.asSharedFlow()

    val diagnosisState: StateFlow<DiagnosisState> get() = _diagnosisState
    private val _diagnosisState by lazy { MutableStateFlow<DiagnosisState>(DiagnosisState.Start) }

    fun setDiagnosisState(state: DiagnosisState) {
        _diagnosisState.value = state
    }

    fun getDiagnosisState(): DiagnosisState = _diagnosisState.value

    fun getDiagnosis() {
        viewModelScope.launch {
            val result = getResultWithAttempts(ATTEMPTS_TO_DIAGNOSE) {
                bodyWornDiagnosisUseCase.isDiagnosisSuccess()
            }
            _diagnosisResult.emit(result)
        }
    }

    companion object {
        private const val ATTEMPTS_TO_DIAGNOSE = 3
    }
}
