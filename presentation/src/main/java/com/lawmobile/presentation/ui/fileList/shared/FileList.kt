package com.lawmobile.presentation.ui.fileList.shared

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.widgets.CustomFilterDialog

interface FileList {
    var listBackup: MutableList<out DomainInformationForList>
    var filter: CustomFilterDialog?
    var onFileCheck: ((Int) -> Unit)?
    fun setSelectedFiles(selectedFiles: List<DomainCameraFile>)
    fun applyFiltersToList()
    fun getListOfSelectedItems(): List<DomainCameraFile>?
    fun toggleCheckBoxes(show: Boolean)
}
