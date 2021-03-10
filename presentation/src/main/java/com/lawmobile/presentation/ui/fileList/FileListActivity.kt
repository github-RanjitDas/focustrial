package com.lawmobile.presentation.ui.fileList

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityFileListBinding
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createFilterDialog
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment.Companion.checkableListInit
import com.lawmobile.presentation.ui.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SIMPLE_FILE_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.THUMBNAIL_FILE_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.lawmobile.presentation.widgets.CustomFilterDialog
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class FileListActivity : BaseActivity() {

    private lateinit var activityFileListBinding: ActivityFileListBinding

    private val fileListViewModel: FileListViewModel by viewModels()

    private val simpleFileListFragment = SimpleFileListFragment()
    private val thumbnailFileListFragment = ThumbnailFileListFragment()

    private lateinit var actualFragment: String
    private var listType: String? = null

    private var filterDialog: CustomFilterDialog? = null

    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(activityFileListBinding.bottomSheetPartnerId.bottomSheetPartnerId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFileListBinding = ActivityFileListBinding.inflate(layoutInflater)
        setContentView(activityFileListBinding.root)

        listType = intent.extras?.getString(FILE_LIST_SELECTOR)

        setExtras()
        setObservers()
        setCustomAppBar()
        setListTypeFragment()
        setListeners()
        configureBottomSheet()
    }

    private fun setListTypeFragment() {
        when (listType) {
            VIDEO_LIST -> setSimpleFileListFragment()
            SNAPSHOT_LIST -> setThumbnailListFragment()
        }
    }

    private fun setCustomAppBar() {
        when (listType) {
            SNAPSHOT_LIST -> {
                activityFileListBinding.layoutFileListAppBar.fileListAppBarTitle.text =
                    getString(R.string.snapshots_title)
            }
            VIDEO_LIST -> {
                activityFileListBinding.layoutFileListAppBar.fileListAppBarTitle.text =
                    getString(R.string.videos_title)
                activityFileListBinding.layoutFileListAppBar.buttonThumbnailList.isVisible = false
                activityFileListBinding.layoutFileListAppBar.buttonSimpleList.isVisible = false
            }
        }
    }

    private fun setExtras() {
        simpleFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, listType) }
        thumbnailFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, listType) }
    }

    private fun setObservers() {
        fileListViewModel.snapshotPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
        fileListViewModel.videoPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
    }

    private fun setSimpleFileListFragment() {
        actualFragment = SIMPLE_FILE_LIST
        activityFileListBinding.layoutFileListAppBar.buttonSimpleList.isActivated = true
        activityFileListBinding.layoutFileListAppBar.buttonThumbnailList.isActivated = false
        activityFileListBinding.textViewSelectedItems.isVisible = false
        resetButtonAssociate()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            simpleFileListFragment,
            SIMPLE_FILE_LIST
        )
    }

    private fun setThumbnailListFragment() {
        actualFragment = THUMBNAIL_FILE_LIST
        activityFileListBinding.layoutFileListAppBar.buttonThumbnailList.isActivated = true
        activityFileListBinding.layoutFileListAppBar.buttonSimpleList.isActivated = false
        activityFileListBinding.textViewSelectedItems.isVisible = false
        resetButtonAssociate()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            thumbnailFileListFragment,
            THUMBNAIL_FILE_LIST
        )
    }

    private fun setListeners() {
        simpleFileListFragment.onFileCheck = ::enableAssociatePartnerButton
        thumbnailFileListFragment.onImageCheck = ::enableAssociatePartnerButton
        activityFileListBinding.layoutFileListAppBar.buttonThumbnailList.setClickListenerCheckConnection { setThumbnailListFragment() }
        activityFileListBinding.layoutFileListAppBar.buttonSimpleList.setClickListenerCheckConnection { setSimpleFileListFragment() }
        activityFileListBinding.layoutFileListAppBar.backArrowFileListAppBar.setOnClickListenerCheckConnection { onBackPressed() }
        activityFileListBinding.buttonSelectSnapshotsToAssociate.setOnClickListenerCheckConnection { showCheckBoxes() }
        activityFileListBinding.buttonAssociatePartnerIdList.setOnClickListenerCheckConnection { showAssignToOfficerBottomSheet() }
        activityFileListBinding.buttonOpenFilters.setOnClickListenerCheckConnection { showFilterDialog() }
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
                activityFileListBinding.layoutFilterTags.createFilterDialog(::handleOnApplyFilterClick)
        }

        filterDialog?.apply {
            this.listToFilter = listToFilter
            show()
            when (listType) {
                VIDEO_LIST -> filterDialog?.isEventSpinnerFilterVisible(true)
                SNAPSHOT_LIST -> filterDialog?.isEventSpinnerFilterVisible(false)
            }
            simpleFileListFragment.filter = this
            thumbnailFileListFragment.filter = this
        }
    }

    private fun handleOnApplyFilterClick(it: Boolean) {
        activityFileListBinding.scrollFilterTags.isVisible = it
        updateFilterButtonState(it)
        when (actualFragment) {
            SIMPLE_FILE_LIST ->
                simpleFileListFragment.applyFiltersToList()
            THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.applyFiltersToList()
        }
    }

    private fun updateFilterButtonState(it: Boolean) {
        with(activityFileListBinding.buttonOpenFilters) {
            background = if (it) {
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

    private fun configureBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        activityFileListBinding.bottomSheetPartnerId.buttonAssignToOfficer.setOnClickListenerCheckConnection {
            associatePartnerId(activityFileListBinding.bottomSheetPartnerId.editTextAssignToOfficer.text.toString())
            hideKeyboard()
        }
        activityFileListBinding.bottomSheetPartnerId.buttonCloseAssignToOfficer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // The interface requires to implement this method but not needed
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN ->
                            activityFileListBinding.shadowFileListView.isVisible =
                                false
                        else -> activityFileListBinding.shadowFileListView.isVisible = true
                    }
                }
            })
    }

    private fun associatePartnerId(partnerId: String) {

        if (partnerId.isEmpty()) {
            activityFileListBinding.constraintLayoutFileList.showErrorSnackBar(getString(R.string.valid_partner_id_message))
            return
        }

        showLoadingDialog()

        val listSelected = when (actualFragment) {
            SIMPLE_FILE_LIST ->
                simpleFileListFragment.simpleFileListAdapter?.fileList?.filter { it.isSelected }
                    ?.map { it.domainCameraFile }
            THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.thumbnailFileListAdapter?.fileList?.filter { it.isSelected }
                    ?.map { it.domainCameraFile }
            else -> throw Exception("List type not supported")
        }

        listSelected?.let {
            when (listType) {
                SNAPSHOT_LIST -> fileListViewModel.associatePartnerIdToSnapshotList(
                    it,
                    partnerId
                )
                VIDEO_LIST -> fileListViewModel.associatePartnerIdToVideoList(
                    it,
                    partnerId
                )
            }
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun resetButtonAssociate() {
        with(activityFileListBinding.buttonSelectSnapshotsToAssociate) {
            isActivated = false
            text = when (listType) {
                VIDEO_LIST -> getString(R.string.select_videos_to_associate)
                else -> getString(R.string.select_snapshots_to_associate)
            }
        }

        when (actualFragment) {
            SIMPLE_FILE_LIST -> {
                if (simpleFileListFragment.simpleFileListAdapter?.showCheckBoxes == true)
                    simpleFileListFragment.showCheckBoxes()
            }
            THUMBNAIL_FILE_LIST -> {
                if (thumbnailFileListFragment.thumbnailFileListAdapter?.showCheckBoxes == true)
                    thumbnailFileListFragment.showCheckBoxes()
            }
        }

        activityFileListBinding.buttonAssociatePartnerIdList.isVisible = false
    }

    private fun activateButtonAssociate() {
        with(activityFileListBinding.buttonSelectSnapshotsToAssociate) {
            isActivated = true
            text = getString(R.string.cancel)
        }
        when (actualFragment) {
            SIMPLE_FILE_LIST -> {
                simpleFileListFragment.showCheckBoxes()
                simpleFileListFragment.simpleFileListAdapter?.uncheckAllItems()
            }
            THUMBNAIL_FILE_LIST -> {
                thumbnailFileListFragment.showCheckBoxes()
                thumbnailFileListFragment.thumbnailFileListAdapter?.uncheckAllItems()
            }
        }
    }

    private fun showCheckBoxes() {
        if (activityFileListBinding.buttonSelectSnapshotsToAssociate.isActivated) resetButtonAssociate()
        else activateButtonAssociate()
    }

    private fun showAssignToOfficerBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun enableAssociatePartnerButton(activate: Boolean, selectedItems: Int) {
        activityFileListBinding.buttonAssociatePartnerIdList.run {
            isVisible = activate
            isActivated = activate
        }

        activityFileListBinding.textViewSelectedItems.run {
            isVisible = selectedItems > 0
            text = getString(R.string.items_selected, selectedItems)
        }
    }

    private fun handlePartnerIdResult(result: Result<Unit>) {
        with(result) {
            doIfSuccess {
                activityFileListBinding.constraintLayoutFileList.showSuccessSnackBar(getString(R.string.file_list_associate_partner_id_success))
                resetButtonAssociate()
            }
            doIfError {
                activityFileListBinding.constraintLayoutFileList.showErrorSnackBar(
                    it.message ?: getString(R.string.file_list_associate_partner_id_error)
                )
            }
        }
        hideLoadingDialog()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkableListInit = false
    }
}
