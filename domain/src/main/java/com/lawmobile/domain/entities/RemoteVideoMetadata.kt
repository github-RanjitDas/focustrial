package com.lawmobile.domain.entities

data class RemoteVideoMetadata(
    var videoMetadata: DomainVideoMetadata,
    var isChanged: Boolean
)
