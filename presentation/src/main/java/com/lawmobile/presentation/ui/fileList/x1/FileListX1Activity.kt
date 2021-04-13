package com.lawmobile.presentation.ui.fileList.x1

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.appBar.x1.AppBarX1Fragment
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.fileList.filterSection.x1.FilterSectionX1Fragment
import com.lawmobile.presentation.utils.Constants

class FileListX1Activity : FileListActivity() {

    private lateinit var appBarFragment: AppBarX1Fragment
    private var filterSectionFragment = FilterSectionX1Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppBarFragment()
        attachFragments()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        appBarFragment.isSimpleListActivity(actualFragment == Constants.SIMPLE_FILE_LIST)
    }

    private fun attachFragments() {
        attachAppBarFragment()
        attachFilterSectionFragment()
        attachListTypeFragment()
    }

    private fun attachListTypeFragment() {
        filterSectionFragment.resetButtonAssociateSnapshot(listType)
        when (listType) {
            Constants.VIDEO_LIST -> {
                appBarFragment.isSimpleListActivity(true)
                filterSectionFragment.isTextSelectedVisible(false)
                attachSimpleFileListFragment()
            }
            Constants.SNAPSHOT_LIST -> {
                appBarFragment.isSimpleListActivity(false)
                filterSectionFragment.isTextSelectedVisible(false)
                attachThumbnailListFragment()
            }
        }
    }

    private fun setListeners() {
        simpleFileListFragment.onFileCheck = { activate, selectedItems ->
            enableAssociatePartnerButton(activate)
            filterSectionFragment.changeTextSelectedItems(selectedItems)
        }

        thumbnailFileListFragment.onImageCheck = { activate, selectedItems ->
            enableAssociatePartnerButton(activate)
            filterSectionFragment.changeTextSelectedItems(selectedItems)
        }

        appBarFragment.onTapThumbnail = {
            appBarFragment.isSimpleListActivity(false)
            filterSectionFragment.isTextSelectedVisible(false)
            attachThumbnailListFragment()
        }

        appBarFragment.onTapSimpleList = {
            appBarFragment.isSimpleListActivity(true)
            filterSectionFragment.isTextSelectedVisible(false)
            attachSimpleFileListFragment()
        }

        appBarFragment.onBackPressed = ::onBackPressed
        filterSectionFragment.onTapButtonSelectSnapshotAssociate = ::showCheckBoxes
        filterSectionFragment.onTapButtonOpenFilters = ::showFilterDialog
        binding.buttonAssociatePartnerIdList.setOnClickListenerCheckConnection { showAssignToOfficerBottomSheet() }
    }

    private fun setAppBarFragment() {
        when (listType) {
            Constants.SNAPSHOT_LIST -> {
                appBarFragment =
                    AppBarX1Fragment.createInstance(getString(R.string.snapshots_title), true)
            }
            Constants.VIDEO_LIST -> {
                appBarFragment =
                    AppBarX1Fragment.createInstance(getString(R.string.videos_title))
            }
        }
    }

    private fun attachAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX1Fragment.TAG
        )
    }

    private fun attachFilterSectionFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.selectedSectionItems,
            fragment = filterSectionFragment,
            tag = FilterSectionX1Fragment.TAG
        )
    }

    private fun showAssignToOfficerBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showCheckBoxes() {
        if (filterSectionFragment.isButtonSelectedSnapshotActive()) {
            filterSectionFragment.resetButtonAssociateSnapshot(listType)
            resetButtonAssociate()
        } else {
            filterSectionFragment.activateButtonAssociate()
            activateButtonAssociate()
        }
    }

    private fun showFilterDialog() {
        val listToFilter = getListToFilter()
        if (filterDialog == null) createFilterDialog()
        applyFilterDialog(listToFilter)
    }

    private fun createFilterDialog() {
        filterDialog =
            filterSectionFragment.createFilterDialog {
                filterSectionFragment.updateFilterButtonState(it)
                handleOnApplyFilterClick()
            }
    }
}
