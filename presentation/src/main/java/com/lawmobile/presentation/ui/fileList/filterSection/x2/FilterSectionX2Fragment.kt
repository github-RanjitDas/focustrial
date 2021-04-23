package com.lawmobile.presentation.ui.fileList.filterSection.x2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFilterSectionListX2Binding
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.widgets.CustomFilterDialog

class FilterSectionX2Fragment : BaseFragment() {

    private var _binding: FragmentFilterSectionListX2Binding? = null
    private val binding: FragmentFilterSectionListX2Binding get() = _binding!!
    private var isViewCreated: Boolean = false
    private var isNavigationActive: Boolean = false

    lateinit var onTapSelectButtonToAssociate: () -> Unit
    lateinit var onTapButtonOpenFilters: () -> Unit
    lateinit var onTapThumbnail: () -> Unit
    lateinit var onTapSimpleList: () -> Unit

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
        isViewCreated = true
        setListener()
        configureView()
    }

    private fun configureView() {
        binding.buttonSimpleList.isVisible = isNavigationActive
        binding.buttonThumbnailList.isVisible = isNavigationActive
    }

    fun resetButtonSelectToAssociate() {
        if (!isViewCreated) return
        with(binding.buttonSelectToAssociate) {
            isActivated = false
            text = getString(R.string.select)
        }
    }

    fun changeTextSelectedItems(selectedItems: Int) {
        if (selectedItems > 0 || isButtonSelectToAssociateActive()) {
            binding.buttonSelectToAssociate.text =
                getString(R.string.items_selected, selectedItems)
        }
    }

    fun isButtonSelectToAssociateActive() = binding.buttonSelectToAssociate.isActivated

    fun activateSelectButtonToAssociate() {
        if (!isViewCreated) return
        with(binding.buttonSelectToAssociate) {
            isActivated = true
            text = getString(R.string.items_selected, 0)
        }
    }

    fun createFilterDialog(onApplyClick: (Boolean) -> Unit): CustomFilterDialog =
        binding.layoutFilterTags.createFilterDialog(onApplyClick)

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

    fun isSimpleListActivity(isActive: Boolean) {
        if (!isViewCreated) return
        binding.buttonSimpleList.isActivated = isActive
        binding.buttonThumbnailList.isActivated = !isActive
    }

    private fun setListener() {
        binding.buttonSelectToAssociate.setOnClickListenerCheckConnection { onTapSelectButtonToAssociate() }
        binding.buttonOpenFilters.setOnClickListenerCheckConnection { onTapButtonOpenFilters() }
        binding.buttonSimpleList.setClickListenerCheckConnection { onTapSimpleList() }
        binding.buttonThumbnailList.setClickListenerCheckConnection { onTapThumbnail() }
    }

    companion object {
        val TAG = FilterSectionX2Fragment::class.java.simpleName
        fun createInstance(isNavigationActive: Boolean = false): FilterSectionX2Fragment {
            return FilterSectionX2Fragment().apply {
                this.isNavigationActive = isNavigationActive
            }
        }
    }
}
