package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingRepositoryImplTest {

    private val liveStreamingRemoteDataSource: LiveStreamingRemoteDataSource = mockk {
        every { getUrlForLiveStream() } returns "xyz"
    }

    private val liveStreamingRepositoryImpl: LiveStreamingRepositoryImpl by lazy {
        LiveStreamingRepositoryImpl(liveStreamingRemoteDataSource)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamingRepositoryImpl.getUrlForLiveStream()
        verify { liveStreamingRemoteDataSource.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        val url = liveStreamingRemoteDataSource.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

}