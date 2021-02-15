package com.lawmobile.domain.entities

object CacheManager {

    fun cleanFileLists() {
        FileList.cleanFileList()
        VideoListMetadata.cleanVideoMetadataList()
        CameraInfo.cleanInfo()
    }
}
