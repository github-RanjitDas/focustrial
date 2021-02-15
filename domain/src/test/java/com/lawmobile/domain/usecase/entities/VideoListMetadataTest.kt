package com.lawmobile.domain.usecase.entities

import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import io.mockk.clearAllMocks
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class VideoListMetadataTest {

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Test
    fun getVideoMetadataSuccess() {
        val remoteVideoMetadata = RemoteVideoMetadata(
            DomainVideoMetadata("1234.mp4"),
            false
        )
        VideoListMetadata.metadataList = mutableListOf(remoteVideoMetadata)
        val metadata = VideoListMetadata.getVideoMetadata("1234.mp4")
        Assert.assertEquals(metadata, remoteVideoMetadata)
    }

    @Test
    fun getVideoMetadataNull() {
        VideoListMetadata.metadataList = mutableListOf(
            RemoteVideoMetadata(
                DomainVideoMetadata("1234.mp4"),
                false
            )
        )
        val metadata = VideoListMetadata.getVideoMetadata("")
        Assert.assertTrue(metadata == null)
    }

    @Test
    fun saveOrUpdateVideoMetadataIsChangedTrue() {
        VideoListMetadata.metadataList = mutableListOf(
            RemoteVideoMetadata(
                DomainVideoMetadata("1234.mp4"),
                false
            )
        )
        val remoteVideoMetadata = RemoteVideoMetadata(
            DomainVideoMetadata("1234.mp4"),
            true
        )
        VideoListMetadata.saveOrUpdateVideoMetadata(remoteVideoMetadata)
        Assert.assertTrue(VideoListMetadata.metadataList.first().isChanged)
    }

    @Test
    fun saveOrUpdateVideoMetadataIsChangedFalse() {
        VideoListMetadata.metadataList = mutableListOf()
        val remoteVideoMetadata = RemoteVideoMetadata(
            DomainVideoMetadata("1234.mp4"),
            false
        )
        VideoListMetadata.saveOrUpdateVideoMetadata(remoteVideoMetadata)
        Assert.assertFalse(VideoListMetadata.metadataList.first().isChanged)
    }
}
