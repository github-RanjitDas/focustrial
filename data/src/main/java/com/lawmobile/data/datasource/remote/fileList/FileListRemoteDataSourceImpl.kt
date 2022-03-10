package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class FileListRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    FileListRemoteDataSource {

    private var cameraConnectService = cameraServiceFactory.create()

    override suspend fun savePartnerIdVideos(videoInformation: VideoInformation): Result<Unit> =
        cameraConnectService.saveVideoMetadata(videoInformation)

    override suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit> =
        cameraConnectService.savePhotoMetadata(photoInformation)

    override suspend fun savePartnerIdAudios(audioInformation: AudioInformation): Result<Unit> =
        cameraConnectService.saveAudioMetadata(audioInformation)

    override suspend fun savePartnerIdInAllSnapshots(list: List<PhotoInformation>): Result<Unit> =
        cameraConnectService.saveAllPhotoMetadata(list)

    override suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>> =
        cameraConnectService.getMetadataOfPhotos()
}
