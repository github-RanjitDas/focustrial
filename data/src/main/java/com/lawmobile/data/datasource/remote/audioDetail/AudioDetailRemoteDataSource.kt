package com.lawmobile.data.datasource.remote.audioDetail

import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AudioDetailRemoteDataSource {
    suspend fun getAudioBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun savePartnerIdAudio(audioInformation: AudioInformation): Result<Unit>
    suspend fun getInformationOfAudio(cameraFile: CameraFile): Result<AudioInformation>
    suspend fun getAssociatedVideos(cameraFile: CameraFile): Result<List<CameraFile>>
}
