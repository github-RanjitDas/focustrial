package com.lawmobile.domain.utils

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.VideoListMetadata

object CacheManager {
    fun cleanFileLists() {
        FileList.cleanFileList()
        VideoListMetadata.cleanVideoMetadataList()
        CameraInfo.cleanInfo()
    }
}
