package com.lawmobile.data.entities

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test

class FileListTest {

    @Test
    fun testChangeListOfImages(){
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.changeListOfImages(list)
        Assert.assertEquals(list, FileList.listOfImages)
    }

    @Test
    fun testChangeListOfVideos(){
        val domainInformationFile: DomainInformationFile = mockk(relaxed = true)
        val list: List<DomainInformationFile> = listOf(domainInformationFile, domainInformationFile)
        FileList.changeListOfVideos(list)
        Assert.assertEquals(list,FileList.listOfVideos)
    }

    @Test
    fun testUpdateItemInListImageMetadataNewValue() {
        val item = DomainInformationImageMetadata(
            CameraConnectPhotoMetadata("filename.PNG")
        )
        FileList.updateItemInListImageMetadata(item)
        val itemFind = FileList.getItemInListImageOfMetadata("filename.PNG")
        Assert.assertEquals(itemFind, item)
    }

    @Test
    fun testUpdateItemInListImageMetadataValueChange() {
        val item = DomainInformationImageMetadata(
            CameraConnectPhotoMetadata("filename.PNG")
        )
        FileList.updateItemInListImageMetadata(item)

        val itemChange = DomainInformationImageMetadata(
            CameraConnectPhotoMetadata("filename.PNG", "officer")
        )
        FileList.updateItemInListImageMetadata(itemChange)
        val itemFind = FileList.getItemInListImageOfMetadata("filename.PNG")
        Assert.assertEquals(itemFind, itemChange)
    }

    @Test
    fun testUpdateItemInListImageMetadataSaveTwoItems() {
        val item = DomainInformationImageMetadata(
            CameraConnectPhotoMetadata("filename.PNG")
        )
        FileList.updateItemInListImageMetadata(item)

        val itemTwo = DomainInformationImageMetadata(
            CameraConnectPhotoMetadata("filename2.PNG", "officer2")
        )
        FileList.updateItemInListImageMetadata(itemTwo)
        val itemFindOne = FileList.getItemInListImageOfMetadata("filename.PNG")
        Assert.assertEquals(itemFindOne, item)

        val itemFindTwo = FileList.getItemInListImageOfMetadata("filename2.PNG")
        Assert.assertEquals(itemTwo, itemFindTwo)

        val itemFindThree = FileList.getItemInListImageOfMetadata("filename3.PNG")
        Assert.assertEquals(null, itemFindThree)
    }
}