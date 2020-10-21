package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.PhotoMetadata
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import com.safefleet.mobile.commons.helpers.getResultWithAttempts
import kotlinx.coroutines.delay

class SnapshotDetailRepositoryImpl(private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource) :
    SnapshotDetailRepository {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile)
    }

    override suspend fun saveSnapshotPartnerId(
        cameraFile: CameraConnectFile,
        partnerId: String
    ): Result<Unit> {
        val itemsFinal = mutableListOf<CameraConnectPhotoMetadata>()
        val listPhotosSaved = ArrayList<CameraConnectPhotoMetadata>()
        val resultGetMetadataOfPhotos = snapshotDetailRemoteDataSource.getSavedPhotosMetadata()
        if (resultGetMetadataOfPhotos is Result.Error) {
            return resultGetMetadataOfPhotos
        }

        listPhotosSaved.addAll((resultGetMetadataOfPhotos as Result.Success).data)
        itemsFinal.addAll(listPhotosSaved)
        val errorsInFiles = ArrayList<String>()

        itemsFinal.removeAll { it.fileName == cameraFile.name }
        val partnerMetadata = PhotoMetadata(partnerID = partnerId)
        val cameraPhotoMetadata = CameraConnectPhotoMetadata(
            fileName = cameraFile.name,
            officerId = CameraInfo.officerId,
            path = cameraFile.path,
            x1sn = CameraInfo.serialNumber,
            metadata = partnerMetadata,
            nameFolder = cameraFile.nameFolder
        )

        delay(150)
        val resultPartnerOnly =
            snapshotDetailRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)
        itemsFinal.removeAll { it.fileName == cameraFile.name }
        itemsFinal.add(cameraPhotoMetadata)
        if (resultPartnerOnly is Result.Error) {
            errorsInFiles.add(cameraPhotoMetadata.fileName)
        } else {
            val item = FileList.getItemInListImageOfMetadata(cameraFile.name)
            val newItemPhoto =
                DomainInformationImageMetadata(cameraPhotoMetadata, item?.videosAssociated)
            FileList.updateItemInListImageMetadata(newItemPhoto)
        }

        delay(300)
        val resultJSONOnly = snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(itemsFinal)
        resultJSONOnly.doIfSuccess {
            return if (errorsInFiles.isEmpty()) Result.Success(Unit)
            else Result.Error(java.lang.Exception("Partner ID could not be associated to: $errorsInFiles"))
        }

        return Result.Error(java.lang.Exception("Partner ID could not be associated"))
    }

    override suspend fun getInformationOfPhoto(cameraFile: CameraConnectFile): Result<DomainInformationImageMetadata> {


        val item = FileList.getItemInListImageOfMetadata(cameraFile.name)
        if (!thereAreErrorInMetadataVideo  && item != null) {
            return Result.Success(item)
        }

        val response = snapshotDetailRemoteDataSource.getInformationOfPhoto(cameraFile)

        response.doIfSuccess {
            delay(350)
            val responseMetadataVideos = updateVideosMetadata()
            thereAreErrorInMetadataVideo = responseMetadataVideos is Result.Error
            responseMetadataVideos.doIfError { exceptionMetadata ->
                return Result.Error(exceptionMetadata)
            }

            val videosAssociated =
                VideoListMetadata.metadataList.map { remote -> remote.videoMetadata }
                    .filter { metadata ->
                        metadata.photos?.find { photo -> photo.name == cameraFile.name } != null
                    }

            val domainInformation = DomainInformationImageMetadata(it, videosAssociated)
            FileList.updateItemInListImageMetadata(domainInformation)
            return Result.Success(domainInformation)
        }

        return Result.Error(Exception("Was not possible get information from the camera"))

    }

    private suspend fun updateVideosMetadata(): Result<Unit> {
        val videoList = snapshotDetailRemoteDataSource.getVideoList()
        videoList.doIfSuccess { response ->
            response.items.forEach { cameraConnectFile ->
                delay(350)
                val responseMetadata = getResultWithAttempts(ATTEMPTS_TO_GET_METADATA) {
                    snapshotDetailRemoteDataSource.getMetadataOfVideo(cameraConnectFile)
                }

                responseMetadata.doIfSuccess { metadata ->
                    VideoListMetadata.saveOrUpdateVideoMetadata(
                        RemoteVideoMetadata(
                            metadata,
                            false
                        )
                    )
                }

                responseMetadata.doIfError {
                    return Result.Error(Exception("Error in get information of:${cameraConnectFile.name}"))
                }
            }
        }

        videoList.doIfError {
            return Result.Error(Exception("Error in get videoList"))
        }

        return Result.Success(Unit)
    }

    companion object {
        const val ATTEMPTS_TO_GET_METADATA = 5
        private var thereAreErrorInMetadataVideo = false
    }
}