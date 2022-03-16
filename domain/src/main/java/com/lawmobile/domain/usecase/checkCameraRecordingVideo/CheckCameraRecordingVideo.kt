package com.lawmobile.domain.usecase.checkCameraRecordingVideo

interface CheckCameraRecordingVideo {
    suspend operator fun invoke(): Boolean
}
