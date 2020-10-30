package com.lawmobile.data.entities

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata

object FileList {
    var videoList = emptyList<DomainInformationFile>()
    var imageList = emptyList<DomainInformationFile>()
    var imageMetadataList = ArrayList<DomainInformationImageMetadata>()

    fun changeImageList(newListOfImages: List<DomainInformationFile>) {
        this.imageList = newListOfImages
    }

    fun changeVideoList(newListOfVideos: List<DomainInformationFile>) {
        this.videoList = newListOfVideos
    }

    fun updateItemInImageMetadataList(item: DomainInformationImageMetadata) {
        val index = imageMetadataList.indexOfFirst {
            it.cameraConnectPhotoMetadata.fileName == item.cameraConnectPhotoMetadata.fileName
        }

        if (index != -1) imageMetadataList[index] = item
        else imageMetadataList.add(item)
    }

    fun getMetadataOfImageInList(name: String) =
        imageMetadataList.find { it.cameraConnectPhotoMetadata.fileName == name }
}