package com.lawmobile.presentation.ui.base.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase,
    private val eventsUseCase: EventsUseCase
) : BaseViewModel() {

    private val _pendingNotificationsCountResult: MediatorLiveData<Result<Int>> = MediatorLiveData()
    val pendingNotificationsCountResult: LiveData<Result<Int>> get() = _pendingNotificationsCountResult

    fun getPendingNotificationsCount() {
        viewModelScope.launch {
            _pendingNotificationsCountResult.postValue(eventsUseCase.getPendingNotificationsCount())
        }
    }

    fun disconnectCamera() {
        viewModelScope.launch {
            liveStreamingUseCase.disconnectCamera()
        }
    }
}
