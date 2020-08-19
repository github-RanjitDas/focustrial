package com.safefleet.lawmobile.di.mocksServiceCameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.lawmobile.data.entities.FileList
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.testData.TestLoginData
import com.safefleet.mobile.avml.cameras.entities.*
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class CameraConnectServiceX1Mock : CameraConnectService {
    private val progressPairingCameraMediator: MediatorLiveData<Result<Int>> = MediatorLiveData()
    override val progressPairingCamera: LiveData<Result<Int>>
        get() = progressPairingCameraMediator

    override suspend fun deleteFile(fileName: String): Result<Unit> {
        return Result.Success(Unit)
    }

    companion object {
        var snapshotsList = CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
        var videoList = CameraFilesData.DEFAULT_VIDEO_LIST.value
        var takenPhotos = 0
        var takenVideos = 0
    }

    override suspend fun disconnectCamera(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun getBatteryLevel(): Result<Int> {
        return Result.Success(10)
    }

    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        val bytes = byteArrayOf(1, 2, 3, 0, 17, 0, 34, 0, 9)
        return Result.Success(bytes)
    }

    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoInfo> {
        return Result.Success(CameraConnectVideoInfo(0, 1000, 100, "", "10", 10, "", ""))
    }

    override suspend fun getListOfImages(): Result<CameraConnectFileResponseWithErrors> {
        FileList.listOfImages = emptyList()
        return Result.Success(snapshotsList)
    }

    override suspend fun getListOfVideos(): Result<CameraConnectFileResponseWithErrors> {
        FileList.listOfVideos = emptyList()
        return Result.Success(videoList)
    }

    override suspend fun getNumberOfSnapshots(): Result<String> {
        return Result.Success("10")
    }

    override suspend fun getNumberOfVideos(): Result<String> {
        return Result.Success("10")
    }

    override suspend fun getTotalStorage(): Result<String> {
        return Result.Success("10")
    }

    override fun getUrlForLiveStream(): String = ""

    override suspend fun getUserResponse(): Result<CameraConnectUserResponse> {
        return Result.Success(
            CameraConnectUserResponse(
                TestLoginData.OFFICER_PASSWORD.value,
                TestLoginData.OFFICER_NAME.value,
                "dZnvtiaAwPk/xx/OrSx0p7TLhGs48Uc0g7seZR7ej/4=" //Hashed value for 'san 6279!' password
            )
        )
    }

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<CameraConnectVideoMetadata> {
        return Result.Success(
            CameraConnectVideoMetadata(
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
                    "1234",
                    null,
                    "1234",
                    "1234",
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
        progressPairingCameraMediator.postValue(Result.Success(100))
    }

    override suspend fun savePhotoMetadata(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun saveVideoMetadata(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        videoList.items.add(CameraFilesData.EXTRA_VIDEO_LIST.value.items[takenVideos])
        if (takenVideos < CameraFilesData.EXTRA_VIDEO_LIST.value.items.size)
            takenVideos++
        else
            takenVideos = 0
        return Result.Success(Unit)
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

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> {
        return Result.Success(listOf(CameraConnectCatalog("1", "Default", "Default")))
    }

    override suspend fun getFreeStorage(): Result<String> {
        return Result.Success("10")
    }
}
