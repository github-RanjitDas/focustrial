package com.lawmobile.body_cameras.enums

enum class XCameraCommandTypes(val value: String) {
    APP_STATUS("app_status"),
    TOTAL_SPACE("total"),
    FREE_SPACE("free"),
    PHOTO_COUNT("photo"),
    VIDEO_COUNT("video"),
    FILES_COUNT("total"),
    CLIENT_CONNECTION_TCP("TCP")
}
