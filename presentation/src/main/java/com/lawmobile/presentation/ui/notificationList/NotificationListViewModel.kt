package com.lawmobile.presentation.ui.notificationList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(private val eventsUseCase: EventsUseCase) :
    BaseViewModel() {

    val notificationEventsResult: LiveData<Result<List<CameraEvent>>> get() = _notificationListResult
    private val _notificationListResult = MediatorLiveData<Result<List<CameraEvent>>>()

    suspend fun getNotificationEvents() {
        val result = eventsUseCase.getNotificationEvents()
        _notificationListResult.postValue(result)
        result.doIfSuccess { setAllNotificationsAsRead() }
    }

    suspend fun getNotificationDictionary() {
        val notificationDictionaryListResult = eventsUseCase.getNotificationDictionary()
        notificationDictionaryListResult.doIfSuccess {
            CameraInfo.notificationDictionaryList = it
        }
    }

    private suspend fun setAllNotificationsAsRead() {
        CameraInfo.currentNotificationCount = 0
        eventsUseCase.setAllNotificationsAsRead()
    }
}
