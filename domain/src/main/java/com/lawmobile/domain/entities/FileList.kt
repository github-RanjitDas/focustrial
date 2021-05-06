package com.lawmobile.domain.entities

object FileList {
    var videoList = emptyList<DomainInformationFile>()
    var imageList = emptyList<DomainInformationFile>()
    var imageMetadataList = ArrayList<DomainInformationImageMetadata>()

    fun changeImageList(newListOfImages: List<DomainInformationFile>) {
        imageList = newListOfImages
    }

    fun changeVideoList(newListOfVideos: List<DomainInformationFile>) {
        videoList = newListOfVideos
    }

    fun updateItemInImageMetadataList(item: DomainInformationImageMetadata) {
        val index = imageMetadataList.indexOfFirst {
            it.photoMetadata.fileName == item.photoMetadata.fileName
        }

        if (index != -1) imageMetadataList[index] = item
        else imageMetadataList.add(item)
    }

    fun getMetadataOfImageInList(name: String) =
        imageMetadataList.find { it.photoMetadata.fileName == name }

    fun cleanFileList() {
        videoList = emptyList()
        imageList = emptyList()
        imageMetadataList = ArrayList()
    }
}
