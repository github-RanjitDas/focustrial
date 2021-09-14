package com.lawmobile.domain.entities

object FileList {
    var videoList = emptyList<DomainInformationFile>()
    var imageList = emptyList<DomainInformationFile>()
    var audioList = emptyList<DomainInformationFile>()
    var imageMetadataList = ArrayList<DomainInformationImageMetadata>()

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
