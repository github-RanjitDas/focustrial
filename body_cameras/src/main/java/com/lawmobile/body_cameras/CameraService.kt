package com.lawmobile.body_cameras

import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.CameraUser
import com.lawmobile.body_cameras.entities.Config
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.SetupConfiguration
import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.enums.CameraType
import com.lawmobile.body_cameras.enums.CatalogTypesDto
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface CameraService {
    var progressPairingCamera: ((Result<Int>) -> Unit)?
    var arriveNotificationFromCamera: ((NotificationResponse) -> Unit)?

    // Camera operations
    suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String)
    suspend fun takePhoto(): Result<Unit>
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun disconnectCamera(): Result<Unit>
    suspend fun deleteFile(fileName: String): Result<Unit>
    suspend fun isFolderOnCamera(folderName: String): Boolean

    // Verifications
    fun isCameraConnected(gatewayConnection: String): Boolean
    suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit>
    fun getCanReadNotification(): Boolean
    fun reviewIfArriveNotificationInCMDSocket()
    suspend fun isRecording(): Boolean
    suspend fun resetViewFinder(ipAddressClient: String): Boolean

    // File lists
    suspend fun getListOfVideos(): Result<FileResponseWithErrors>
    suspend fun getListOfImages(): Result<FileResponseWithErrors>
    suspend fun getListOfAudios(): Result<FileResponseWithErrors>

    // Camera status
    suspend fun getFreeStorage(): Result<String>
    suspend fun getTotalStorage(): Result<String>
    suspend fun getBatteryLevel(): Result<Int>

    // Get other information
    fun getUrlForLiveStream(): String
    suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo>
    suspend fun getCatalogInfo(catalogTypesDto: CatalogTypesDto): Result<List<CameraCatalog>>
    suspend fun getLogEvents(): Result<List<LogEvent>>
    suspend fun getBodyWornDiagnosis(): Result<Boolean>
    suspend fun getCameraType(): Result<CameraType>
    suspend fun getUserResponse(): Result<CameraUser>
    suspend fun getConfiguration(): Result<Config>

    // Get bytes from camera
    suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun getAudioBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun getFileBytes(cameraFile: CameraFile): Result<ByteArray>

    // Get metadata
    suspend fun getVideoMetadata(fileName: String, folderName: String): Result<VideoInformation>
    suspend fun getPhotoMetadata(cameraFile: CameraFile): Result<PhotoInformation>
    suspend fun getAudioMetadata(cameraFile: CameraFile): Result<AudioInformation>
    suspend fun getMetadataOfPhotos(): Result<List<PhotoInformation>>
    suspend fun getAssociatedVideos(cameraFile: CameraFile): Result<List<CameraFile>>

    // Save metadata
    suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit>
    suspend fun savePhotoMetadata(photoInformation: PhotoInformation): Result<Unit>
    suspend fun saveAudioMetadata(audioInformation: AudioInformation): Result<Unit>
    suspend fun saveAllPhotoMetadata(list: List<PhotoInformation>): Result<Unit>
    suspend fun saveFailSafeVideo(fileBytes: ByteArray): Result<Unit>

    // Other operations
    suspend fun setSetupConfiguration(setupConfiguration: SetupConfiguration): Result<Unit>
    fun cleanCacheFiles()

    // Camera Status features
    suspend fun startCovertMode(): Result<Unit>
    suspend fun stopCovertMode(): Result<Unit>
    suspend fun turnOnBluetooth(): Result<Unit>
    suspend fun turnOffBluetooth(): Result<Unit>
}
