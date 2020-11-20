package com.lawmobile.presentation.ui.associateSnapshots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentAssociateSnapshotsBinding
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment.Companion.checkableListInit
import com.lawmobile.presentation.ui.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SIMPLE_FILE_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.THUMBNAIL_FILE_LIST
import com.lawmobile.presentation.widgets.CustomFilterDialog

class AssociateSnapshotsFragment : BaseFragment() {

    private var _fragmentAssociateSnapshotsBinding: FragmentAssociateSnapshotsBinding? = null
    private val fragmentAssociateSnapshotsBinding get() = _fragmentAssociateSnapshotsBinding!!

    private var simpleFileListFragment = SimpleFileListFragment()
    private var thumbnailFileListFragment = ThumbnailFileListFragment()
    private lateinit var actualFragment: String
    private var snapshotsAssociatedFromMetadata: MutableList<DomainPhotoAssociated>? = null
    var onAssociateSnapshots: (() -> Unit)? = null
    private var filterDialog: CustomFilterDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentAssociateSnapshotsBinding =
            FragmentAssociateSnapshotsBinding.inflate(inflater, container, false)
        return fragmentAssociateSnapshotsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkableListInit = true
        setExtras()
        setListeners()
        setThumbnailListFragment()
    }

    private fun setExtras() {
        simpleFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, SNAPSHOT_LIST) }
        thumbnailFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, SNAPSHOT_LIST) }
    }

    fun setSnapshotsAssociatedFromMetadata(list: MutableList<DomainPhotoAssociated>) {
        snapshotsAssociatedFromMetadata = mutableListOf()
        snapshotsAssociatedFromMetadata?.addAll(list)
    }

    private fun setListeners() {
        simpleFileListFragment.onFileCheck = { _, it ->
            showSelectedItemsCount(it)
        }
        thumbnailFileListFragment.onImageCheck = { _, it ->
            showSelectedItemsCount(it)
        }
        fragmentAssociateSnapshotsBinding.buttonFilterAssociateImages.setOnClickListenerCheckConnection {
            showFilterDialog()
        }
        fragmentAssociateSnapshotsBinding.buttonThumbnailListAssociate.setClickListenerCheckConnection {
            setThumbnailListFragment()
        }
        fragmentAssociateSnapshotsBinding.buttonSimpleListAssociate.setClickListenerCheckConnection {
            setSimpleFileListFragment()
        }
        fragmentAssociateSnapshotsBinding.buttonAssociateImages.setOnClickListenerCheckConnection {
            addSnapshotsToVideo()
        }
    }

    private fun addSnapshotsToVideo() {
        if (areSnapshotsSelected()) {
            onAssociateSnapshots?.invoke()
        } else {
            fragmentAssociateSnapshotsBinding.layoutAssociateImagesToVideo.showErrorSnackBar(
                getString(R.string.no_new_photo_selected_message)
            )
        }
    }

    private fun areSnapshotsSelected(): Boolean {
        return if (snapshotsAssociatedFromMetadata.isNullOrEmpty())
            SnapshotsAssociatedByUser.temporal.isNotEmpty()
        else snapshotsAssociatedFromMetadata != SnapshotsAssociatedByUser.temporal
    }

    private fun showFilterDialog() {
        var listToFilter = listOf<DomainInformationForList>()

        when (actualFragment) {
            SIMPLE_FILE_LIST ->
                listToFilter = simpleFileListFragment.fileListBackup
            THUMBNAIL_FILE_LIST ->
                listToFilter = thumbnailFileListFragment.fileListBackup
        }

        if (filterDialog == null) {
            filterDialog =
                fragmentAssociateSnapshotsBinding.layoutAssociateFilterTags.createFilterDialog(::handleOnApplyFilterClick)
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
        fragmentAssociateSnapshotsBinding.scrollFilterAssociateTags.isVisible = it
        updateButtonFilterState(it)
        when (actualFragment) {
            SIMPLE_FILE_LIST ->
                simpleFileListFragment.applyFiltersToList()
            THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.applyFiltersToList()
        }
    }

    private fun updateButtonFilterState(it: Boolean) {
        with(fragmentAssociateSnapshotsBinding.buttonFilterAssociateImages) {
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
                    R.drawable.background_button_cancel
                )
            }
        }
    }

    private fun setSimpleFileListFragment() {
        actualFragment = SIMPLE_FILE_LIST
        fragmentAssociateSnapshotsBinding.buttonSimpleListAssociate.isActivated = true
        fragmentAssociateSnapshotsBinding.buttonThumbnailListAssociate.isActivated = false
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            simpleFileListFragment,
            SIMPLE_FILE_LIST
        )
    }

    private fun setThumbnailListFragment() {
        actualFragment = THUMBNAIL_FILE_LIST
        fragmentAssociateSnapshotsBinding.buttonThumbnailListAssociate.isActivated = true
        fragmentAssociateSnapshotsBinding.buttonSimpleListAssociate.isActivated = false
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            thumbnailFileListFragment,
            THUMBNAIL_FILE_LIST
        )
    }

    private fun showSelectedItemsCount(selectedItems: Int) {
        fragmentAssociateSnapshotsBinding.textViewAssociatedItems.run {
            isVisible = selectedItems > 0
            text = getString(R.string.items_selected, selectedItems)
        }
    }

    override fun onDestroy() {
        checkableListInit = false
        super.onDestroy()
    }

    companion object {
        val TAG = AssociateSnapshotsFragment::class.java.simpleName
    }
}