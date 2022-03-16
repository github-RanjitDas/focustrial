package com.lawmobile.data.repository.bodyCameraStatus

import com.lawmobile.data.datasource.remote.bodyCameraStatus.BodyCameraStatusDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class BodyCameraStatusRepositoryImplTest {
    private val dataSource: BodyCameraStatusDataSource = mockk()
    private val bodyCameraStatusRepository = BodyCameraStatusRepositoryImpl(dataSource)

    @Test
    fun isRecordingTrue() = runBlockingTest {
        coEvery { dataSource.isRecording() } returns true
        Assert.assertTrue(bodyCameraStatusRepository.isRecording())
        coVerify { dataSource.isRecording() }
    }

    @Test
    fun isRecordingFalse() = runBlockingTest {
        coEvery { dataSource.isRecording() } returns false
        Assert.assertFalse(bodyCameraStatusRepository.isRecording())
        coVerify { dataSource.isRecording() }
    }
}
