package com.safefleet.lawmobile.di.mocksServiceCameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.safefleet.lawmobile.TestData
import com.safefleet.mobile.avml.cameras.entities.*
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.mockk

class CameraConnectServiceX1Mock : CameraConnectService {
    private val progressPairingCameraMediator: MediatorLiveData<Result<Int>> = MediatorLiveData()
    override val progressPairingCamera: LiveData<Result<Int>>
        get() = progressPairingCameraMediator

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

    override suspend fun getListOfImages(): Result<List<CameraConnectFile>> {
        return Result.Success(emptyList())
    }

    override suspend fun getListOfVideos(): Result<List<CameraConnectFile>> {
        return Result.Success(emptyList())
    }

    override fun getUrlForLiveStream(): String = ""

    override suspend fun getUserResponse(): Result<CameraConnectUserResponse> {
        return Result.Success(
            CameraConnectUserResponse(
                TestData.OFFICER_PASSWORD.value,
                TestData.OFFICER_NAME.value,
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
        return Result.Success(Unit)
    }

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> {
        return Result.Success(emptyList())
    }
}
