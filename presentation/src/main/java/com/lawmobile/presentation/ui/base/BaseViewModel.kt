package com.lawmobile.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    val isWaitFinishedLiveData: LiveData<Boolean> get() = isWaitFinishedMediator
    private val isWaitFinishedMediator = MediatorLiveData<Boolean>()
    fun waitToFinish(time: Long) {
        viewModelScope.launch {
            isWaitFinishedMediator.postValue(false)
            delay(time)
            isWaitFinishedMediator.postValue(true)
        }
    }

    companion object {
        private const val LOADING_TIMEOUT = 20000L
        fun getLoadingTimeOut() = LOADING_TIMEOUT
    }
}