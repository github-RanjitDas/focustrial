package com.lawmobile.domain.repository.videoPlayback

import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result

interface VideoPlaybackRepository: BaseRepository {
    suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<DomainInformationVideo>
    suspend fun saveVideoMetadata(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit>
    suspend fun getVideoMetadata(fileName: String): Result<CameraConnectVideoMetadata>
}