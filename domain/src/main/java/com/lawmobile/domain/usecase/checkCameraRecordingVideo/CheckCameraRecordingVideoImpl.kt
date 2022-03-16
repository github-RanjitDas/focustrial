package com.lawmobile.domain.usecase.checkCameraRecordingVideo

import com.lawmobile.domain.repository.bodyCameraStatus.BodyCameraStatusRepository

class CheckCameraRecordingVideoImpl(
    private val bodyCameraStatusRepository: BodyCameraStatusRepository
) : CheckCameraRecordingVideo {
    override suspend operator fun invoke(): Boolean = bodyCameraStatusRepository.isRecording()
}
