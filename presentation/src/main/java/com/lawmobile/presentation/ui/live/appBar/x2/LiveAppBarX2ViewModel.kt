package com.lawmobile.presentation.ui.live.appBar.x2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveAppBarX2ViewModel @Inject constructor(
    private val eventsUseCase: EventsUseCase
) : BaseViewModel() {

    private val _pendingNotificationsSizeResult: MediatorLiveData<Result<Int>> = MediatorLiveData()
    val pendingNotificationSizeResult: LiveData<Result<Int>> get() = _pendingNotificationsSizeResult

    fun getPendingNotificationsCount() {
        viewModelScope.launch {
            _pendingNotificationsSizeResult.postValue(eventsUseCase.getPendingNotificationsCount())
        }
    }
}
