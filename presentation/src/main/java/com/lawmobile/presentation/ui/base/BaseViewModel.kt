package com.lawmobile.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    val isWaitFinishedLiveData: LiveData<Event<Boolean>> get() = isWaitFinishedMediator
    private val isWaitFinishedMediator = MediatorLiveData<Event<Boolean>>()

    fun setEventsUseCase(instance: EventsUseCase) {
        eventsUseCase = instance
    }

    fun saveNotificationEvent(cameraEvent: CameraEvent) {
        viewModelScope.launch {
            eventsUseCase.saveEvent(cameraEvent)
        }
    }

    fun waitToFinish(time: Long) {
        viewModelScope.launch {
            isWaitFinishedMediator.postValue(Event(false))
            delay(time)
            isWaitFinishedMediator.postValue(Event(true))
        }
    }

    companion object {
        private lateinit var eventsUseCase: EventsUseCase
        private const val LOADING_TIMEOUT = 70000L
        fun getLoadingTimeOut() = LOADING_TIMEOUT
    }
}
