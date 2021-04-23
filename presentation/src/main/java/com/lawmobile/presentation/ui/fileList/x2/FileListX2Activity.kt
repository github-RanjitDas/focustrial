package com.lawmobile.presentation.ui.fileList.x2

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.MenuInformation
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.closeMenuButton
import com.lawmobile.presentation.extensions.openMenuButton
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment
import com.lawmobile.presentation.ui.fileList.FileListBaseActivity
import com.lawmobile.presentation.ui.fileList.filterSection.x2.FilterSectionX2Fragment
import com.lawmobile.presentation.utils.Constants

class FileListX2Activity : FileListBaseActivity() {

    private val menuFragment = MenuFragment()
    private lateinit var menuInformation: MenuInformation
    private lateinit var appBarFragment: AppBarX2Fragment
    private lateinit var filterSectionFragment: FilterSectionX2Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentsDependingOnListType()
        attachFragments()
        setListeners()
        menuInformation = MenuInformation(this, menuFragment, binding.layoutCustomMenu.shadowOpenMenuView)
    }

    override fun onResume() {
        super.onResume()
        filterSectionFragment.isSimpleListActivity(actualFragment == Constants.SIMPLE_FILE_LIST)
    }

    private fun attachFragments() {
        attachAppBarFragment()
        attachFilterSectionFragment()
        attachListTypeFragment()
        attachMenuFragment()
    }

    private fun attachListTypeFragment() {
        when (listType) {
            Constants.VIDEO_LIST -> {
                filterSectionFragment.isSimpleListActivity(true)
                attachSimpleFileListFragment()
            }
            Constants.SNAPSHOT_LIST -> {
                filterSectionFragment.isSimpleListActivity(false)
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

        filterSectionFragment.onTapSimpleList = {
            filterSectionFragment.isSimpleListActivity(true)
            filterSectionFragment.resetButtonSelectToAssociate()
            attachSimpleFileListFragment()
        }

        filterSectionFragment.onTapThumbnail = {
            filterSectionFragment.isSimpleListActivity(false)
            filterSectionFragment.resetButtonSelectToAssociate()
            attachThumbnailListFragment()
        }

        appBarFragment.onBackPressed = ::onBackPressed
        binding.buttonAssociatePartnerIdList.setOnClickListenerCheckConnection { showAssignToOfficerBottomSheet() }
        filterSectionFragment.onTapSelectButtonToAssociate = ::showCheckBoxes
        filterSectionFragment.onTapButtonOpenFilters = ::showFilterDialog

        menuFragment.onCloseMenuButton = {
            binding.layoutCustomMenu.menuContainer.closeMenuButton(menuInformation)
        }
        appBarFragment.onTapMenuButton = {
            binding.layoutCustomMenu.menuContainer.openMenuButton(menuInformation)
        }

        onPartnerIdAssociated = {
            filterSectionFragment.resetButtonSelectToAssociate()
        }
    }

    private fun setFragmentsDependingOnListType() {
        when (listType) {
            Constants.SNAPSHOT_LIST -> {
                appBarFragment =
                    AppBarX2Fragment.createInstance(false, getString(R.string.snapshots_title))
                filterSectionFragment = FilterSectionX2Fragment.createInstance(true)
            }
            Constants.VIDEO_LIST -> {
                appBarFragment =
                    AppBarX2Fragment.createInstance(false, getString(R.string.videos_title))
                filterSectionFragment = FilterSectionX2Fragment.createInstance(false)
            }
        }
    }

    private fun attachMenuFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.menuContainer,
            fragment = menuFragment,
            tag = MenuFragment.TAG
        )
    }

    private fun attachAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX2Fragment.TAG
        )
    }

    private fun attachFilterSectionFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.selectedSectionItems,
            fragment = filterSectionFragment,
            tag = FilterSectionX2Fragment.TAG
        )
    }

    private fun showAssignToOfficerBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showCheckBoxes() {
        if (filterSectionFragment.isButtonSelectToAssociateActive()) {
            filterSectionFragment.resetButtonSelectToAssociate()
            resetButtonAssociate()
        } else {
            filterSectionFragment.activateSelectButtonToAssociate()
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
