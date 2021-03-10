package com.lawmobile.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safefleet.mobile.kotlin_commons.helpers.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    val isWaitFinishedLiveData: LiveData<Event<Boolean>> get() = isWaitFinishedMediator
    private val isWaitFinishedMediator = MediatorLiveData<Event<Boolean>>()
    fun waitToFinish(time: Long) {
        viewModelScope.launch {
            isWaitFinishedMediator.postValue(Event(false))
            delay(time)
            isWaitFinishedMediator.postValue(Event(true))
        }
    }

    companion object {
        private const val LOADING_TIMEOUT = 20000L
        fun getLoadingTimeOut() = LOADING_TIMEOUT
    }
}
