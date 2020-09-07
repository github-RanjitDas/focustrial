package com.lawmobile.presentation.ui.simpleList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCase
import com.lawmobile.presentation.ui.base.BaseActivity.Companion.LOADING_TIMEOUT
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SimpleListViewModel @ViewModelInject constructor(private val simpleListUseCase: SimpleListUseCase) :
    BaseViewModel() {

    private val fileListMediator: MediatorLiveData<Result<DomainInformationFileResponse>> =
        MediatorLiveData()
    val fileListLiveData: LiveData<Result<DomainInformationFileResponse>> get() = fileListMediator

    fun getSnapshotList() {
        viewModelScope.launch {
            try {
                withTimeout(LOADING_TIMEOUT) {
                    fileListMediator.postValue(simpleListUseCase.getSnapshotList())
                }
            } catch (e: Exception) {
                fileListMediator.postValue(Result.Error(e))
            }
        }
    }

    fun getVideoList() {
        viewModelScope.launch {
            try {
                withTimeout(LOADING_TIMEOUT) {
                    fileListMediator.postValue(simpleListUseCase.getVideoList())
                }
            } catch (e: Exception) {
                fileListMediator.postValue(Result.Error(e))
            }
        }
    }
}