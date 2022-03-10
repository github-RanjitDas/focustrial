package com.lawmobile.body_cameras.x1

import com.lawmobile.body_cameras.CameraServiceImpl
import com.lawmobile.body_cameras.utils.CommandHelper
import com.lawmobile.body_cameras.utils.FileInformationHelper
import com.lawmobile.body_cameras.utils.MetadataHelper

class X1CameraServiceImpl(
    fileInformationHelper: FileInformationHelper,
    commandHelper: CommandHelper,
    metadataHelper: MetadataHelper
) : CameraServiceImpl(
    fileInformationHelper = fileInformationHelper,
    commandHelper = commandHelper,
    metadataHelper = metadataHelper
)
