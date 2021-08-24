package com.lawmobile.data.mappers

import com.lawmobile.data.extensions.getDateDependingOnNameLength
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors

object FileResponseMapper {
    fun cameraToDomain(fileResponse: FileResponseWithErrors) =
        fileResponse.run {
            DomainInformationFileResponse(
                items.sortedByDescending { it.getDateDependingOnNameLength() }.map {
                    DomainInformationFile(FileMapper.cameraToDomain(it))
                } as MutableList,
                errors
            )
        }
}
