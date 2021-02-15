package com.lawmobile.domain.usecase.videoPlayback

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface VideoPlaybackUseCase : BaseUseCase {
    suspend fun getInformationResourcesVideo(domainCameraFile: DomainCameraFile): Result<DomainInformationVideo>
    suspend fun saveVideoMetadata(domainVideoMetadata: DomainVideoMetadata): Result<Unit>
    suspend fun getVideoMetadata(fileName: String, folderName: String): Result<DomainVideoMetadata>
}
