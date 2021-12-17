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
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
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
        return try {
            val errorsInFiles = mutableListOf<String>()
            val cameraSnapshotMetadata = buildPhotoInformation(domainCameraFile, partnerId)
            savePartnerIdOnSnapshotMetadata(cameraSnapshotMetadata, errorsInFiles)
            val photoMetadataList = getAllSnapshotInformation(cameraSnapshotMetadata)
            Result.Success(updateAllSnapshotMetadata(photoMetadataList, errorsInFiles))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun updateAllSnapshotMetadata(
        photoMetadataList: MutableList<PhotoInformation>,
        errorsInFiles: MutableList<String>
    ) {
        snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(photoMetadataList).run {
            doIfSuccess {
                if (errorsInFiles.isEmpty()) return
                else throw Exception("Partner ID could not be associated to: $errorsInFiles")
            }
            doIfError { throw Exception("Partner ID could not be associated") }
        }
    }

    private suspend fun savePartnerIdOnSnapshotMetadata(
        cameraPhotoMetadata: PhotoInformation,
        errorsInFiles: MutableList<String>
    ) {
        snapshotDetailRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata).run {
            doIfSuccess { updateSnapshotMetadataInCache(cameraPhotoMetadata) }
            doIfError { errorsInFiles.add(cameraPhotoMetadata.fileName) }
        }

        delay(SAVE_SNAPSHOT_METADATA_DELAY)
    }

    private fun updateSnapshotMetadataInCache(
        cameraPhotoMetadata: PhotoInformation
    ) {
        val videosAssociated =
            FileList.findAndGetImageMetadata(cameraPhotoMetadata.fileName)?.videosAssociated
        val domainPhotoMetadata = cameraPhotoMetadata.toDomain()
        val newItemPhoto = DomainInformationImageMetadata(domainPhotoMetadata, videosAssociated)
        FileList.updateItemInImageMetadataList(newItemPhoto)
    }

    private suspend fun getAllSnapshotInformation(cameraPhotoMetadata: PhotoInformation): MutableList<PhotoInformation> {
        val photoMetadataList = mutableListOf<PhotoInformation>()

        snapshotDetailRemoteDataSource.getSavedPhotosMetadata().run {
            doIfSuccess { photoMetadataList.addAll(it) }
            doIfError { throw it }
        }

        delay(GET_ALL_SNAPSHOT_METADATA_DELAY)

        photoMetadataList.removeAll { it.fileName == cameraPhotoMetadata.fileName }
        photoMetadataList.add(cameraPhotoMetadata)
        return photoMetadataList
    }

    private fun buildPhotoInformation(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ) = PhotoInformation(
        fileName = domainCameraFile.name,
        officerId = CameraInfo.officerId,
        path = domainCameraFile.path,
        x1sn = CameraInfo.serialNumber,
        metadata = PhotoMetadata(partnerId),
        nameFolder = domainCameraFile.nameFolder
    )

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
        private const val SAVE_SNAPSHOT_METADATA_DELAY = 300L
        private const val GET_ALL_SNAPSHOT_METADATA_DELAY = 150L
    }
}
