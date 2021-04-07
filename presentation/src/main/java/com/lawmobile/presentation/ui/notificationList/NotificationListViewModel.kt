package com.lawmobile.presentation.ui.notificationList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(private val eventsUseCase: EventsUseCase) :
    BaseViewModel() {

    val notificationListResult: LiveData<Result<List<CameraEvent>>> get() = _notificationListResult
    private val _notificationListResult = MediatorLiveData<Result<List<CameraEvent>>>()

    fun getAllNotificationEvents() {
        viewModelScope.launch {
            _notificationListResult.postValue(eventsUseCase.getAllNotificationEvents())
        }
    }

    fun setAllNotificationsAsRead() {
        viewModelScope.launch {
            eventsUseCase.setAllNotificationsAsRead()
        }
    }
}
