package com.lawmobile.presentation.ui.bodyWornDiagnosis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.bodyWornDiagnosis.BodyWornDiagnosisUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyWornDiagnosisViewModel @Inject constructor(private val bodyWornDiagnosisUseCase: BodyWornDiagnosisUseCase) :
    BaseViewModel() {

    private val diagnosisCameraMediator: MediatorLiveData<Result<Boolean>> = MediatorLiveData()
    val diagnosisCameraLiveData: LiveData<Result<Boolean>> get() = diagnosisCameraMediator

    fun getDiagnosis() {
        viewModelScope.launch {
            diagnosisCameraMediator.postValue(bodyWornDiagnosisUseCase.isDiagnosisSuccess())
        }
    }
}
