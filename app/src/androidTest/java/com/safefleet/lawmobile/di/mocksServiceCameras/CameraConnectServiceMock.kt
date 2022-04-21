package com.safefleet.lawmobile.di.mocksServiceCameras

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.CameraUser
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.SetupConfiguration
import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.entities.VideoMetadata
import com.lawmobile.body_cameras.enums.CameraType
import com.lawmobile.domain.entities.FileList
import com.safefleet.lawmobile.helpers.MockUtils
import com.safefleet.lawmobile.helpers.MockUtils.Companion.bodyWornDiagnosisResult
import com.safefleet.lawmobile.testData.CameraEventsData
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.testData.TestLoginData
import com.safefleet.lawmobile.testData.VideoPlaybackMetadata
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.mockk

class CameraConnectServiceMock : CameraService {

    override var arriveNotificationFromCamera: ((NotificationResponse) -> Unit)? = null
    override var progressPairingCamera: ((Result<Int>) -> Unit)? = null

    override fun cleanCacheFiles() = Unit

    override suspend fun startCovertMode(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun stopCovertMode(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun turnOnBluetooth(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun turnOffBluetooth(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun deleteFile(fileName: String): Result<Unit> {
        return Result.Success(Unit)
    }

    companion object {
        var snapshotsList = CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
        var videoList = CameraFilesData.DEFAULT_VIDEO_LIST.value
        var audioList = CameraFilesData.DEFAULT_AUDIO_LIST.value
        var takenPhotos = 0
        var takenVideos = 0
        var result: Result<Int> = Result.Success(100)
        var eventList: MutableList<LogEvent> = CameraEventsData.DEFAULT.value
        var updatedMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value
        var isVideoUpdated = false
        var isRecordingVideoSuccess = true
    }

    override suspend fun disconnectCamera(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun getBatteryLevel(): Result<Int> {
        return MockUtils.progressBatteryCamera
    }

    override suspend fun getBodyWornDiagnosis(): Result<Boolean> {
        return bodyWornDiagnosisResult
    }

    override suspend fun getCameraType(): Result<CameraType> {
        if (MockUtils.cameraSSID == TestLoginData.SSID_X1.value) {
            return Result.Success(CameraType.X1)
        }
        return Result.Success(CameraType.X2)
    }

    override fun getCanReadNotification(): Boolean {
        return true
    }

    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> {
        return Result.Error(mockk())
    }

    override suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo> {
        return Result.Success(VideoFileInfo(0, 1000, 100, "", "10", 10, "", ""))
    }

    override suspend fun getListOfAudios(): Result<FileResponseWithErrors> {
        FileList.audioList = emptyList()
        return Result.Success(audioList)
    }

    override suspend fun getListOfImages(): Result<FileResponseWithErrors> {
        FileList.imageList = emptyList()
        return Result.Success(snapshotsList)
    }

    override suspend fun getListOfVideos(): Result<FileResponseWithErrors> {
        FileList.videoList = emptyList()
        return Result.Success(videoList)
    }

    override suspend fun getLogEvents(): Result<List<LogEvent>> {
        return Result.Success(eventList)
    }

    override suspend fun getMetadataOfPhotos(): Result<List<PhotoInformation>> {
        return Result.Success(emptyList())
    }

    override suspend fun getPhotoMetadata(cameraFile: CameraFile): Result<PhotoInformation> {
        return Result.Success(PhotoInformation("", ""))
    }

    override fun getUrlForLiveStream(): String = ""

    override suspend fun getUserResponse(): Result<CameraUser> {
        return Result.Success(
            CameraUser(
                TestLoginData.OFFICER_PASSWORD.value,
                TestLoginData.OFFICER_NAME.value,
                "dZnvtiaAwPk/xx/OrSx0p7TLhGs48Uc0g7seZR7ej/4=" // Hashed value for 'san 6279!' password
            )
        )
    }

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<VideoInformation> {

        if (isVideoUpdated) {
            return Result.Success(
                VideoInformation(
                    fileName, "kmenesesp", "/DCIM/", folderName, "X57",
                    with(updatedMetadata) {
                        VideoMetadata(
                            this.metadata?.caseNumber,
                            this.metadata?.caseNumber2,
                            this.metadata?.dispatchNumber,
                            this.metadata?.dispatchNumber2,
                            this.metadata?.driverLicense,
                            this.metadata?.licensePlate,
                            this.metadata?.event,
                            this.metadata?.firstName,
                            this.metadata?.gender,
                            this.metadata?.lastName,
                            this.metadata?.location,
                            this.metadata?.partnerID,
                            this.metadata?.race,
                            this.metadata?.remarks,
                            this.metadata?.ticketNumber,
                            this.metadata?.ticketNumber2,
                        )
                    },
                    null
                )
            )
        }
        return Result.Success(
            VideoInformation(
                fileName,
                "kmenesesp",
                "/DCIM/",
                folderName,
                "X57",
                VideoMetadata(
                    "1234",
                    "1234",
                    "1234",
                    "1234",
                    "1234",
                    "1234",
                    null,
                    "John",
                    null,
                    "Copeland",
                    "Miami",
                    "1234",
                    null,
                    "1234",
                    "1234",
                    "1234"
                ),
                null
            )
        )
    }

    override fun isCameraConnected(gatewayConnection: String): Boolean = true

    override suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String) {
        progressPairingCamera?.invoke(result)
    }

    override fun reviewIfArriveNotificationInCMDSocket() = Unit

    override suspend fun isRecording(): Boolean = false

    override suspend fun resetViewFinder(ipAddressClient: String): Boolean = true

    override suspend fun saveAllPhotoMetadata(list: List<PhotoInformation>): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun savePhotoMetadata(photoInformation: PhotoInformation): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun setSetupConfiguration(setupConfiguration: SetupConfiguration): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        if (isRecordingVideoSuccess) {
            videoList.items.add(CameraFilesData.EXTRA_VIDEO_LIST.value.items[takenVideos])
            if (takenVideos < CameraFilesData.EXTRA_VIDEO_LIST.value.items.size)
                takenVideos++
            else
                takenVideos = 0
            return Result.Success(Unit)
        }

        return Result.Error(mockk())
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun takePhoto(): Result<Unit> {
        snapshotsList.items.add(CameraFilesData.EXTRA_SNAPSHOT_LIST.value.items[takenPhotos])
        if (takenPhotos < CameraFilesData.EXTRA_SNAPSHOT_LIST.value.items.size)
            takenPhotos++
        else
            takenPhotos = 0
        return Result.Success(Unit)
    }

    override suspend fun getCatalogInfo(): Result<List<CameraCatalog>> {
        return Result.Success(
            listOf(
                CameraCatalog("1", "Default", "Event"),
                CameraCatalog("2", "Disk Clean", "Event"),
                CameraCatalog("3", "Jenn Main", "Event"),
                CameraCatalog("1", "Male", "Gender"),
                CameraCatalog("2", "Female", "Gender"),
                CameraCatalog("1", "White", "Race"),
                CameraCatalog("2", "Black", "Race")
            )
        )
    }

    override suspend fun getFreeStorage(): Result<String> {
        return Result.Success(MockUtils.freeStorageCamera.toString())
    }

    override suspend fun getTotalStorage(): Result<String> {
        return Result.Success(MockUtils.totalStorageCamera.toString())
    }

    fun setMaxTimeSession(time: Int) {}

    fun sendPushNotification(notificationResponse: NotificationResponse) {
        arriveNotificationFromCamera?.invoke(notificationResponse)
    }

    fun setIsVideoUpdated(value: Boolean) {
        isVideoUpdated = value
    }

    fun setUpdatedMetadata(value: VideoInformation) {
        updatedMetadata = value
    }

    fun setIsRecordingVideoSuccess(value: Boolean) {
        isRecordingVideoSuccess = value
    }

    override suspend fun getAudioBytes(cameraFile: CameraFile): Result<ByteArray> {
        return Result.Error(mockk())
    }

    override suspend fun getAudioMetadata(cameraFile: CameraFile): Result<AudioInformation> {
        return Result.Error(mockk())
    }

    override suspend fun saveAudioMetadata(audioInformation: AudioInformation): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun getAssociatedVideos(cameraFile: CameraFile): Result<List<CameraFile>> {
        return Result.Success(listOf(cameraFile))
    }
}
