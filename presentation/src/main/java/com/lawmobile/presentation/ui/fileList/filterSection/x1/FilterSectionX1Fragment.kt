package com.lawmobile.presentation.ui.fileList.filterSection.x1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFilterSectionListX1Binding
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.widgets.CustomFilterDialog

class FilterSectionX1Fragment : BaseFragment() {

    private var _binding: FragmentFilterSectionListX1Binding? = null
    private val binding: FragmentFilterSectionListX1Binding get() = _binding!!

    lateinit var onTapButtonSelectSnapshotAssociate: () -> Unit
    lateinit var onTapButtonOpenFilters: () -> Unit
    private var isViewCreated: Boolean = false
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
        setListener()
        isViewCreated = true
    }

    fun resetButtonAssociateSnapshot(listType: String?) {
        if (!isViewCreated) return
        with(binding.buttonSelectToAssociate) {
            isActivated = false
            text = when (listType) {
                Constants.VIDEO_LIST -> getString(R.string.select_videos_to_associate)
                else -> getString(R.string.select_snapshots_to_associate)
            }
        }
    }

    fun changeTextSelectedItems(selectedItems: Int) {
        binding.textViewSelectedItems.run {
            isTextSelectedVisible(selectedItems > 0)
            text = getString(R.string.items_selected, selectedItems)
        }
    }

    fun isTextSelectedVisible(isVisible: Boolean) {
        if (!isViewCreated) return
        binding.textViewSelectedItems.isVisible = isVisible
    }

    fun isButtonSelectedSnapshotActive() = binding.buttonSelectToAssociate.isActivated

    fun activateButtonAssociate() {
        if (!isViewCreated) return
        with(binding.buttonSelectToAssociate) {
            isActivated = true
            text = getString(R.string.cancel)
        }
    }

    fun createFilterDialog(onApplyClick: (Boolean) -> Unit): CustomFilterDialog = binding.layoutFilterTags.createFilterDialog(onApplyClick)

    fun updateFilterButtonState(isVisible: Boolean) {
        binding.scrollFilterTags.isVisible = isVisible
        with(binding.buttonOpenFilters) {
            background = if (isVisible) {
                setImageResource(R.drawable.ic_filter_white)
                androidx.core.content.ContextCompat.getDrawable(
                    context,
                    R.drawable.background_button_blue
                )
            } else {
                setImageResource(R.drawable.ic_filter)
                androidx.core.content.ContextCompat.getDrawable(
                    context,
                    R.drawable.border_rounded_blue
                )
            }
        }
    }

    private fun setListener() {
        binding.buttonSelectToAssociate.setOnClickListenerCheckConnection { onTapButtonSelectSnapshotAssociate() }
        binding.buttonOpenFilters.setOnClickListenerCheckConnection { onTapButtonOpenFilters() }
    }

    companion object {
        val TAG = FilterSectionX1Fragment::class.java.simpleName
    }
}
