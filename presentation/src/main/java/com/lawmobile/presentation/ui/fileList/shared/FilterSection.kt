package com.lawmobile.presentation.ui.fileList.shared
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.widgets.CustomFilterDialog

interface FilterSection {
    var onButtonFilterClick: () -> Unit
    fun showFilterDialog(
        listToFilter: List<DomainInformationForList>,
        enableEvents: Boolean,
        onApplyFilter: () -> Unit,
        onCloseFilter: () -> Unit
    ): CustomFilterDialog
}
