package com.lawmobile.presentation.ui.fileList.thumbnailList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.presentation.extensions.postEventValueWithTimeout
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThumbnailListViewModel @Inject constructor(
    private val thumbnailListUseCase: ThumbnailListUseCase,
    private var job: Job? = null
) : BaseViewModel() {

    private val thumbnailBytesListMediatorLiveData =
        MediatorLiveData<Event<Result<DomainInformationImage>>>()
    val thumbnailListLiveData: LiveData<Event<Result<DomainInformationImage>>> get() = thumbnailBytesListMediatorLiveData

    private val imageListMediatorLiveData =
        MediatorLiveData<Result<DomainInformationFileResponse>>()
    val imageListLiveData: LiveData<Result<DomainInformationFileResponse>> get() = imageListMediatorLiveData

    fun getImageBytes(domainCameraFile: DomainCameraFile) {
        job = viewModelScope.launch {
            thumbnailBytesListMediatorLiveData.postEventValueWithTimeout(
                LOADING_TIMEOUT_SNAPSHOT_BYTES
            ) {
                Event(
                    getResultWithAttempts(ATTEMPTS_TO_GET_BYTES) {
                        thumbnailListUseCase.getImageBytes(domainCameraFile)
                    }
                )
            }
        }
    }

    fun getSnapshotList() {
        viewModelScope.launch {
            imageListMediatorLiveData.postValueWithTimeout(LOADING_TIMEOUT_SNAPSHOT_INFORMATION) {
                thumbnailListUseCase.getSnapshotList()
            }
        }
    }

    fun getVideoList() {
        viewModelScope.launch(Dispatchers.IO) {
            imageListMediatorLiveData.postValueWithTimeout(LOADING_TIMEOUT_SNAPSHOT_INFORMATION) {
                thumbnailListUseCase.getVideoList()
            }
        }
    }

    fun cancelGetImageBytes() {
        job?.cancel()
    }

    companion object {
        private const val LOADING_TIMEOUT_SNAPSHOT_INFORMATION = 35000L
        private const val LOADING_TIMEOUT_SNAPSHOT_BYTES = 20000L
        private const val ATTEMPTS_TO_GET_BYTES = 2
    }
}
