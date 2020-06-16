package com.lawmobile.data.entities

import com.lawmobile.domain.entity.DomainInformationFile

object FileList {
    var listOfVideos = emptyList<DomainInformationFile>()
    var listOfImages = emptyList<DomainInformationFile>()

    fun changeListOfImages(newListOfImages:List<DomainInformationFile>){
        this.listOfImages = newListOfImages
    }

    fun changeListOfVideos(newListOfVideos:List<DomainInformationFile>){
        this.listOfVideos = newListOfVideos
    }
}