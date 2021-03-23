package com.lawmobile.domain.entities

data class DomainLogEvent(
    val name: String,
    val date: String,
    val type: String,
    val value: String = "",
    val additionalInformation: Any? = null
)
