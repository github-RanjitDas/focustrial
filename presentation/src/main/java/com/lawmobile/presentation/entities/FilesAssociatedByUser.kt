package com.lawmobile.presentation.entities

import com.lawmobile.domain.entities.DomainAssociatedFile
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.extensions.getDateDependingOnNameLength

object FilesAssociatedByUser {

    var value = mutableListOf<DomainAssociatedFile>()
        private set

    var temporal = mutableListOf<DomainAssociatedFile>()
        private set

    fun setFinalValue(mutableList: MutableList<DomainAssociatedFile>) {
        value = mutableListOf()
        value.addAll(mutableList)
    }

    fun setTemporalValue(mutableList: MutableList<DomainAssociatedFile>) {
        temporal = mutableListOf()
        temporal.addAll(mutableList)
    }

    fun updateAssociatedSnapshots(imageFile: DomainCameraFile) {
        with(imageFile) {
            val result = temporal.find { it.name == name }
            if (result == null) {
                temporal.add(
                    DomainAssociatedFile(
                        name = name,
                        date = getDateDependingOnNameLength()
                    )
                )
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
