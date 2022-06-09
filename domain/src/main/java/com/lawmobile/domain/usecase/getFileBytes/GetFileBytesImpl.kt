package com.lawmobile.domain.usecase.getFileBytes

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.file.FileRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class GetFileBytesImpl(
    private val fileRepository: FileRepository
) : GetFileBytes {
    override suspend fun invoke(file: DomainCameraFile): Result<ByteArray> =
        fileRepository.getFileBytes(file)
}
