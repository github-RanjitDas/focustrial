package com.lawmobile.presentation.entities

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.entities.DomainPhotoAssociated
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class SnapshotsAssociatedByUserTest {

    @Test
    fun setOrGetValue() {
        val newList = mutableListOf<DomainPhotoAssociated>()
        SnapshotsAssociatedByUser.setFinalValue(newList)
        assert(newList == SnapshotsAssociatedByUser.value)
    }

    @Test
    fun updateAssociatedSnapshotsAdd() {
        val domainCameraFile = mockk<DomainCameraFile>(relaxed = true) {
            every { name } returns "4567890"
            every { nameFolder } returns "4567890123"
        }
        val photoList = mutableListOf(DomainPhotoAssociated("123", "123"))
        SnapshotsAssociatedByUser.setTemporalValue(photoList)
        SnapshotsAssociatedByUser.updateAssociatedSnapshots(domainCameraFile)
        assert(SnapshotsAssociatedByUser.temporal.size == 2)
    }

    @Test
    fun updateAssociatedSnapshotsRemove() {
        val domainCameraFile = mockk<DomainCameraFile>(relaxed = true) {
            every { name } returns "123"
            every { date } returns "123"
        }
        val photoList = mutableListOf(DomainPhotoAssociated("123", "123"))
        SnapshotsAssociatedByUser.setTemporalValue(photoList)
        SnapshotsAssociatedByUser.updateAssociatedSnapshots(domainCameraFile)
        assert(SnapshotsAssociatedByUser.temporal.size == 0)
    }

    @Test
    fun getImagesAssociatedToVideoListThumbnails() {
        val domainInformationImageList =
            mutableListOf(
                DomainInformationImage(
                    mockk {
                        every { name } returns "123"
                    }
                ),
                DomainInformationImage(
                    mockk {
                        every { name } returns "456"
                    }
                )
            )
        SnapshotsAssociatedByUser.setTemporalValue(
            mutableListOf(DomainPhotoAssociated("123", "123"))
        )
        val associatedList =
            SnapshotsAssociatedByUser
                .getListOfImagesAssociatedToVideo(domainInformationImageList).find { it.isSelected }
        assert(associatedList != null)
    }

    @Test
    fun getImagesAssociatedToVideoListSimple() {
        val domainInformationImageList =
            mutableListOf(
                DomainInformationFile(
                    mockk {
                        every { name } returns "123"
                    }
                ),
                DomainInformationFile(
                    mockk {
                        every { name } returns "456"
                    }
                )
            )
        SnapshotsAssociatedByUser.setTemporalValue(
            mutableListOf(DomainPhotoAssociated("123", "123"))
        )
        val associatedList =
            SnapshotsAssociatedByUser
                .getListOfImagesAssociatedToVideo(domainInformationImageList).find { it.isSelected }
        assert(associatedList != null)
    }

    @Test
    fun cleanList() {
        SnapshotsAssociatedByUser.setFinalValue(mutableListOf(mockk(), mockk()))
        SnapshotsAssociatedByUser.cleanList()
        assert(SnapshotsAssociatedByUser.value.size == 0)
    }
}
