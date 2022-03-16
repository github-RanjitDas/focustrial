package com.lawmobile.data.datasource.remote.bodyCameraStatus

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.data.utils.CameraServiceFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class BodyCameraStatusDataSourceImplTest {

    private val bodyCamera: CameraService = mockk()
    private val bodyCameraFactory: CameraServiceFactory = mockk {
        every { create() } returns bodyCamera
    }

    private val bodyCameraStatusDataSource = BodyCameraStatusDataSourceImpl(bodyCameraFactory)

    @Test
    fun isRecordingTrue() = runBlockingTest {
        coEvery { bodyCamera.isRecording() } returns true
        Assert.assertTrue(bodyCameraStatusDataSource.isRecording())
        coVerify { bodyCamera.isRecording() }
    }

    @Test
    fun isRecordingFalse() = runBlockingTest {
        coEvery { bodyCamera.isRecording() } returns false
        Assert.assertFalse(bodyCameraStatusDataSource.isRecording())
        coVerify { bodyCamera.isRecording() }
    }
}
