package com.lawmobile.domain.entities

object VideoListMetadata {

    var metadataList = mutableListOf<RemoteVideoMetadata>()

    fun getVideoMetadata(fileName: String): RemoteVideoMetadata? {
        return metadataList.find { it.videoMetadata.fileName == fileName }
    }

    fun getIndexVideoMetadata(fileName: String) =
        metadataList.indexOfFirst { it.videoMetadata.fileName == fileName }

    fun saveOrUpdateVideoMetadata(remoteVideoMetadata: RemoteVideoMetadata) {
        val videoMetadata = getVideoMetadata(remoteVideoMetadata.videoMetadata.fileName)

        if (videoMetadata == null) {
            metadataList.add(remoteVideoMetadata)
            return
        }

        val indexVideoMetadata = getIndexVideoMetadata(remoteVideoMetadata.videoMetadata.fileName)
        metadataList[indexVideoMetadata] = remoteVideoMetadata
    }

    fun getVideosWithPhotosAssociated(domainCameraFile: DomainCameraFile) =
        metadataList.map { it.videoMetadata }.filter {
            it.associatedPhotos?.find { photo ->
                photo.name == domainCameraFile.name
            } != null
        }

    fun cleanVideoMetadataList() {
        metadataList.clear()
    }
}
