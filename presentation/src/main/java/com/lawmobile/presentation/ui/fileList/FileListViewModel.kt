package com.lawmobile.presentation.ui.fileList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class FileListViewModel @ViewModelInject constructor(private val fileListUseCase: FileListUseCase) :
    BaseViewModel() {

    private val videoPartnerIdMediator: MediatorLiveData<Result<Unit>> =
        MediatorLiveData()
    val videoPartnerIdLiveData: LiveData<Result<Unit>> get() = videoPartnerIdMediator

    private val snapshotPartnerIdMediator: MediatorLiveData<Result<Unit>> =
        MediatorLiveData()
    val snapshotPartnerIdLiveData: LiveData<Result<Unit>> get() = snapshotPartnerIdMediator

    fun associatePartnerIdToVideoList(
        domainCameraFileList: List<DomainCameraFile>,
        partnerId: String
    ) {
        viewModelScope.launch {
            videoPartnerIdMediator.postValue(
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
            snapshotPartnerIdMediator.postValue(
                fileListUseCase.savePartnerIdSnapshot(
                    domainCameraFileList,
                    partnerId
                )
            )
        }
    }
}