package com.lawmobile.presentation.entities

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.domain.extensions.getCreationDate

object SnapshotsAssociatedByUser {
    var value = mutableListOf<DomainPhotoAssociated>()
    var temporal = mutableListOf<DomainPhotoAssociated>()

    fun updateAssociatedSnapshots(imageFile: DomainCameraFile) {
        with(imageFile) {
            val result = temporal.find { it.name == name }
            if (result == null) {
                temporal.add(DomainPhotoAssociated(name = name, date = getCreationDate()))
            } else {
                temporal.remove(result)
            }
        }
    }

    fun isImageAssociated(name: String): Boolean {
        return temporal.find { it.name == name } != null
    }

    fun getListOfImagesAssociatedToVideo(imageList: List<DomainInformationForList>): List<DomainInformationForList> =
        filterPhotoSnapshotsAssociatedSelected(imageList)

    private fun filterPhotoSnapshotsAssociatedSelected(imageList: List<DomainInformationForList>): List<DomainInformationForList> {
        val completeList: MutableList<DomainInformationForList> = imageList as MutableList
        temporal.map { it.name }.forEach { imageName ->
            val index = completeList.indexOfFirst { it.domainCameraFile.name == imageName }
            if (index >= 0) completeList[index].isSelected = true
        }
        return completeList
    }

    fun cleanList() {
        value = mutableListOf()
        temporal = mutableListOf()
    }
}