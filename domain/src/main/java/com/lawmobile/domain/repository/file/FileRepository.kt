package com.lawmobile.domain.repository.file

import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface FileRepository {
    suspend fun getFileBytes(file: DomainCameraFile): Result<ByteArray>
    suspend fun saveFailSafeVideo(fileBytes: ByteArray): Result<Unit>
}
