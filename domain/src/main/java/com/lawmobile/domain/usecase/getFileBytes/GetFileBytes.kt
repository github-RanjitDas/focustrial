package com.lawmobile.domain.usecase.getFileBytes

import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface GetFileBytes {
    suspend operator fun invoke(file: DomainCameraFile): Result<ByteArray>
}
