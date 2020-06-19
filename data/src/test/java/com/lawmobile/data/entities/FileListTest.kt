package com.lawmobile.data.entities

import com.lawmobile.domain.entities.DomainInformationFile
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
}