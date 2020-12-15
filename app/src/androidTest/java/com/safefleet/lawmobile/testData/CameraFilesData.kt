package com.safefleet.lawmobile.testData

import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile

enum class CameraFilesData(val value: FileResponseWithErrors) {
    DEFAULT_SNAPSHOT_LIST(
        FileResponseWithErrors().apply {
            errors.addAll(emptyList())
            items.addAll(
                listOf(
                    CameraFile(
                        name = "16334400.JPG",
                        date = "2020-05-20 16:33:44",
                        path = "/tmp/SD0/DCIM/200520001/",
                        nameFolder = "200520001/"
                    ),
                    CameraFile(
                        name = "16544400.JPG",
                        date = "2020-05-21 16:54:44",
                        path = "/tmp/SD0/DCIM/200521001/",
                        nameFolder = "200521001/"
                    ),
                    CameraFile(
                        name = "16545000.JPG",
                        date = "2020-05-21 16:54:50",
                        path = "/tmp/SD0/DCIM/200521001/",
                        nameFolder = "200521001/"
                    ),
                    CameraFile(
                        name = "16545600.JPG",
                        date = "2020-05-21 16:54:56",
                        path = "/tmp/SD0/DCIM/200521001/",
                        nameFolder = "200521001/"
                    ),
                    CameraFile(
                        name = "02443800.JPG",
                        date = "2016-01-01 02:44:38",
                        path = "/tmp/SD0/DCIM/160101000/",
                        nameFolder = "160101000/"
                    ),
                    CameraFile(
                        name = "17294400.JPG",
                        date = "2020-05-27 17:29:44",
                        path = "/tmp/SD0/DCIM/200527000/",
                        nameFolder = "200527000/"
                    ),
                    CameraFile(
                        name = "18082000.JPG",
                        date = "2020-05-27 18:08:20",
                        path = "/tmp/SD0/DCIM/200527000/",
                        nameFolder = "200527000/"
                    ),
                    CameraFile(
                        name = "10181900.JPG",
                        date = "2020-05-28 10:18:19",
                        path = "/tmp/SD0/DCIM/200528000/",
                        nameFolder = "200528000/"
                    ),
                    CameraFile(
                        name = "13265000.JPG",
                        date = "2020-05-28 13:26:50",
                        path = "/tmp/SD0/DCIM/200528001/",
                        nameFolder = "200528001/"
                    ),
                    CameraFile(
                        name = "15204300.JPG",
                        date = "2020-06-01 15:20:43",
                        path = "/tmp/SD0/DCIM/200601000/",
                        nameFolder = "200601000/"
                    ),
                    CameraFile(
                        name = "11020700.JPG",
                        date = "2020-06-02 11:02:07",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11020900.JPG",
                        date = "2020-06-02 11:02:09",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11021000.JPG",
                        date = "2020-06-02 11:02:10",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11021800.JPG",
                        date = "2020-06-02 11:02:18",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11022100.JPG",
                        date = "2020-06-02 11:02:21",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    )
                )
            )
        }
    ),
    EXTRA_SNAPSHOT_LIST(
        FileResponseWithErrors().apply {
            errors.addAll(emptyList())
            items.addAll(
                listOf(
                    CameraFile(
                        name = "11201800.JPG",
                        date = "2020-06-03 11:20:18",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "15022100.JPG",
                        date = "2020-06-03 15:02:21",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "17032100.JPG",
                        date = "2020-06-03 17:03:21",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "20032100.JPG",
                        date = "2020-06-03 20:03:21",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "21101100.JPG",
                        date = "2020-06-03 21:10:11",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    )
                )
            )
        }
    ),
    DEFAULT_VIDEO_LIST(
        FileResponseWithErrors().apply {
            errors.addAll(emptyList())
            items.addAll(
                listOf(
                    CameraFile(
                        name = "16334400AA.MP4",
                        date = "2020-05-20 16:33:44",
                        path = "/tmp/SD0/DCIM/200520001/",
                        nameFolder = "200520001/"
                    ),
                    CameraFile(
                        name = "16544400AA.MP4",
                        date = "2020-05-21 16:54:44",
                        path = "/tmp/SD0/DCIM/200521001/",
                        nameFolder = "200521001/"
                    ),
                    CameraFile(
                        name = "16545000AA.MP4",
                        date = "2020-05-21 16:54:50",
                        path = "/tmp/SD0/DCIM/200521001/",
                        nameFolder = "200521001/"
                    ),
                    CameraFile(
                        name = "16545600AA.MP4",
                        date = "2020-05-21 16:54:56",
                        path = "/tmp/SD0/DCIM/200521001/",
                        nameFolder = "200521001/"
                    ),
                    CameraFile(
                        name = "02443800AA.MP4",
                        date = "2016-01-01 02:44:38",
                        path = "/tmp/SD0/DCIM/160101000/",
                        nameFolder = "160101000/"
                    ),
                    CameraFile(
                        name = "17294400AA.MP4",
                        date = "2020-05-27 17:29:44",
                        path = "/tmp/SD0/DCIM/200527000/",
                        nameFolder = "200527000/"
                    ),
                    CameraFile(
                        name = "18082000AA.MP4",
                        date = "2020-05-27 18:08:20",
                        path = "/tmp/SD0/DCIM/200527000/",
                        nameFolder = "200527000/"
                    ),
                    CameraFile(
                        name = "10181900AA.MP4",
                        date = "2020-05-28 10:18:19",
                        path = "/tmp/SD0/DCIM/200528000/",
                        nameFolder = "200528000/"
                    ),
                    CameraFile(
                        name = "13265000AA.MP4",
                        date = "2020-05-28 13:26:50",
                        path = "/tmp/SD0/DCIM/200528001/",
                        nameFolder = "200528001/"
                    ),
                    CameraFile(
                        name = "15204300AA.MP4",
                        date = "2020-06-01 15:20:43",
                        path = "/tmp/SD0/DCIM/200601000/",
                        nameFolder = "200601000/"
                    ),
                    CameraFile(
                        name = "11020700AA.MP4",
                        date = "2020-06-02 11:02:07",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11020900AA.MP4",
                        date = "2020-06-02 11:02:09",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11021000AA.MP4",
                        date = "2020-06-02 11:02:10",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11021800AA.MP4",
                        date = "2020-06-02 11:02:18",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    ),
                    CameraFile(
                        name = "11022100AA.MP4",
                        date = "2020-06-02 11:02:21",
                        path = "/tmp/SD0/DCIM/200602000/",
                        nameFolder = "200602000/"
                    )
                )
            )
        }
    ),
    EXTRA_VIDEO_LIST(
        FileResponseWithErrors().apply {
            errors.addAll(emptyList())
            items.addAll(
                listOf(
                    CameraFile(
                        name = "11201800AA.MP4",
                        date = "2020-06-03 11:20:18",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "15022100AA.MP4",
                        date = "2020-06-03 15:02:21",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "17032100AA.MP4",
                        date = "2020-06-03 17:03:21",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "20032100AA.MP4",
                        date = "2020-06-03 20:03:21",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    ),
                    CameraFile(
                        name = "21101100AA.MP4",
                        date = "2020-06-03 21:10:11",
                        path = "/tmp/SD0/DCIM/200603000/",
                        nameFolder = "200603000/"
                    )
                )
            )
        }
    )
}
