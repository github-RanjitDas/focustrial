package com.lawmobile.presentation.ui.fileList.filterSection.x1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFilterSectionListX1Binding
import com.lawmobile.presentation.extensions.buttonFilterState
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.shared.FileSelection
import com.lawmobile.presentation.ui.fileList.shared.FilterSection
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.lawmobile.presentation.widgets.CustomFilterDialog

class FilterSectionX1Fragment : BaseFragment(), FileSelection, FilterSection {

    private var _binding: FragmentFilterSectionListX1Binding? = null
    private val binding: FragmentFilterSectionListX1Binding get() = _binding!!

    override lateinit var onButtonSelectClick: () -> Unit
    override lateinit var onButtonFilterClick: () -> Unit

    private var selectButtonTitle = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterSectionListX1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setListener()
        setFeatures()
    }

    private fun setFeatures() {
        if (!FeatureSupportHelper.supportAssociateOfficerID)
            binding.buttonSelectToAssociate.visibility = View.INVISIBLE
    }

    override fun toggleSelection(isActive: Boolean) {
        toggleButtonSelect(isActive)
        setSelectedItemsCountVisibility(isActive)
    }

    override fun onFileSelected(selectedCount: Int) {
        updateSelectedItemsCount(selectedCount)
    }

    private fun toggleButtonSelect(isActive: Boolean) {
        binding.buttonSelectToAssociate.apply {
            isActivated = isActive
            text = if (isActive) getString(R.string.cancel)
            else selectButtonTitle
        }
    }

    private fun updateSelectedItemsCount(selectedItems: Int) {
        binding.textViewSelectedItems.apply {
            setSelectedItemsCountVisibility(selectedItems > 0)
            text = getString(R.string.items_selected, selectedItems)
        }
    }

    private fun setSelectedItemsCountVisibility(isVisible: Boolean) {
        binding.textViewSelectedItems.isVisible = isVisible
    }

    override fun createFilterDialog(
        onApplyFilter: () -> Unit,
        onCloseFilter: () -> Unit
    ): CustomFilterDialog =
        binding.layoutFilterTags.createFilterDialog(
            {
                updateFilterButtonState(it)
                onApplyFilter()
            },
            onCloseFilter
        )

    private fun updateFilterButtonState(isActive: Boolean) {
        binding.scrollFilterTags.isVisible = isActive
        binding.buttonOpenFilters.buttonFilterState(isActive)
    }

    private fun FragmentFilterSectionListX1Binding.setListener() {
        buttonSelectToAssociate.setOnClickListenerCheckConnection { onButtonSelectClick() }
        buttonOpenFilters.setOnClickListenerCheckConnection { onButtonFilterClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG: String = FilterSectionX1Fragment::class.java.simpleName
        fun createInstance(selectButtonTitle: String) = FilterSectionX1Fragment().apply {
            this.selectButtonTitle = selectButtonTitle
        }
    }
}
