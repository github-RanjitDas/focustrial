package com.lawmobile.presentation.ui.fileList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class FileListViewModel @Inject constructor(private val fileListUseCase: FileListUseCase) :
    BaseViewModel() {

    private val snapshotListMediator: MediatorLiveData<Result<List<CameraConnectFile>>> =
        MediatorLiveData()
    val snapshotListLiveData: LiveData<Result<List<CameraConnectFile>>> get() = snapshotListMediator

    private val videoListMediator: MediatorLiveData<Result<List<CameraConnectFile>>> = MediatorLiveData()
    val videoListLiveData: LiveData<Result<List<CameraConnectFile>>> get() = videoListMediator

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
}