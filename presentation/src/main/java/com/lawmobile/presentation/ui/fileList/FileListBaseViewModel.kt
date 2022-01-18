package com.lawmobile.presentation.ui.fileList

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.fileList.state.FileListState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileListBaseViewModel @Inject constructor(
    private val fileListUseCase: FileListUseCase
) : BaseViewModel() {

    val associationResult: SharedFlow<Result<Unit>> get() = _associationResult
    private val _associationResult by lazy { MutableSharedFlow<Result<Unit>>() }

    val fileListState: StateFlow<FileListState?> get() = _fileListState
    private val _fileListState = MutableStateFlow<FileListState?>(null)

    var isSelectActive = false
    var isFilterDialogOpen = false
    var isAssociateDialogOpen = false

    var filesToAssociate: List<DomainCameraFile>? = null

    fun setFileListState(state: FileListState?) {
        _fileListState.value = state
    }

    fun getFileListState(): FileListState? = _fileListState.value

    fun associateOfficerToVideos(partnerId: String) {
        viewModelScope.launch {
            if (!filesToAssociate.isNullOrEmpty()) {
                val result = getResultWithAttempts(RETRY_ATTEMPTS) {
                    fileListUseCase.savePartnerIdVideos(filesToAssociate!!, partnerId)
                }
                _associationResult.emit(result)
            }
        }
    }

    fun associateOfficerToSnapshots(partnerId: String) {
        viewModelScope.launch {
            if (!filesToAssociate.isNullOrEmpty()) {
                val result = getResultWithAttempts(RETRY_ATTEMPTS) {
                    fileListUseCase.savePartnerIdSnapshot(filesToAssociate!!, partnerId)
                }
                _associationResult.emit(result)
            }
        }
    }

    companion object {
        private const val RETRY_ATTEMPTS = 3
    }
}
