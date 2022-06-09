package com.lawmobile.domain.usecase.saveFileBytes

import com.lawmobile.domain.repository.file.FileRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class SaveFailSafeVideoImplTest {

    private val fileRepository: FileRepository = mockk {
        coEvery { saveFailSafeVideo(any()) } returns Result.Success(Unit)
    }

    private val saveFailSafeVideoImpl: SaveFailSafeVideoImpl by lazy {
        SaveFailSafeVideoImpl(fileRepository)
    }

    @Test
    fun invoke() {
        val byte = ByteArray(1)

        coEvery { fileRepository.saveFailSafeVideo(byte) } returns Result.Success(Unit)
        runBlocking {
            val result = saveFailSafeVideoImpl.invoke(byte)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { fileRepository.saveFailSafeVideo(byte) }
    }
}
