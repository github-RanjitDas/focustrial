package com.lawmobile.data.datasource.remote.videoPlayback

import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo
import com.safefleet.mobile.commons.helpers.Result

interface VideoPlaybackRemoteDataSource {
    suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoInfo>
    suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>>
}