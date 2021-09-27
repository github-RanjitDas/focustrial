package com.lawmobile.presentation.ui.associateFiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.DomainAssociatedFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentAssociateFilesBinding
import com.lawmobile.presentation.entities.FilesAssociatedByUser
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment.Companion.checkableListInit
import com.lawmobile.presentation.ui.fileList.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants.AUDIO_LIST
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SIMPLE_FILE_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.THUMBNAIL_FILE_LIST
import com.lawmobile.presentation.widgets.CustomFilterDialog

class AssociateFilesFragment : BaseFragment() {

    private var _binding: FragmentAssociateFilesBinding? = null
    private val binding get() = _binding!!

    private var simpleFileListFragment = SimpleFileListFragment()
    private var thumbnailFileListFragment = ThumbnailFileListFragment()
    private lateinit var actualFragment: String
    private var associatedFilesFromMetadata: MutableList<DomainAssociatedFile>? = null

    private var listType: String? = null

    private var filterDialog: CustomFilterDialog? = null
    var onAssociateFiles: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentAssociateFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkableListInit = true
        setListType()
        setExtras()
        setListeners()
        binding.setFileListFragment()
    }

    private fun FragmentAssociateFilesBinding.setFileListFragment() {
        when (listType) {
            SNAPSHOT_LIST -> {
                setListTypeButtonsVisibility(true)
                buttonAssociateFiles.text = getString(R.string.associate_snapshots)
                setThumbnailListFragment()
            }
            AUDIO_LIST -> {
                setListTypeButtonsVisibility(false)
                buttonAssociateFiles.text = getString(R.string.associate_audios)
                setSimpleFileListFragment()
            }
        }
    }

    private fun FragmentAssociateFilesBinding.setListTypeButtonsVisibility(isVisible: Boolean) {
        buttonSimpleListAssociate.isVisible = isVisible
        buttonThumbnailListAssociate.isVisible = isVisible
    }

    private fun setListType() {
        listType = arguments?.getString(FILE_LIST_TYPE)
    }

    private fun setExtras() {
        simpleFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, listType) }

        when (listType) {
            SNAPSHOT_LIST ->
                thumbnailFileListFragment.arguments =
                    Bundle().apply { putString(FILE_LIST_TYPE, listType) }
        }
    }

    fun setFilesAssociatedFromMetadata(list: MutableList<DomainAssociatedFile>) {
        associatedFilesFromMetadata = mutableListOf()
        associatedFilesFromMetadata?.addAll(list)
    }

    private fun setListeners() {
        simpleFileListFragment.onFileCheck = { _, it ->
            showSelectedItemsCount(it)
        }
        thumbnailFileListFragment.onImageCheck = { _, it ->
            showSelectedItemsCount(it)
        }
        binding.buttonFilterFiles.setOnClickListenerCheckConnection {
            showFilterDialog()
        }
        binding.buttonThumbnailListAssociate.setClickListenerCheckConnection {
            setThumbnailListFragment()
        }
        binding.buttonSimpleListAssociate.setClickListenerCheckConnection {
            setSimpleFileListFragment()
        }
        binding.buttonAssociateFiles.setOnClickListenerCheckConnection {
            addFilesToVideo()
        }
    }

    private fun addFilesToVideo() {
        if (areFilesSelected()) onAssociateFiles?.invoke()
        else {
            binding.layoutAssociateImagesToVideo.showErrorSnackBar(
                getString(R.string.no_new_photo_selected_message)
            )
        }
    }

    private fun areFilesSelected(): Boolean {
        return if (associatedFilesFromMetadata.isNullOrEmpty())
            FilesAssociatedByUser.temporal.isNotEmpty()
        else associatedFilesFromMetadata != FilesAssociatedByUser.temporal
    }

    private fun showFilterDialog() {
        var listToFilter = listOf<DomainInformationForList>()

        when (actualFragment) {
            SIMPLE_FILE_LIST -> listToFilter = simpleFileListFragment.fileListBackup
            THUMBNAIL_FILE_LIST -> listToFilter = thumbnailFileListFragment.fileListBackup
        }

        if (filterDialog == null) {
            filterDialog =
                binding.layoutAssociateFilterTags.createFilterDialog(::handleOnApplyFilterClick)
        }

        filterDialog?.apply {
            this.listToFilter = listToFilter
            show()
            filterDialog?.isEventSpinnerFilterVisible(false)
            simpleFileListFragment.filter = this
            thumbnailFileListFragment.filter = this
        }
    }

    private fun handleOnApplyFilterClick(it: Boolean) {
        binding.scrollFilterAssociateTags.isVisible = it
        updateButtonFilterState(it)
        when (actualFragment) {
            SIMPLE_FILE_LIST -> simpleFileListFragment.applyFiltersToList()
            THUMBNAIL_FILE_LIST -> thumbnailFileListFragment.applyFiltersToList()
        }
    }

    private fun updateButtonFilterState(it: Boolean) {
        with(binding.buttonFilterFiles) {
            background = if (!it) {
                setImageResource(R.drawable.ic_filter_white)
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_button_blue
                )
            } else {
                setImageResource(R.drawable.ic_filter)
                ContextCompat.getDrawable(
                    context,
                    R.drawable.border_rounded_blue
                )
            }
        }
    }

    private fun setSimpleFileListFragment() {
        actualFragment = SIMPLE_FILE_LIST
        binding.setListTypeButtonsState(true)
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            simpleFileListFragment,
            SIMPLE_FILE_LIST
        )
    }

    private fun setThumbnailListFragment() {
        actualFragment = THUMBNAIL_FILE_LIST
        binding.setListTypeButtonsState(false)
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            thumbnailFileListFragment,
            THUMBNAIL_FILE_LIST
        )
    }

    private fun FragmentAssociateFilesBinding.setListTypeButtonsState(isSimple: Boolean) {
        buttonSimpleListAssociate.isActivated = isSimple
        buttonThumbnailListAssociate.isActivated = isSimple.not()
    }

    private fun showSelectedItemsCount(selectedItems: Int) {
        binding.textViewAssociatedItems.run {
            isVisible = selectedItems > 0
            text = getString(R.string.items_selected, selectedItems)
        }
    }

    override fun onDestroy() {
        checkableListInit = false
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = AssociateFilesFragment::class.java.simpleName
    }
}
