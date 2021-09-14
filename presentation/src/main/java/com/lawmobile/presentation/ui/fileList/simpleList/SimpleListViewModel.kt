package com.lawmobile.presentation.ui.fileList.simpleList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCase
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimpleListViewModel @Inject constructor(private val simpleListUseCase: SimpleListUseCase) :
    BaseViewModel() {

    private val _fileListResult = MediatorLiveData<Result<DomainInformationFileResponse>>()
    val fileListResult: LiveData<Result<DomainInformationFileResponse>> get() = _fileListResult

    fun getSnapshotList() {
        viewModelScope.launch {
            _fileListResult.postValueWithTimeout(getLoadingTimeOut()) {
                simpleListUseCase.getSnapshotList()
            }
        }
    }

    fun getVideoList() {
        viewModelScope.launch {
            _fileListResult.postValueWithTimeout(getLoadingTimeOut()) {
                simpleListUseCase.getVideoList()
            }
        }
    }

    fun getAudioList() {
        viewModelScope.launch {
            _fileListResult.postValueWithTimeout(getLoadingTimeOut()) {
                simpleListUseCase.getAudioList()
            }
        }
    }
}
