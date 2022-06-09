package com.lawmobile.domain.usecase.saveFileBytes

import com.lawmobile.domain.repository.file.FileRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SaveFailSafeVideoImpl(
    private val fileRepository: FileRepository
) : SaveFailSafeVideo {
    override suspend fun invoke(fileBytes: ByteArray): Result<Unit> =
        fileRepository.saveFailSafeVideo(fileBytes)
}
