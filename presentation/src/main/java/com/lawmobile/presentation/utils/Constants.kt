package com.lawmobile.presentation.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val FILE_LIST_SELECTOR = "fileListSelector"
    const val SIMPLE_FILE_LIST = "simpleFileList"
    const val THUMBNAIL_FILE_LIST = "thumbnailFileList"
    const val FILE_LIST_TYPE = "fileListType"
    const val VIDEO_LIST = "videoList"
    const val SNAPSHOT_LIST = "snapshotList"
    const val AUDIO_LIST = "audioList"
    const val DOMAIN_CAMERA_FILE = "domainCameraFile"
    val ON_BOARDING_DISPLAYED = booleanPreferencesKey("Was On Boarding Displayed")
    val CAMERA_TYPE = stringPreferencesKey("Camera Type")
}
