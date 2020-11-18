package com.lawmobile.domain.entities

import java.io.Serializable

data class DomainCameraFile (
    val date: String,
    val name: String,
    val nameFolder: String,
    val path: String
): Serializable