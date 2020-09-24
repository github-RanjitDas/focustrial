package com.lawmobile.presentation.entities

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class SnapshotsAssociatedByUserTest {

    @Test
    fun setOrGetValue() {
        val newList = mutableListOf<PhotoAssociated>()
        SnapshotsAssociatedByUser.value = newList
        assert(newList == SnapshotsAssociatedByUser.value)
    }

    @Test
    fun updateAssociatedSnapshotsAdd() {
        val cameraConnectFile = mockk<CameraConnectFile>(relaxed = true) {
            every { name } returns "4567890"
            every { nameFolder } returns "4567890123"
        }
        val photoList = mutableListOf(PhotoAssociated("123", "123"))
        SnapshotsAssociatedByUser.value = photoList
        SnapshotsAssociatedByUser.updateAssociatedSnapshots(cameraConnectFile)
        assert(SnapshotsAssociatedByUser.value.size == 2)
    }

    @Test
    fun updateAssociatedSnapshotsRemove() {
        val cameraConnectFile = mockk<CameraConnectFile>(relaxed = true) {
            every { name } returns "123"
            every { date } returns "123"
        }
        val photoList = mutableListOf(PhotoAssociated("123", "123"))
        SnapshotsAssociatedByUser.value = photoList
        SnapshotsAssociatedByUser.updateAssociatedSnapshots(cameraConnectFile)
        assert(SnapshotsAssociatedByUser.value.size == 0)
    }

    @Test
    fun getImagesAssociatedToVideoListThumbnails() {
        val domainInformationImageList =
            mutableListOf(
                DomainInformationImage(
                    mockk{
                        every { name } returns "123"
                    }
                ),
                DomainInformationImage(
                    mockk{
                        every { name } returns "456"
                    }
                )
            )
        SnapshotsAssociatedByUser.value = mutableListOf(PhotoAssociated("123", "123"))
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
                    mockk{
                        every { name } returns "123"
                    }
                ),
                DomainInformationFile(
                    mockk{
                        every { name } returns "456"
                    }
                )
            )
        SnapshotsAssociatedByUser.value = mutableListOf(PhotoAssociated("123", "123"))
        val associatedList =
            SnapshotsAssociatedByUser
                .getListOfImagesAssociatedToVideo(domainInformationImageList).find { it.isSelected }
        assert(associatedList != null)
    }

    @Test
    fun cleanList() {
        SnapshotsAssociatedByUser.value = mutableListOf(mockk(), mockk())
        SnapshotsAssociatedByUser.cleanList()
        assert(SnapshotsAssociatedByUser.value.size == 0)
    }
}