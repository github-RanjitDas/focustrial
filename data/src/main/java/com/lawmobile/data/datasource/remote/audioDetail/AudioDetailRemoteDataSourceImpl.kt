package com.lawmobile.data.datasource.remote.audioDetail

import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AudioDetailRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    AudioDetailRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getAudioBytes(cameraFile: CameraFile): Result<ByteArray> =
        cameraService.getAudioBytes(cameraFile)

    override suspend fun savePartnerIdAudio(audioInformation: AudioInformation): Result<Unit> =
        cameraService.saveAudioMetadata(audioInformation)

    override suspend fun getInformationOfAudio(cameraFile: CameraFile): Result<AudioInformation> =
        cameraService.getAudioMetadata(cameraFile)

    override suspend fun getAssociatedVideos(cameraFile: CameraFile): Result<List<CameraFile>> =
        cameraService.getAssociatedVideos(cameraFile)
}
