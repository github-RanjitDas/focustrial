package com.lawmobile.presentation.ui.live.statusBar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusBarBaseViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    private val wasLowStorageShowed = MutableLiveData<Boolean>().apply { value = false }
    private val wasLowBatteryShowed = MutableLiveData<Boolean>().apply { value = false }

    private val _metadataEvents = MediatorLiveData<Result<List<MetadataEvent>>>()
    val metadataEvents: LiveData<Result<List<MetadataEvent>>> get() = _metadataEvents

    private val _batteryLevel = MediatorLiveData<Event<Result<Int>>>()
    val batteryLevel: LiveData<Event<Result<Int>>> get() = _batteryLevel

    private val _storageLevel = MediatorLiveData<Event<Result<List<Double>>>>()
    val storageLevel: LiveData<Event<Result<List<Double>>>> get() = _storageLevel

    fun setLowStorageShowed(wasShowed: Boolean) {
        wasLowStorageShowed.value = wasShowed
    }

    fun wasLowStorageShowed(): Boolean = wasLowStorageShowed.value ?: false

    fun setLowBatteryShowed(wasShowed: Boolean) {
        wasLowBatteryShowed.value = wasShowed
    }

    fun wasLowBatteryShowed(): Boolean = wasLowBatteryShowed.value ?: false

    fun getMetadataEvents() {
        viewModelScope.launch {
            val catalogInfo =
                getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getCatalogInfo() }
            _metadataEvents.postValue(catalogInfo)
        }
    }

    fun getBatteryLevel() {
        viewModelScope.launch {
            val batteryLevel: Result<Int> =
                getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getBatteryLevel() }
            _batteryLevel.postValue(Event(batteryLevel))
        }
    }

    fun getStorageLevels() {
        viewModelScope.launch {
            val gigabyteList = mutableListOf<Double>()
            val freeStorageResult =
                getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getFreeStorage() }
            with(freeStorageResult) {
                doIfSuccess { freeKb ->
                    val freeStorageMb = freeKb.toDouble() / SCALE_BYTES
                    gigabyteList.add(freeStorageMb)
                    delay(200)
                    val totalStorageResult =
                        getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getTotalStorage() }

                    with(totalStorageResult) {
                        doIfSuccess { totalKb ->
                            val totalMb = (totalKb.toDouble() / SCALE_BYTES)
                            val usedBytes = totalMb - freeStorageMb
                            gigabyteList.add(usedBytes)
                            gigabyteList.add(totalMb)
                            _storageLevel.postValue(Event(Result.Success(gigabyteList)))
                        }
                        doIfError {
                            _storageLevel.postValue(Event(Result.Error(it)))
                        }
                    }
                }
                doIfError {
                    _storageLevel.postValue(Event(Result.Error(it)))
                }
            }
        }
    }

    companion object {
        const val SCALE_BYTES = 1024
        private const val RETRY_ATTEMPTS = 5
    }
}
