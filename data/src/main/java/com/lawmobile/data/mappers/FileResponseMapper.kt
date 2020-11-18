package com.lawmobile.data.mappers

import com.lawmobile.data.extensions.getCreationDate
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors

object FileResponseMapper {
    fun cameraToDomain(cameraConnectFileResponse: CameraConnectFileResponseWithErrors) =
        cameraConnectFileResponse.run {
            DomainInformationFileResponse(
                items.sortedByDescending { it.getCreationDate() }.map {
                    DomainInformationFile(FileMapper.cameraToDomain(it))
                } as MutableList,
                errors
            )
        }
}