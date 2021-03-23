package com.lawmobile.presentation.utils

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.usecase.notification.NotificationUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CameraNotificationManager @Inject constructor(
    private val notificationUseCase: NotificationUseCase,
    private val dispatcher: CoroutineDispatcher,
    private val notificationHandler: Handler
) : CoroutineScope {

    val logEventsLiveData: LiveData<Result<List<DomainNotification>>>
        get() = _logEventsLiveData
    private val _logEventsLiveData = MediatorLiveData<Result<List<DomainNotification>>>()

    private val notificationTask = object : Runnable {
        override fun run() {
            notificationHandler.postDelayed(this, NOTIFICATION_PERIOD)
            if (notificationUseCase.isPossibleToReadLog()) {
                launch {
                    _logEventsLiveData.postValue(
                        notificationUseCase.getLogEvents()
                    )
                }
            }
        }
    }

    fun startReading() {
        notificationHandler.post(notificationTask)
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher

    companion object {
        private const val NOTIFICATION_PERIOD = 5000L
    }
}
