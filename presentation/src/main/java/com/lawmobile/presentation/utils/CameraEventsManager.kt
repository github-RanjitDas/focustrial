package com.lawmobile.presentation.utils

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CameraEventsManager @Inject constructor(
    private val eventsUseCase: EventsUseCase,
    private val dispatcher: CoroutineDispatcher,
    private val notificationHandler: Handler,
) : CoroutineScope {

    val logEventsLiveData: LiveData<Result<List<CameraEvent>>>
        get() = _logEventsLiveData
    private val _logEventsLiveData = MediatorLiveData<Result<List<CameraEvent>>>()

    private val job = Job()

    private val notificationTask = object : Runnable {
        override fun run() {
            notificationHandler.postDelayed(this, NOTIFICATION_PERIOD)
            if (isReadyToReadEvents && eventsUseCase.isPossibleToReadLog()) {
                launch {
                    _logEventsLiveData.postValue(
                        eventsUseCase.getCameraEvents()
                    )
                }
            }
        }
    }

    fun startReading() {
        notificationHandler.post(notificationTask)
    }

    fun stopReading() {
        isReadyToReadEvents = false
        notificationHandler.removeCallbacks(notificationTask)
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher + job

    companion object {
        private const val NOTIFICATION_PERIOD = 5000L
        var isReadyToReadEvents = false
    }
}
