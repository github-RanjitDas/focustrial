package com.lawmobile.domain.entities

data class DomainInformationFileResponse(
    var items: MutableList<DomainInformationFile>,
    val errors: MutableList<String>
)
