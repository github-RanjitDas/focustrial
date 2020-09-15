package com.lawmobile.data.entities

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata

object FileList {
    var listOfVideos = emptyList<DomainInformationFile>()
    var listOfImages = emptyList<DomainInformationFile>()
    var listOfMetadataImages = ArrayList<DomainInformationImageMetadata>()

    fun changeListOfImages(newListOfImages: List<DomainInformationFile>) {
        this.listOfImages = newListOfImages
    }

    fun changeListOfVideos(newListOfVideos: List<DomainInformationFile>) {
        this.listOfVideos = newListOfVideos
    }

    fun updateItemInListImageMetadata(item: DomainInformationImageMetadata) {
        val index = listOfMetadataImages.indexOfFirst { it.cameraConnectPhotoMetadata.fileName == item.cameraConnectPhotoMetadata.fileName }
        if (index != -1) {
            listOfMetadataImages[index] = item
        }
        listOfMetadataImages.add(item)
    }

    fun getItemInListImageOfMetadata(name: String) =
        listOfMetadataImages.find { it.cameraConnectPhotoMetadata.fileName == name }
}