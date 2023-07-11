package net.safefleet.focus.testData

import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.entities.VideoMetadata

enum class VideoPlaybackMetadata(val value: VideoInformation) {
    DEFAULT_VIDEO_METADATA(
        VideoInformation(
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
                CameraCatalog("1", "Default", "Event"),
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
        VideoInformation(
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
                CameraCatalog("2", "Disk Clean", "Event"),
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
