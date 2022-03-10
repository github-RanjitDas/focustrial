package com.lawmobile.data.utils

import com.lawmobile.body_cameras.CameraService

interface CameraServiceFactory {
    fun create(): CameraService
}
