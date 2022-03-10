package com.lawmobile.body_cameras.constants

object CameraConstants {
    const val PATH_DOWNLOAD_OFFICER_INFO = "/tmp/SD0/OfficerPassword.txt"
    const val PATH_DOWNLOAD_CATALOGS = "/tmp/SD0/PreviewEvents.txt"
    const val PATH_VERSION = "/tmp/SD0/version.json"
    const val PROTOCOL_LIVE = "rtsp://"
    const val COMPLEMENT_LIVE = "/live"
    const val FILES_MAIN_PATH_FOLDER = "/tmp/SD0/DCIM/"
    const val FILES_MAIN_PATH = "/tmp/SD0/"
    const val NAME_SETUP_CONFIGURATION = "WiFi_setup.json"
    const val FILES_MISC_PATH_FOLDER = "/tmp/SD0/MISC/"
    const val PHOTO_NAME_JSON = "photometadata.json"
    const val PROGRESS_CONNECT_CAMERA = 20
    const val PROGRESS_SESSION_TOKEN = 40
    const val PROGRESS_LIVE_STREAM_INFO = 60
    const val PROGRESS_DEVICE_INFO = 80
    const val PROGRESS_CLIENT_INFO = 100
    const val ATTEMPTS_RETRY_REQUEST = 3
    const val NOTIFICATION_FILE_NAME = "warnings.log"
}
