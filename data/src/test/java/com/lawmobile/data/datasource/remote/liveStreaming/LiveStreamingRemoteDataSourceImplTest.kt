package com.lawmobile.data.datasource.remote.liveStreaming

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.CameraDataSource
import io.mockk.*
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class LiveStreamingRemoteDataSourceImplTest {

    private val cameraDataSource: CameraDataSource = mockk {
        every { getUrlForLiveStream() } returns "xyz"
    }

    private val liveStreamingRemoteDataSourceImpl by lazy {
        LiveStreamingRemoteDataSourceImpl(cameraDataSource)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        verify { cameraDataSource.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        val url = liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

}