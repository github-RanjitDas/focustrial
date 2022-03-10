package com.lawmobile.body_cameras.cache

import com.lawmobile.body_cameras.entities.CameraFile
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CameraServiceCacheTest {

    @BeforeEach
    fun setup() {
        val cameraFile1 = CameraFile("video1", "", "", "")
        val cameraFile2 = CameraFile("video2", "", "", "")
        CameraServiceCache.videosInformationJson = mutableListOf("andres", "manuel", "john", "raul")
        CameraServiceCache.videos = mutableListOf(cameraFile1, cameraFile2)
    }

    @Test
    fun findVideoInformationFound() {
        val videoInformation = CameraServiceCache.findVideoInformation("andres")
        Assert.assertEquals(CameraServiceCache.videosInformationJson.first(), videoInformation)
    }

    @Test
    fun findVideoInformationNotFound() {
        val videoInformation = CameraServiceCache.findVideoInformation("pipe")
        Assert.assertEquals("", videoInformation)
    }

    @Test
    fun filterVideoInformationSuccess() {
        val videoInformationList = CameraServiceCache.filterVideoInformationList("a")
        Assert.assertEquals(3, videoInformationList.size)
    }

    @Test
    fun filterVideoInformationError() {
        val videoInformationList = CameraServiceCache.filterVideoInformationList("z")
        Assert.assertTrue(videoInformationList.isEmpty())
    }

    @Test
    fun filterVideosSuccess() {
        val filteredVideos = CameraServiceCache.filterVideos("video1")
        Assert.assertEquals(1, filteredVideos.size)
    }

    @Test
    fun filterVideosError() {
        val filteredVideos = CameraServiceCache.filterVideos("video3")
        Assert.assertEquals(0, filteredVideos.size)
    }

    @Test
    fun isVideoInformationNotEmpty() {
        Assert.assertFalse(CameraServiceCache.isVideosInformationEmpty())
    }

    @Test
    fun isVideoInformationEmpty() {
        CameraServiceCache.videosInformationJson.clear()
        Assert.assertTrue(CameraServiceCache.isVideosInformationEmpty())
    }

    @Test
    fun cleanCache() {
        CameraServiceCache.cleanCache()
        Assert.assertTrue(CameraServiceCache.videos.isEmpty())
        Assert.assertTrue(CameraServiceCache.snapshots.isEmpty())
        Assert.assertTrue(CameraServiceCache.audios.isEmpty())
        Assert.assertTrue(CameraServiceCache.videosInformationJson.isEmpty())
    }

    @Test
    fun updateVideosInformationJsonExisting() {
        CameraServiceCache.updateVideosInformationJson("andres")
        Assert.assertEquals(4, CameraServiceCache.videosInformationJson.size)
    }

    @Test
    fun updateVideosInformationJsonNotExisting() {
        CameraServiceCache.updateVideosInformationJson("kevin")
        Assert.assertEquals(5, CameraServiceCache.videosInformationJson.size)
    }
}
