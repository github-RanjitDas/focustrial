package com.lawmobile.presentation.ui.live.statusBar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
class LiveStatusBarBaseViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    private val catalogInfoMediatorLiveData = MediatorLiveData<Result<List<MetadataEvent>>>()
    val catalogInfoLiveData: LiveData<Result<List<MetadataEvent>>> get() = catalogInfoMediatorLiveData

    private val batteryLevelMediatorLiveData = MediatorLiveData<Event<Result<Int>>>()
    val batteryLevelLiveData: LiveData<Event<Result<Int>>> get() = batteryLevelMediatorLiveData

    private val storageMediatorLiveData = MediatorLiveData<Event<Result<List<Double>>>>()
    val storageLiveData: LiveData<Event<Result<List<Double>>>> get() = storageMediatorLiveData

    fun getMetadataEvents() {
        viewModelScope.launch {
            val catalogInfo =
                getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getCatalogInfo() }
            catalogInfoMediatorLiveData.postValue(catalogInfo)
        }
    }

    fun getBatteryLevel() {
        viewModelScope.launch {
            val batteryLevel: Result<Int> =
                getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getBatteryLevel() }
            batteryLevelMediatorLiveData.postValue(
                Event(batteryLevel)
            )
        }
    }

    fun getStorageLevels() {
        viewModelScope.launch {
            val gigabyteList = mutableListOf<Double>()
            val freeStorageResult =
                getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getFreeStorage() }
            with(freeStorageResult) {
                doIfSuccess { freeKb ->
                    val storageFreeMb = freeKb.toDouble() / SCALE_BYTES
                    gigabyteList.add(storageFreeMb)
                    delay(200)
                    val totalStorageResult =
                        getResultWithAttempts(RETRY_ATTEMPTS) { liveStreamingUseCase.getTotalStorage() }

                    with(totalStorageResult) {
                        doIfSuccess { totalKb ->
                            val totalMb = (totalKb.toDouble() / SCALE_BYTES)
                            val usedBytes = totalMb - storageFreeMb
                            gigabyteList.add(usedBytes)
                            gigabyteList.add(totalMb)
                            storageMediatorLiveData.postValue(Event(Result.Success(gigabyteList)))
                        }
                        doIfError {
                            storageMediatorLiveData.postValue(Event(Result.Error(it)))
                        }
                    }
                }
                doIfError {
                    storageMediatorLiveData.postValue(Event(Result.Error(it)))
                }
            }
        }
    }

    companion object {
        const val SCALE_BYTES = 1024
        private const val RETRY_ATTEMPTS = 5
    }
}
