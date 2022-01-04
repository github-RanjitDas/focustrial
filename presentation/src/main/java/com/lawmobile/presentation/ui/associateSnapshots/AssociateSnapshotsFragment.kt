package com.lawmobile.presentation.ui.associateSnapshots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.DomainAssociatedFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentAssociateSnapshotsBinding
import com.lawmobile.presentation.entities.FilesAssociatedByUser
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SIMPLE_FILE_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.THUMBNAIL_FILE_LIST
import com.lawmobile.presentation.widgets.CustomFilterDialog

class AssociateSnapshotsFragment : BaseFragment() {

    private var _binding: FragmentAssociateSnapshotsBinding? = null
    private val binding get() = _binding!!

    private var simpleFileListFragment = SimpleFileListFragment()
    private var thumbnailFileListFragment = ThumbnailFileListFragment()
    private lateinit var actualFragment: String
    private var associatedFilesFromMetadata: MutableList<DomainAssociatedFile>? = null
    var onAssociateSnapshots: (() -> Unit)? = null
    private var filterDialog: CustomFilterDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentAssociateSnapshotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setExtras()
        setListeners()
        setThumbnailListFragment()
    }

    override fun onResume() {
        super.onResume()
        setAssociatedRecyclerView()
    }

    private fun setAssociatedRecyclerView() {
        simpleFileListFragment.toggleCheckBoxes(true)
        simpleFileListFragment.listAdapter.run {
            fileList.let { completeList ->
                fileList = FilesAssociatedByUser
                    .getListOfImagesAssociatedToVideo(completeList)
                    .filterIsInstance<DomainInformationFile>() as MutableList
            }
        }
        simpleFileListFragment.setRecyclerView()
    }

    private fun setExtras() {
        simpleFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, SNAPSHOT_LIST) }
        thumbnailFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, SNAPSHOT_LIST) }
    }

    fun setSnapshotsAssociatedFromMetadata(list: MutableList<DomainAssociatedFile>) {
        associatedFilesFromMetadata = mutableListOf()
        associatedFilesFromMetadata?.addAll(list)
    }

    private fun setListeners() {
        simpleFileListFragment.onFileCheck = { it ->
            showSelectedItemsCount(it)
        }
        thumbnailFileListFragment.onFileCheck = { it ->
            showSelectedItemsCount(it)
        }
        binding.buttonFilterAssociateImages.setOnClickListenerCheckConnection {
            showFilterDialog()
        }
        binding.buttonThumbnailListAssociate.setClickListenerCheckConnection {
            setThumbnailListFragment()
        }
        binding.buttonSimpleListAssociate.setClickListenerCheckConnection {
            setSimpleFileListFragment()
        }
        binding.buttonAssociateImages.setOnClickListenerCheckConnection {
            addSnapshotsToVideo()
        }
    }

    private fun addSnapshotsToVideo() {
        if (areSnapshotsSelected()) {
            onAssociateSnapshots?.invoke()
        } else {
            binding.layoutAssociateImagesToVideo.showErrorSnackBar(
                getString(R.string.no_new_photo_selected_message)
            )
        }
    }

    private fun areSnapshotsSelected(): Boolean {
        return if (associatedFilesFromMetadata.isNullOrEmpty())
            FilesAssociatedByUser.temporal.isNotEmpty()
        else associatedFilesFromMetadata != FilesAssociatedByUser.temporal
    }

    private fun showFilterDialog() {
        var listToFilter = listOf<DomainInformationForList>()

        when (actualFragment) {
            SIMPLE_FILE_LIST ->
                listToFilter = simpleFileListFragment.listBackup
            THUMBNAIL_FILE_LIST ->
                listToFilter = thumbnailFileListFragment.listBackup
        }

        if (filterDialog == null) {
            filterDialog =
                binding.layoutAssociateFilterTags.createFilterDialog(::handleOnApplyFilterClick) {}
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
            SIMPLE_FILE_LIST ->
                simpleFileListFragment.applyFiltersToList()
            THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.applyFiltersToList()
        }
    }

    private fun updateButtonFilterState(it: Boolean) {
        with(binding.buttonFilterAssociateImages) {
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
        binding.buttonSimpleListAssociate.isActivated = true
        binding.buttonThumbnailListAssociate.isActivated = false
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            simpleFileListFragment,
            SIMPLE_FILE_LIST
        )
    }

    private fun setThumbnailListFragment() {
        actualFragment = THUMBNAIL_FILE_LIST
        binding.buttonThumbnailListAssociate.isActivated = true
        binding.buttonSimpleListAssociate.isActivated = false
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            thumbnailFileListFragment,
            THUMBNAIL_FILE_LIST
        )
    }

    private fun showSelectedItemsCount(selectedItems: Int) {
        binding.textViewAssociatedItems.run {
            isVisible = selectedItems > 0
            text = getString(R.string.items_selected, selectedItems)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG: String = AssociateSnapshotsFragment::class.java.simpleName
    }
}
