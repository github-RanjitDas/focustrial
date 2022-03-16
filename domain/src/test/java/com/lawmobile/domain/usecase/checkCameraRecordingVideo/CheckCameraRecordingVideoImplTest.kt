package com.lawmobile.domain.usecase.checkCameraRecordingVideo

import com.lawmobile.domain.repository.bodyCameraStatus.BodyCameraStatusRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CheckCameraRecordingVideoImplTest {

    private val repository: BodyCameraStatusRepository = mockk()
    private val checkCameraRecordingVideo = CheckCameraRecordingVideoImpl(repository)

    @Test
    fun invokeTrue() = runBlockingTest {
        coEvery { repository.isRecording() } returns true
        Assert.assertTrue(checkCameraRecordingVideo())
        coVerify { repository.isRecording() }
    }

    @Test
    fun invokeFalse() = runBlockingTest {
        coEvery { repository.isRecording() } returns false
        Assert.assertFalse(checkCameraRecordingVideo())
        coVerify { repository.isRecording() }
    }
}
