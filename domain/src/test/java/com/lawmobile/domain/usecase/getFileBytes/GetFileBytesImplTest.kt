package com.lawmobile.domain.usecase.getFileBytes

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.file.FileRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class GetFileBytesImplTest {

    private val fileRepository: FileRepository = mockk {
        val byte = ByteArray(1)
        coEvery { getFileBytes(any()) } returns Result.Success(byte)
    }

    private val getFileBytesImpl: GetFileBytesImpl by lazy {
        GetFileBytesImpl(fileRepository)
    }

    @Test
    fun invoke() {
        val domainCameraFile: DomainCameraFile = mockk()
        val byte = ByteArray(1)
        coEvery { fileRepository.getFileBytes(domainCameraFile) } returns Result.Success(byte)
        runBlocking {
            val result = getFileBytesImpl.invoke(domainCameraFile)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { fileRepository.getFileBytes(domainCameraFile) }
    }
}
