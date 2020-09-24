package com.lawmobile.presentation.entities

import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.extensions.getCreationDate
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated

object SnapshotsAssociatedByUser {
    var value = mutableListOf<PhotoAssociated>()

    fun updateAssociatedSnapshots(imageFile: CameraConnectFile) {
        with(imageFile) {
            val result =
                value.find { it.name == name }
            if (result == null) {
                value.add(PhotoAssociated(name, getCreationDate()))
            } else {
                value.remove(result)
            }
        }
    }

    fun getListOfImagesAssociatedToVideo(imageList: List<DomainInformationForList>): List<DomainInformationForList> =
        filterPhotoSnapshotsAssociatedSelected(imageList)

    private fun filterPhotoSnapshotsAssociatedSelected(imageList: List<DomainInformationForList>): List<DomainInformationForList> {
        val completeList: MutableList<DomainInformationForList> = imageList as MutableList
        value.map { it.name }.forEach { image ->
            val index = completeList.indexOfFirst { it.cameraConnectFile.name == image }
            if (index >= 0) completeList[index].isSelected = true
        }
        return completeList
    }

    fun cleanList() {
        value = mutableListOf()
    }
}