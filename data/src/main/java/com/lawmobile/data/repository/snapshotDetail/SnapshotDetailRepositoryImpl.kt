package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.data.mappers.PhotoMetadataMapper
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoMetadata
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
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
        val errorsInFiles = mutableListOf<String>()

        when (val resultGetMetadataOfPhotos =
            snapshotDetailRemoteDataSource.getSavedPhotosMetadata()) {
            is Result.Success -> photoMetadataList.addAll(resultGetMetadataOfPhotos.data)
            is Result.Error -> return resultGetMetadataOfPhotos
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

        delay(150)

        photoMetadataList.removeAll { it.fileName == domainCameraFile.name }
        photoMetadataList.add(cameraPhotoMetadata)

        with(snapshotDetailRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)) {
            doIfSuccess {
                val item = FileList.getMetadataOfImageInList(domainCameraFile.name)
                val domainPhotoMetadata =
                    PhotoMetadataMapper.cameraToDomain(cameraPhotoMetadata)
                val newItemPhoto =
                    DomainInformationImageMetadata(domainPhotoMetadata, item?.videosAssociated)
                FileList.updateItemInImageMetadataList(newItemPhoto)
            }
            doIfError {
                errorsInFiles.add(cameraPhotoMetadata.fileName)
            }
        }

        delay(300)

        with(snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(photoMetadataList)) {
            doIfSuccess {
                return if (errorsInFiles.isEmpty()) Result.Success(Unit)
                else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
            }
        }

        return Result.Error(Exception("Partner ID could not be associated"))
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