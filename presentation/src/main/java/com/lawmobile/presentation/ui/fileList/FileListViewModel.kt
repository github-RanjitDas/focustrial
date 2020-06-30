package com.lawmobile.presentation.ui.fileList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class FileListViewModel @Inject constructor(private val fileListUseCase: FileListUseCase) :
    BaseViewModel() {

    private val snapshotListMediator: MediatorLiveData<Result<DomainInformationFileResponse>> =
        MediatorLiveData()
    val snapshotListLiveData: LiveData<Result<DomainInformationFileResponse>> get() = snapshotListMediator

    private val videoListMediator: MediatorLiveData<Result<DomainInformationFileResponse>> =
        MediatorLiveData()
    val videoListLiveData: LiveData<Result<DomainInformationFileResponse>> get() = videoListMediator

    private val videoPartnerIdMediator: MediatorLiveData<Result<Unit>> =
        MediatorLiveData()
    val videoPartnerIdLiveData: LiveData<Result<Unit>> get() = videoPartnerIdMediator

    private val snapshotPartnerIdMediator: MediatorLiveData<Result<Unit>> =
        MediatorLiveData()
    val snapshotPartnerIdLiveData: LiveData<Result<Unit>> get() = snapshotPartnerIdMediator

    fun getSnapshotList() {
        viewModelScope.launch {
            snapshotListMediator.postValue(fileListUseCase.getSnapshotList())
        }
    }

    fun getVideoList() {
        viewModelScope.launch {
            videoListMediator.postValue(fileListUseCase.getVideoList())
        }
    }

    fun associatePartnerIdToVideoList(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerId: String
    ) {
        viewModelScope.launch {
            videoPartnerIdMediator.postValue(
                fileListUseCase.savePartnerIdVideos(
                    cameraConnectFileList,
                    partnerId
                )
            )
        }
    }

    fun associatePartnerIdToSnapshotList(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerId: String
    ) {
        viewModelScope.launch {
            snapshotPartnerIdMediator.postValue(
                fileListUseCase.savePartnerIdSnapshot(
                    cameraConnectFileList,
                    partnerId
                )
            )
        }
    }


}