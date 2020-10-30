package com.lawmobile.domain.repository.videoPlayback

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.commons.helpers.Result

interface VideoPlaybackRepository : BaseRepository {
    suspend fun getInformationResourcesVideo(domainCameraFile: DomainCameraFile): Result<DomainInformationVideo>
    suspend fun saveVideoMetadata(domainVideoMetadata: DomainVideoMetadata): Result<Unit>
    suspend fun getVideoMetadata(fileName: String, folderName: String): Result<DomainVideoMetadata>
}