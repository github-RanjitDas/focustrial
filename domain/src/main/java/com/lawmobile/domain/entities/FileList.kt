package com.lawmobile.domain.entities

object FileList {
    var videoList = emptyList<DomainInformationFile>()
    var imageList = emptyList<DomainInformationFile>()
    var audioList = emptyList<DomainInformationFile>()

    var imageMetadataList = mutableListOf<DomainInformationImageMetadata>()
    var audioMetadataList = mutableListOf<DomainInformationAudioMetadata>()

    fun updateItemInImageMetadataList(item: DomainInformationImageMetadata) {
        val index = imageMetadataList.indexOfFirst {
            it.photoMetadata.fileName == item.photoMetadata.fileName
        }

        if (index != -1) imageMetadataList[index] = item
        else imageMetadataList.add(item)
    }

    fun updateItemInAudioMetadataList(item: DomainInformationAudioMetadata) {
        val index = audioMetadataList.indexOfFirst {
            it.audioMetadata.fileName == item.audioMetadata.fileName
        }

        if (index != -1) audioMetadataList[index] = item
        else audioMetadataList.add(item)
    }

    fun findAndGetImageMetadata(name: String) =
        imageMetadataList.find { it.photoMetadata.fileName == name }

    fun findAndGetAudioMetadata(name: String) =
        audioMetadataList.find { it.audioMetadata.fileName == name }

    fun cleanFileList() {
        videoList = emptyList()
        imageList = emptyList()
        audioList = emptyList()
        imageMetadataList = mutableListOf()
    }
}
