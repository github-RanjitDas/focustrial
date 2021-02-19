package com.lawmobile.domain.entities

import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test

class FileListTest {

    @Test
    fun testChangeListOfImages() {
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.changeImageList(list)
        Assert.assertEquals(list, FileList.imageList)
    }

    @Test
    fun testChangeListOfVideos() {
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.changeVideoList(list)
        Assert.assertEquals(list, FileList.videoList)
    }

    @Test
    fun testUpdateItemInListImageMetadataNewValue() {
        val item = DomainInformationImageMetadata(
            DomainPhotoMetadata("filename.PNG")
        )
        FileList.updateItemInImageMetadataList(item)
        val itemFind = FileList.getMetadataOfImageInList("filename.PNG")
        Assert.assertEquals(itemFind, item)
    }

    @Test
    fun testUpdateItemInListImageMetadataValueChange() {
        val item = DomainInformationImageMetadata(
            DomainPhotoMetadata("filename.PNG")
        )
        FileList.updateItemInImageMetadataList(item)

        val itemChange = DomainInformationImageMetadata(
            DomainPhotoMetadata(fileName = "filename.PNG", officerId = "officer")
        )
        FileList.updateItemInImageMetadataList(itemChange)
        val itemFind = FileList.getMetadataOfImageInList("filename.PNG")
        Assert.assertEquals(itemFind, itemChange)
    }

    @Test
    fun testUpdateItemInListImageMetadataSaveTwoItems() {
        val item = DomainInformationImageMetadata(
            DomainPhotoMetadata("filename.PNG")
        )
        FileList.updateItemInImageMetadataList(item)

        val itemTwo = DomainInformationImageMetadata(
            DomainPhotoMetadata(fileName = "filename2.PNG", officerId = "officer2")
        )
        FileList.updateItemInImageMetadataList(itemTwo)
        val itemFindOne = FileList.getMetadataOfImageInList("filename.PNG")
        Assert.assertEquals(itemFindOne, item)

        val itemFindTwo = FileList.getMetadataOfImageInList("filename2.PNG")
        Assert.assertEquals(itemTwo, itemFindTwo)

        val itemFindThree = FileList.getMetadataOfImageInList("filename3.PNG")
        Assert.assertEquals(null, itemFindThree)
    }
}
