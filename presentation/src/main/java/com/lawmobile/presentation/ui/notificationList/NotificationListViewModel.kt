package com.lawmobile.presentation.ui.notificationList

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(private val eventsUseCase: EventsUseCase) :
    BaseViewModel() {
    fun getNotificationList(): List<CameraEvent> = eventsUseCase.getNotificationList()
}
