package com.lawmobile.presentation.ui.simpleList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCase
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class SimpleListViewModel @ViewModelInject constructor(private val simpleListUseCase: SimpleListUseCase) :
    BaseViewModel() {

    private val fileListMediator: MediatorLiveData<Result<DomainInformationFileResponse>> =
        MediatorLiveData()
    val fileListLiveData: LiveData<Result<DomainInformationFileResponse>> get() = fileListMediator

    fun getSnapshotList() {
        viewModelScope.launch {
            fileListMediator.postValueWithTimeout(getLoadingTimeOut()) {
                simpleListUseCase.getSnapshotList()
            }
        }
    }

    fun getVideoList() {
        viewModelScope.launch {
            fileListMediator.postValueWithTimeout(getLoadingTimeOut()) {
                simpleListUseCase.getVideoList()
            }
        }
    }
}