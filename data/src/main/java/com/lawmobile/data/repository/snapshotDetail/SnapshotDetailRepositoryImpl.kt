package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.data.mappers.PhotoMetadataMapper
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class SnapshotDetailRepositoryImpl(private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource) :
    SnapshotDetailRepository {

    override suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<ByteArray> {
        val cameraConnectFile = FileMapper.domainToCamera(domainCameraFile)
        return snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile)
    }

    override suspend fun saveSnapshotPartnerId(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit> {
        val photoMetadataList = mutableListOf<PhotoInformation>()

        when (val photosMetadataResult = snapshotDetailRemoteDataSource.getSavedPhotosMetadata()) {
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

        delay(1000)

        photoMetadataList.removeAll { it.fileName == domainCameraFile.name }
        photoMetadataList.add(cameraPhotoMetadata)

        when (val saveResult = snapshotDetailRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)) {
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

        delay(1000)

        return snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(photoMetadataList)
    }

    override suspend fun getInformationOfPhoto(domainCameraFile: DomainCameraFile): Result<DomainInformationImageMetadata> {
        val item = FileList.getMetadataOfImageInList(domainCameraFile.name)
        if (!thereIsErrorInMetadataVideo && item != null) return Result.Success(item)

        val cameraConnectFile = FileMapper.domainToCamera(domainCameraFile)

        with(snapshotDetailRemoteDataSource.getInformationOfPhoto(cameraConnectFile)) {
            doIfSuccess {
                val domainPhotoMetadata = PhotoMetadataMapper.cameraToDomain(it)
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
    }
}
