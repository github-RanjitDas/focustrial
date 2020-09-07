package com.lawmobile.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var cameraConnectService: CameraConnectService
    val isWaitFinishedLiveData: LiveData<Boolean> get() = isWaitFinishedMediator
    private val isWaitFinishedMediator = MediatorLiveData<Boolean>()

    fun deactivateCameraHotspot() {
        viewModelScope.launch {
            cameraConnectService.disconnectCamera()
        }
    }

    fun waitToFinish(time: Long) {
        viewModelScope.launch {
            isWaitFinishedMediator.postValue(false)
            delay(time)
            isWaitFinishedMediator.postValue(true)
        }
    }

    companion object {
        const val LOADING_TIMEOUT = 20000L
    }
}