package com.safefleet.lawmobile.di.mocksServiceCameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.testData.TestLoginData
import com.safefleet.mobile.avml.cameras.entities.*
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.mockk

class CameraConnectServiceX1Mock : CameraConnectService {
    private val progressPairingCameraMediator: MediatorLiveData<Result<Int>> = MediatorLiveData()
    override val progressPairingCamera: LiveData<Result<Int>>
        get() = progressPairingCameraMediator

    companion object {
        var snapshotsList = CameraFilesData.DEFAULT_SNAPSHOTS_LIST.value.toMutableList()
        var takenPhotos = 0
    }

    override suspend fun disconnectCamera(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        val bytes = ByteArray(2)
        return Result.Success(bytes)
    }

    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoInfo> {
        return Result.Success(CameraConnectVideoInfo(0, 1000, 100, "", "10", 10, "", ""))
    }

    override suspend fun getListOfImages(): Result<CameraConnectFileResponseWithErrors> {
        val cameraConnectFileResponseWithErrors = CameraConnectFileResponseWithErrors()
        cameraConnectFileResponseWithErrors.items.addAll(snapshotsList)
        return Result.Success(cameraConnectFileResponseWithErrors)
    }

    override suspend fun getListOfVideos(): Result<CameraConnectFileResponseWithErrors> {
        val cameraConnectFileResponseWithErrors = CameraConnectFileResponseWithErrors()
        cameraConnectFileResponseWithErrors.items.addAll(arrayListOf())
        return Result.Success(cameraConnectFileResponseWithErrors)
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
        return Result.Success(mockk())
    }

    override fun isCameraConnected(gatewayConnection: String): Boolean = true

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
        return Result.Success(Unit)
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun takePhoto(): Result<Unit> {
        snapshotsList.add(CameraFilesData.EXTRA_SNAPSHOTS_LIST.value[takenPhotos])
        takenPhotos++
        return Result.Success(Unit)
    }

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> {
        return Result.Success(emptyList())
    }
}
