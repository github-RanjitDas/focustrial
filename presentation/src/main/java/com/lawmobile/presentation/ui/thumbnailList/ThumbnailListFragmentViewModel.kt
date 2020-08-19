package com.lawmobile.presentation.ui.thumbnailList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class ThumbnailListFragmentViewModel @ViewModelInject constructor(private val thumbnailListUseCase: ThumbnailListUseCase) :
    BaseViewModel() {

    private val thumbnailBytesListMediatorLiveData =
        MediatorLiveData<Result<List<DomainInformationImage>>>()
    val thumbnailListLiveData: LiveData<Result<List<DomainInformationImage>>> get() = thumbnailBytesListMediatorLiveData

    fun getImageBytesList(currentPage: Int) {
        viewModelScope.launch {
            thumbnailBytesListMediatorLiveData.postValue(
                thumbnailListUseCase.getImagesByteList(currentPage)
            )
        }
    }

    fun getImageListSize(): Int =
        thumbnailListUseCase.getImageListSize()
}