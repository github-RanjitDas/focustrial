package com.lawmobile.presentation.entities

object FilePathSaved {
    private val filesPathSavedImages: ArrayList<ImageWithPathSaved> = ArrayList()

    fun saveImageWithPath(image: ImageWithPathSaved) {
        val index = filesPathSavedImages.indexOfFirst { it == image }
        if (index != -1) {
            filesPathSavedImages[index] = image
            return
        }

        filesPathSavedImages.add(image)
    }

    fun getImageIfExist(name: String): ImageWithPathSaved? {
        return filesPathSavedImages.firstOrNull { it.name == name }
    }

}