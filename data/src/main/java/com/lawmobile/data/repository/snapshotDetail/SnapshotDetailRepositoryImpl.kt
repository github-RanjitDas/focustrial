package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.PhotoMetadata
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfSuccess

class SnapshotDetailRepositoryImpl(private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource) :
    SnapshotDetailRepository {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile)
    }

    override suspend fun savePartnerIdSnapshot(
        cameraFile: CameraConnectFile,
        partnerId: String
    ): Result<Unit> {
        val partnerMetadata = PhotoMetadata(partnerID = partnerId)
        val cameraPhotoMetadata = CameraConnectPhotoMetadata(
            fileName = cameraFile.name,
            officerId = CameraInfo.officerId,
            path = cameraFile.path,
            x1sn = CameraInfo.serialNumber,
            metadata = partnerMetadata,
            nameFolder = cameraFile.nameFolder
        )
        val response = snapshotDetailRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)
        response.doIfSuccess {
            val newItemPhoto = DomainInformationImageMetadata(cameraPhotoMetadata)
            FileList.updateItemInListImageMetadata(newItemPhoto)
        }

        return response
    }

    override suspend fun getInformationOfPhoto(cameraFile: CameraConnectFile): Result<DomainInformationImageMetadata> {
        val item = FileList.getItemInListImageOfMetadata(cameraFile.name)
        item?.let { return Result.Success(item) }
        val response = snapshotDetailRemoteDataSource.getInformationOfPhoto(cameraFile)
        response.doIfSuccess {
            FileList.updateItemInListImageMetadata(DomainInformationImageMetadata(it))
            return Result.Success(DomainInformationImageMetadata(it))
        }

        return Result.Error(Exception("Was not possible get information from the camera"))
    }
}