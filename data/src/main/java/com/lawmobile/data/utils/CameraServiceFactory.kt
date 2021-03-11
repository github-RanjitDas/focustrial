package com.lawmobile.data.utils

import com.safefleet.mobile.external_hardware.cameras.CameraService

interface CameraServiceFactory {
    fun create(): CameraService
}
