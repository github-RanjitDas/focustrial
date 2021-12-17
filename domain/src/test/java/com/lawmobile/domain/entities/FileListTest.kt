package com.lawmobile.domain.entities

import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test

class FileListTest {

    @Test
    fun testChangeListOfImages() {
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.imageList = list
        Assert.assertEquals(list, FileList.imageList)
    }

    @Test
    fun testChangeListOfVideos() {
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.videoList = list
        Assert.assertEquals(list, FileList.videoList)
    }

    @Test
    fun testChangeListOfAudios() {
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.audioList = list
        Assert.assertEquals(list, FileList.audioList)
    }

    @Test
    fun testUpdateItemInListImageMetadataNewValue() {
        val item = DomainInformationImageMetadata(DomainPhotoMetadata("filename.PNG"))
        FileList.updateItemInImageMetadataList(item)
        val itemFind = FileList.findAndGetImageMetadata("filename.PNG")
        Assert.assertEquals(itemFind, item)
    }

    @Test
    fun testUpdateItemInListImageMetadataValueChange() {
        val item = DomainInformationImageMetadata(DomainPhotoMetadata("filename.PNG"))
        FileList.updateItemInImageMetadataList(item)

        val itemChange = DomainInformationImageMetadata(
            DomainPhotoMetadata(fileName = "filename.PNG", officerId = "officer")
        )
        FileList.updateItemInImageMetadataList(itemChange)
        val itemFind = FileList.findAndGetImageMetadata("filename.PNG")
        Assert.assertEquals(itemFind, itemChange)
    }

    @Test
    fun testUpdateItemInListImageMetadataSaveTwoItems() {
        val item = DomainInformationImageMetadata(DomainPhotoMetadata("filename.PNG"))
        FileList.updateItemInImageMetadataList(item)

        val itemTwo = DomainInformationImageMetadata(
            DomainPhotoMetadata(fileName = "filename2.PNG", officerId = "officer2")
        )
        FileList.updateItemInImageMetadataList(itemTwo)
        val itemFindOne = FileList.findAndGetImageMetadata("filename.PNG")
        Assert.assertEquals(itemFindOne, item)

        val itemFindTwo = FileList.findAndGetImageMetadata("filename2.PNG")
        Assert.assertEquals(itemTwo, itemFindTwo)

        val itemFindThree = FileList.findAndGetImageMetadata("filename3.PNG")
        Assert.assertEquals(null, itemFindThree)
    }

    @Test
    fun testUpdateItemInListAudioMetadataNewValue() {
        val item = DomainInformationAudioMetadata(DomainAudioMetadata("filename.PNG"))
        FileList.updateItemInAudioMetadataList(item)
        val itemFind = FileList.findAndGetAudioMetadata("filename.PNG")
        Assert.assertEquals(itemFind, item)
    }

    @Test
    fun testUpdateItemInListAudioMetadataValueChange() {
        val item = DomainInformationAudioMetadata(DomainAudioMetadata("filename.PNG"))
        FileList.updateItemInAudioMetadataList(item)

        val itemChange = DomainInformationAudioMetadata(
            DomainAudioMetadata(fileName = "filename.PNG", officerId = "officer")
        )
        FileList.updateItemInAudioMetadataList(itemChange)
        val itemFind = FileList.findAndGetAudioMetadata("filename.PNG")
        Assert.assertEquals(itemFind, itemChange)
    }

    @Test
    fun testUpdateItemInListAudioMetadataSaveTwoItems() {
        val item = DomainInformationAudioMetadata(DomainAudioMetadata("filename.PNG"))
        FileList.updateItemInAudioMetadataList(item)

        val itemTwo = DomainInformationAudioMetadata(
            DomainAudioMetadata(fileName = "filename2.PNG", officerId = "officer2")
        )
        FileList.updateItemInAudioMetadataList(itemTwo)
        val itemFindOne = FileList.findAndGetAudioMetadata("filename.PNG")
        Assert.assertEquals(itemFindOne, item)

        val itemFindTwo = FileList.findAndGetAudioMetadata("filename2.PNG")
        Assert.assertEquals(itemTwo, itemFindTwo)

        val itemFindThree = FileList.findAndGetAudioMetadata("filename3.PNG")
        Assert.assertEquals(null, itemFindThree)
    }
}
