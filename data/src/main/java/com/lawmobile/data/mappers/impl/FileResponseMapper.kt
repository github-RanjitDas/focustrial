package com.lawmobile.data.mappers.impl

import com.lawmobile.data.extensions.getDateDependingOnNameLength
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors

object FileResponseMapper : DomainMapper<FileResponseWithErrors, DomainInformationFileResponse> {
    override fun FileResponseWithErrors.toDomain(): DomainInformationFileResponse =
        DomainInformationFileResponse(
            items.sortedByDescending { it.getDateDependingOnNameLength() }.map {
                DomainInformationFile(it.toDomain())
            } as MutableList,
            errors
        )
}
