package com.lawmobile.body_cameras.cache

import com.lawmobile.body_cameras.entities.CameraFile

object CameraServiceCache {
    var snapshots: List<CameraFile> = emptyList()
    var videos: List<CameraFile> = emptyList()
    var audios: List<CameraFile> = emptyList()
    var videosInformationJson: MutableList<String> = mutableListOf()

    fun findVideoInformation(videoName: String): String =
        videosInformationJson.find { it.contains(videoName) } ?: ""

    fun filterVideoInformationList(fileName: String): List<String> =
        videosInformationJson.filter { it.contains(fileName) }

    fun filterVideos(videoName: String): List<CameraFile> = videos.filter { it.name == videoName }
    fun isVideosInformationEmpty() = videosInformationJson.isEmpty()

    fun updateVideosInformationJson(information: String) {
        val videoInformation = videosInformationJson.find { it == information }
        if (videoInformation == null) videosInformationJson.add(information)
    }

    fun cleanCache() {
        videos = emptyList()
        snapshots = emptyList()
        audios = emptyList()
        videosInformationJson = mutableListOf()
    }
}
