package com.lawmobile.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var cameraConnectService: CameraConnectService

    fun deactivateCameraHotspot() {
        viewModelScope.launch {
            cameraConnectService.disconnectCamera()
        }
    }
}