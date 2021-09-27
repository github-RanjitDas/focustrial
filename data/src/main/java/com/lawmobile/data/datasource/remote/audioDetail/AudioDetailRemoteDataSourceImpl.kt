package com.lawmobile.data.datasource.remote.audioDetail

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
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
}
