package com.lawmobile.presentation.ui.linkSnapshotsToVideo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.linkSnapshotsToVideo.LinkSnapshotsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class LinkSnapshotsViewModel @ViewModelInject constructor(private val linkSnapshotsUseCase: LinkSnapshotsUseCase) :
    BaseViewModel() {

    private val imageBytesListMediatorLiveData =
        MediatorLiveData<Result<List<DomainInformationImage>>>()
    val imageListLiveData: LiveData<Result<List<DomainInformationImage>>> get() = imageBytesListMediatorLiveData

    fun getImageBytesList(currentPage: Int) {
        viewModelScope.launch {
            imageBytesListMediatorLiveData.postValue(
                linkSnapshotsUseCase.getImagesByteList(currentPage)
            )
        }
    }

    fun getImageListSize(): Int =
        linkSnapshotsUseCase.getImageListSize()
}