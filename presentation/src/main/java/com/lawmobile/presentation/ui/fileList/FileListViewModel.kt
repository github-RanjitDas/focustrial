package com.lawmobile.presentation.ui.fileList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileListViewModel @Inject constructor(private val fileListUseCase: FileListUseCase) :
    BaseViewModel() {

    private val _associatePartnerIdResult: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val associatePartnerIdResult: LiveData<Result<Unit>> get() = _associatePartnerIdResult

    fun associatePartnerIdToVideoList(
        domainCameraFileList: List<DomainCameraFile>,
        partnerId: String
    ) {
        viewModelScope.launch {
            _associatePartnerIdResult.postValue(
                fileListUseCase.savePartnerIdVideos(
                    domainCameraFileList,
                    partnerId
                )
            )
        }
    }

    fun associatePartnerIdToSnapshotList(
        domainCameraFileList: List<DomainCameraFile>,
        partnerId: String
    ) {
        viewModelScope.launch {
            _associatePartnerIdResult.postValue(
                fileListUseCase.savePartnerIdSnapshot(
                    domainCameraFileList,
                    partnerId
                )
            )
        }
    }

    fun associatePartnerIdToAudioList(
        domainCameraFileList: List<DomainCameraFile>,
        partnerId: String
    ) {
        viewModelScope.launch {
            _associatePartnerIdResult.postValue(
                fileListUseCase.savePartnerIdAudios(
                    domainCameraFileList,
                    partnerId
                )
            )
        }
    }
}
