package com.lawmobile.data.entities

import com.lawmobile.domain.entities.DomainVideoMetadata

data class RemoteVideoMetadata(
    var videoMetadata: DomainVideoMetadata,
    var isChanged: Boolean
)