package com.lawmobile.domain.repository.bodyCameraStatus

import com.lawmobile.domain.repository.BaseRepository

interface BodyCameraStatusRepository : BaseRepository {
    suspend fun isRecording(): Boolean
}
