package com.lawmobile.domain.entities

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class FilesAssociatedByUserTest {

    @Test
    fun setOrGetValue() {
        val newList = mutableListOf<DomainAssociatedFile>()
        FilesAssociatedByUser.setFinalValue(newList)
        assert(newList == FilesAssociatedByUser.value)
    }

    @Test
    fun updateAssociatedSnapshotsAdd() {
        val domainCameraFile = mockk<DomainCameraFile>(relaxed = true) {
            every { name } returns "4567890"
            every { nameFolder } returns "4567890123"
        }
        val photoList = mutableListOf(DomainAssociatedFile("123", "123"))
        FilesAssociatedByUser.setTemporalValue(photoList)
        FilesAssociatedByUser.updateAssociatedSnapshots(domainCameraFile)
        assert(FilesAssociatedByUser.temporal.size == 2)
    }

    @Test
    fun updateAssociatedSnapshotsRemove() {
        val domainCameraFile = mockk<DomainCameraFile>(relaxed = true) {
            every { name } returns "123"
            every { date } returns "123"
        }
        val photoList = mutableListOf(DomainAssociatedFile("123", "123"))
        FilesAssociatedByUser.setTemporalValue(photoList)
        FilesAssociatedByUser.updateAssociatedSnapshots(domainCameraFile)
        assert(FilesAssociatedByUser.temporal.size == 0)
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
        FilesAssociatedByUser.setTemporalValue(
            mutableListOf(DomainAssociatedFile("123", "123"))
        )
        val associatedList =
            FilesAssociatedByUser
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
        FilesAssociatedByUser.setTemporalValue(
            mutableListOf(DomainAssociatedFile("123", "123"))
        )
        val associatedList =
            FilesAssociatedByUser
                .getListOfImagesAssociatedToVideo(domainInformationImageList).find { it.isSelected }
        assert(associatedList != null)
    }

    @Test
    fun cleanList() {
        FilesAssociatedByUser.setFinalValue(mutableListOf(mockk(), mockk()))
        FilesAssociatedByUser.cleanList()
        assert(FilesAssociatedByUser.value.size == 0)
    }
}
