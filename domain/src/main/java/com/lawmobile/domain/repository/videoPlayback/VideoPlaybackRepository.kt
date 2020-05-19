package com.lawmobile.domain.repository.videoPlayback

import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface VideoPlaybackRepository: BaseRepository {
    suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<DomainInformationVideo>
    suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>>
}