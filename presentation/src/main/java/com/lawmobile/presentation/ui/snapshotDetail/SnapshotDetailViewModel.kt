package com.lawmobile.presentation.ui.snapshotDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class SnapshotDetailViewModel @Inject constructor(
    private val snapshotDetailUseCase: SnapshotDetailUseCase
) : BaseViewModel() {

    private val imageBytesMediator: MediatorLiveData<Result<ByteArray>> = MediatorLiveData()
    val imageBytesLiveData: LiveData<Result<ByteArray>> get() = imageBytesMediator

    fun getImageBytes(cameraConnectFile: CameraConnectFile) {
        viewModelScope.launch {
            imageBytesMediator.postValue(snapshotDetailUseCase.getImageBytes(cameraConnectFile))
        }
    }
}