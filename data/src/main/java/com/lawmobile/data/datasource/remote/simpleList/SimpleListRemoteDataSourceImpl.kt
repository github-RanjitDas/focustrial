package com.lawmobile.data.datasource.remote.simpleList

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toDomain
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SimpleListRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory
) : SimpleListRemoteDataSource {

    private var cameraConnectService = cameraServiceFactory.create()

    override suspend fun getSnapshotList(): Result<FileResponseWithErrors> =
        cameraConnectService.getListOfImages()

    override suspend fun getAudioList(): Result<FileResponseWithErrors> =
        cameraConnectService.getListOfAudios()

    override suspend fun getVideoList(): Result<FileResponseWithErrors> =
        cameraConnectService.getListOfVideos().apply {
            doIfSuccess { withContext(Dispatchers.IO) { getMetadataForVideoList(it.items) } }
        }

    private suspend fun getMetadataForVideoList(videos: List<CameraFile>) {
        videos.forEach {
            VideoListMetadata.getVideoMetadata(it.name) ?: run {
                delay(GET_VIDEO_METADATA_DELAY)
                getResultWithAttempts(GET_METADATA_ATTEMPTS) {
                    cameraConnectService.getVideoMetadata(it.name, it.nameFolder)
                }.doIfSuccess { videoInformation ->
                    val domainMetadata = videoInformation.toDomain()
                    val remoteMetadata = RemoteVideoMetadata(domainMetadata, false)
                    VideoListMetadata.saveOrUpdateVideoMetadata(remoteMetadata)
                }
            }
        }
    }

    companion object {
        private const val GET_METADATA_ATTEMPTS = 5
        private const val GET_VIDEO_METADATA_DELAY = 100L
    }
}
