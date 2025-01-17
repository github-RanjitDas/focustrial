package com.lawmobile.presentation.ui.fileList.filterSection.x2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFilterSectionListX2Binding
import com.lawmobile.presentation.extensions.buttonFilterState
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.shared.FileSelection
import com.lawmobile.presentation.ui.fileList.shared.FilterSection
import com.lawmobile.presentation.ui.fileList.shared.ListTypeButtons
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.lawmobile.presentation.widgets.CustomFilterDialog

class FilterSectionX2Fragment : BaseFragment(), ListTypeButtons, FileSelection, FilterSection {

    private var _binding: FragmentFilterSectionListX2Binding? = null
    private val binding: FragmentFilterSectionListX2Binding get() = _binding!!
    private var isNavigationActive: Boolean = false

    override lateinit var onButtonSelectClick: () -> Unit
    override lateinit var onButtonFilterClick: () -> Unit

    override lateinit var onThumbnailsClick: () -> Unit
    override lateinit var onSimpleClick: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterSectionListX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setFeatures()
        configureView()
    }

    private fun setFeatures() {
        binding.buttonSelectToAssociate.isVisible = FeatureSupportHelper.supportAssociateOfficerID
    }

    private fun configureView() {
        binding.buttonSimpleList.isVisible = isNavigationActive
        binding.buttonThumbnailList.isVisible = isNavigationActive
    }

    override fun toggleSelection(isActive: Boolean) {
        toggleButtonSelect(isActive)
    }

    override fun onFileSelected(selectedCount: Int) {
        updateSelectedItemsCount(selectedCount)
    }

    private fun toggleButtonSelect(isActive: Boolean) {
        binding.buttonSelectToAssociate.apply {
            isActivated = isActive
            text = if (isActive) getString(R.string.items_selected, 0)
            else getString(R.string.select)
        }
    }

    private fun updateSelectedItemsCount(selectedItems: Int) {
        if (selectedItems > 0 || isButtonSelectToAssociateActive()) {
            binding.buttonSelectToAssociate.text = getString(R.string.items_selected, selectedItems)
        }
    }

    private fun isButtonSelectToAssociateActive() = binding.buttonSelectToAssociate.isActivated

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

    private fun updateFilterButtonState(isActive: Boolean) = with(binding) {
        scrollFilterTags.isVisible = isActive
        buttonOpenFilters.buttonFilterState(isActive)
    }

    override fun toggleListType(isSimple: Boolean) = with(binding) {
        buttonSimpleList.isActivated = isSimple
        buttonThumbnailList.isActivated = !isSimple
    }

    private fun setListener() = with(binding) {
        buttonSelectToAssociate.setOnClickListenerCheckConnection { onButtonSelectClick() }
        buttonOpenFilters.setOnClickListenerCheckConnection { onButtonFilterClick() }
        buttonSimpleListListener()
        buttonThumbnailListListener()
    }

    private fun FragmentFilterSectionListX2Binding.buttonThumbnailListListener() {
        buttonThumbnailList.setClickListenerCheckConnection {
            it.isActivated = true
            buttonSimpleList.isActivated = false
            onThumbnailsClick()
        }
    }

    private fun FragmentFilterSectionListX2Binding.buttonSimpleListListener() {
        buttonSimpleList.setClickListenerCheckConnection {
            it.isActivated = true
            buttonThumbnailList.isActivated = false
            onSimpleClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = FilterSectionX2Fragment::class.java.simpleName
        fun createInstance(isNavigationActive: Boolean = false): FilterSectionX2Fragment {
            return FilterSectionX2Fragment().apply {
                this.isNavigationActive = isNavigationActive
            }
        }
    }
}
