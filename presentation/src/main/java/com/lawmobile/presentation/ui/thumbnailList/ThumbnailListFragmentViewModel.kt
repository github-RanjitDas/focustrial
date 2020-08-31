package com.lawmobile.presentation.ui.thumbnailList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class ThumbnailListFragmentViewModel @ViewModelInject constructor(private val thumbnailListUseCase: ThumbnailListUseCase) :
    BaseViewModel() {

    private val thumbnailBytesListMediatorLiveData =
        MediatorLiveData<Result<List<DomainInformationImage>>>()
    val thumbnailListLiveData: LiveData<Result<List<DomainInformationImage>>> get() = thumbnailBytesListMediatorLiveData

    private val imageListMediatorLiveData =
        MediatorLiveData<Result<List<DomainInformationFile>>>()
    val imageListLiveData: LiveData<Result<List<DomainInformationFile>>> get() = imageListMediatorLiveData

    fun getImageBytesList(cameraConnectFile: CameraConnectFile) {
        viewModelScope.launch {
            thumbnailBytesListMediatorLiveData.postValue(
                thumbnailListUseCase.getImagesByteList(cameraConnectFile)
            )
        }
    }

    fun getImageList() {
        viewModelScope.launch {
            imageListMediatorLiveData.postValue(
                thumbnailListUseCase.getImageList()
            )
        }
    }
}