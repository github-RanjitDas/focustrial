package com.safefleet.lawmobile.testData

import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata

enum class VideoPlaybackMetadata(val value: CameraConnectVideoMetadata) {
    DEFAULT_VIDEO_METADATA(
        CameraConnectVideoMetadata(
            "",
            "murbanob",
            "",
            "",
            "X57",
            VideoMetadata(
                "1234",
                "4321",
                "1234",
                "4321",
                "1234",
                "4321",
                CameraConnectCatalog("1", "Default", "Event"),
                "Pepe",
                "Male",
                "Pepeto",
                "1234",
                "4321",
                "Black",
                "4321",
                "1234",
                "4321"
            )
        )
    ),
    EXTRA_VIDEO_METADATA(
        CameraConnectVideoMetadata(
            "",
            "scardonau",
            "",
            "",
            "X57",
            VideoMetadata(
                "1254",
                "4621",
                "1235",
                "4351",
                "1235",
                "4351",
                CameraConnectCatalog("2", "Disk Clean", "Event"),
                "Marta",
                "Female",
                "Maria",
                "1254",
                "4521",
                "White",
                "4351",
                "1254",
                "5555"
            )
        )
    )
}