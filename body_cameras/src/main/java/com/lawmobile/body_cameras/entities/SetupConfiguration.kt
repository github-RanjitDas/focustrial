package com.lawmobile.body_cameras.entities

data class SetupConfiguration(
    val hotspotName: String,
    val hotspotPassword: String,
    val discoveryUrl: String,
    val hotspotSecurityType: String = "",
    val tenantId: String? = null
)
