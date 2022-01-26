package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toCamera
import com.lawmobile.data.mappers.impl.PhotoMetadataMapper.toDomain
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.delay

class SnapshotDetailRepositoryImpl(
    private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource
) : SnapshotDetailRepository {

    override suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<ByteArray> {
        val cameraConnectFile = domainCameraFile.toCamera()
        return snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile)
    }

    override suspend fun saveSnapshotPartnerId(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit> {
        val photoMetadataList = mutableListOf<PhotoInformation>()

        val photosMetadataResult = getResultWithAttempts(ATTEMPTS_ON_OPERATION) {
            snapshotDetailRemoteDataSource.getSavedPhotosMetadata()
        }

        when (photosMetadataResult) {
            is Result.Success -> photoMetadataList.addAll(photosMetadataResult.data)
            is Result.Error -> return photosMetadataResult
        }

        val partnerMetadata = PhotoMetadata(partnerID = partnerId)
        val cameraPhotoMetadata = PhotoInformation(
            fileName = domainCameraFile.name,
            officerId = CameraInfo.officerId,
            path = domainCameraFile.path,
            x1sn = CameraInfo.serialNumber,
            metadata = partnerMetadata,
            nameFolder = domainCameraFile.nameFolder
        )

        delay(DELAY_BETWEEN_OPERATION)

        photoMetadataList.removeAll { it.fileName == domainCameraFile.name }
        photoMetadataList.add(cameraPhotoMetadata)

        val saveResult = getResultWithAttempts(ATTEMPTS_ON_OPERATION) {
            snapshotDetailRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)
        }

        when (saveResult) {
            is Result.Success -> {
                val item = FileList.getMetadataOfImageInList(domainCameraFile.name)
                val domainPhotoMetadata =
                    PhotoMetadataMapper.cameraToDomain(cameraPhotoMetadata)
                val newItemPhoto =
                    DomainInformationImageMetadata(domainPhotoMetadata, item?.videosAssociated)
                FileList.updateItemInImageMetadataList(newItemPhoto)
            }
            is Result.Error -> return saveResult
        }

        delay(DELAY_BETWEEN_OPERATION)

        return getResultWithAttempts(ATTEMPTS_ON_OPERATION) {
            snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(photoMetadataList)
        }
    }

    override suspend fun getInformationOfPhoto(domainCameraFile: DomainCameraFile): Result<DomainInformationImageMetadata> {
        val item = FileList.findAndGetImageMetadata(domainCameraFile.name)
        if (item != null) return Result.Success(item)

        val cameraFile = domainCameraFile.toCamera()

        snapshotDetailRemoteDataSource.getInformationOfPhoto(cameraFile).run {
            doIfSuccess {
                val domainPhotoMetadata = it.toDomain()
                val domainInformationImage =
                    DomainInformationImageMetadata(domainPhotoMetadata, emptyList())

                FileList.updateItemInImageMetadataList(domainInformationImage)
                return Result.Success(domainInformationImage)
            }
        }

        return Result.Error(Exception("Was not possible get information from the camera"))
    }

    companion object {
        private var thereIsErrorInMetadataVideo = false
        private const val ATTEMPTS_ON_OPERATION = 3
        private const val DELAY_BETWEEN_OPERATION = 200L
    }
}
