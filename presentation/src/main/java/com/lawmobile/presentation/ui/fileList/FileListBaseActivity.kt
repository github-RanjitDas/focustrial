package com.lawmobile.presentation.ui.fileList

import android.os.Bundle
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityFileListBinding
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.shared.FileList
import com.lawmobile.presentation.ui.fileList.shared.FileSelection
import com.lawmobile.presentation.ui.fileList.shared.FilterSection
import com.lawmobile.presentation.ui.fileList.shared.ListTypeButtons
import com.lawmobile.presentation.ui.fileList.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.fileList.state.FileListState
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.widgets.CustomFilterDialog
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess

abstract class FileListBaseActivity : BaseActivity() {

    protected lateinit var binding: ActivityFileListBinding
    protected val viewModel: FileListBaseViewModel by viewModels()

    protected var listType: String? = null

    private lateinit var fileList: FileList
    private val simpleFileListFragment by lazy { SimpleFileListFragment() }
    private val thumbnailFileListFragment by lazy { ThumbnailFileListFragment() }

    protected abstract val listTypeButtons: ListTypeButtons
    protected abstract val fileSelection: FileSelection
    protected abstract val filterSection: FilterSection

    private var currentFilters
        get() = viewModel.currentFilters
        set(value) {
            viewModel.currentFilters = value
        }

    private var isSelectActive: Boolean
        get() = viewModel.isSelectActive
        set(value) {
            viewModel.isSelectActive = value
            toggleFileSelection(value)
            if (!value) filesToAssociate = emptyList()
        }

    private var isFilterDialogOpen: Boolean
        get() = viewModel.isFilterDialogOpen
        set(value) {
            viewModel.isFilterDialogOpen = value
            if (value) showFilterDialog()
        }

    private var isAssociateDialogOpen: Boolean
        get() = viewModel.isAssociateDialogOpen
        set(value) {
            viewModel.isAssociateDialogOpen = value
            toggleAssociateBottomSheet(value)
        }

    private var filesToAssociate: List<DomainCameraFile>?
        get() = viewModel.filesToAssociate
        set(value) {
            viewModel.filesToAssociate = value
            value?.size?.let { fileSelection.onFileSelected(it) }
        }

    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetAssociateOfficer.bottomSheetAssociateOfficer)
    }

    private var state: FileListState?
        get() = viewModel.getFileListState()
        set(value) = viewModel.setFileListState(value)

    protected abstract fun attachAppBarFragment()
    protected abstract fun attachFilterSectionFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservers()
        setListType()
        setActivityListeners()
        restoreAssociationState()
    }

    override fun onResume() {
        super.onResume()
        restoreSelectionState()
        restoreFilterDialogState()
    }

    private fun restoreFilterDialogState() {
        if (isFilterDialogOpen) showFilterDialog()
    }

    private fun setActivityListeners() {
        binding.buttonAssociateListener()
        binding.bottomSheetAssociateListeners()
    }

    fun setFragmentListeners() {
        buttonSelectListener()
        buttonFilterListener()
        buttonSimpleListListener()
        buttonThumbnailListListener()
    }

    private fun buttonThumbnailListListener() {
        listTypeButtons.onThumbnailsClick = {
            if (state is FileListState.Simple) {
                state = FileListState.Thumbnail
                isSelectActive = false
            }
        }
    }

    private fun buttonSimpleListListener() {
        listTypeButtons.onSimpleClick = {
            if (state is FileListState.Thumbnail) {
                state = FileListState.Simple
                isSelectActive = false
            }
        }
    }

    private fun buttonSelectListener() {
        fileSelection.onButtonSelectClick = {
            isSelectActive = !isSelectActive
        }
    }

    private fun buttonFilterListener() {
        filterSection.onButtonFilterClick = {
            isFilterDialogOpen = true
        }
    }

    private fun ActivityFileListBinding.buttonAssociateListener() {
        buttonAssociateOfficer.setOnClickListenerCheckConnection {
            isAssociateDialogOpen = true
        }
    }

    private fun setListType() {
        listType = intent.extras?.getString(Constants.FILE_LIST_SELECTOR)
        setFragmentArguments()
        if (state == null) {
            when (listType) {
                Constants.SNAPSHOT_LIST -> state = FileListState.Thumbnail
                Constants.VIDEO_LIST -> state = FileListState.Simple
            }
        }
    }

    private fun setFragmentArguments() {
        val bundle = Bundle().apply { putString(Constants.FILE_LIST_TYPE, listType) }
        thumbnailFileListFragment.arguments = bundle
        simpleFileListFragment.arguments = bundle
    }

    override fun onRestart() {
        super.onRestart()
        VLCMediaPlayer.currentProgress = 0
    }

    private fun setObservers() {
        viewModel.observeFileListState()
        observeAssociationResult()
    }

    private fun FileListBaseViewModel.observeFileListState() {
        activityCollect(fileListState) {
            it?.run {
                onSimple {
                    listTypeButtons.toggleListType(true)
                    attachSimpleFileListFragment()
                }
                onThumbnail {
                    listTypeButtons.toggleListType(false)
                    attachThumbnailListFragment()
                }
            }
        }
    }

    private fun restoreSelectionState() {
        toggleFileSelection(isSelectActive)
        restoreFileSelection()
    }

    private fun restoreAssociationState() {
        toggleAssociateBottomSheet(isAssociateDialogOpen)
    }

    private fun toggleFileSelection(isActive: Boolean) {
        fileSelection.toggleSelection(isActive)
        fileList.toggleCheckBoxes(isActive)
        if (!isActive) binding.buttonAssociateOfficer.isVisible = false
    }

    private fun restoreFileSelection() {
        if (isSelectActive) {
            fileList.setSelectedFiles(filesToAssociate ?: emptyList())
            fileSelection.onFileSelected(filesToAssociate?.size ?: 0)
        }
    }

    private fun showFilterDialog() {
        fileList.filter?.apply {
            listToFilter = fileList.listBackup
            show()
            isEventSpinnerFilterVisible(isVideoList())
        }
    }

    private fun createFilterDialog(): CustomFilterDialog {
        return filterSection.createFilterDialog(
            ::onApplyFilters,
            ::onCloseFilter
        ).also {
            it.currentFilters = currentFilters
            fileList.filter = it
        }
    }

    private fun onApplyFilters() {
        currentFilters = fileList.filter?.currentFilters ?: mutableListOf()
        fileList.applyFiltersToList()
    }

    private fun isVideoList(): Boolean = listType == Constants.VIDEO_LIST

    private fun onCloseFilter() {
        isFilterDialogOpen = false
    }

    private fun observeAssociationResult() {
        activityCollect(viewModel.associationResult) { result ->
            with(result) {
                doIfSuccess {
                    binding.root.showSuccessSnackBar(getString(R.string.file_list_associate_partner_id_success))
                    isSelectActive = false
                    isAssociateDialogOpen = false
                }
                doIfError {
                    binding.root.showErrorSnackBar(
                        it.message ?: getString(R.string.file_list_associate_partner_id_error),
                        Snackbar.LENGTH_INDEFINITE
                    )
                }
            }
            hideLoadingDialog()
        }
    }

    private fun attachSimpleFileListFragment() {
        fileList = simpleFileListFragment
        configureFileList()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            simpleFileListFragment,
            Constants.SIMPLE_FILE_LIST
        )
    }

    private fun attachThumbnailListFragment() {
        fileList = thumbnailFileListFragment
        configureFileList()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            thumbnailFileListFragment,
            Constants.THUMBNAIL_FILE_LIST
        )
    }

    private fun configureFileList() {
        fileList.filter = createFilterDialog()
        setFileCheckboxListener()
    }

    private fun setFileCheckboxListener() {
        fileList.onFileCheck = { selectedItemsCount ->
            fileSelection.onFileSelected(selectedItemsCount)
            enableButtonAssociateOfficer(selectedItemsCount > 0)
            filesToAssociate = fileList.getListOfSelectedItems()
        }
    }

    private fun ActivityFileListBinding.bottomSheetAssociateListeners() {
        bottomSheetAssociateOfficer.buttonAssignToOfficer.setOnClickListenerCheckConnection {
            val partnerId = bottomSheetAssociateOfficer.editTextAssignToOfficer.text.toString()
            associatePartnerId(partnerId)
        }

        bottomSheetAssociateOfficer.buttonClose.setOnClickListener {
            isAssociateDialogOpen = false
        }
    }

    private fun toggleAssociateBottomSheet(show: Boolean) {
        if (!show) binding.bottomSheetAssociateOfficer.editTextAssignToOfficer.text.clear()
        bottomSheetBehavior.state =
            if (show) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN
        binding.shadowFileListView.isVisible = show
    }

    private fun ActivityFileListBinding.associatePartnerId(partnerId: String) {
        if (partnerId.isEmpty()) {
            root.showErrorSnackBar(getString(R.string.valid_partner_id_message))
            return
        }
        hideKeyboard()
        showLoadingDialog()
        associateSelectedItems(partnerId)
    }

    private fun associateSelectedItems(partnerId: String) {
        when (listType) {
            Constants.SNAPSHOT_LIST -> viewModel.associateOfficerToSnapshots(partnerId)
            Constants.VIDEO_LIST -> viewModel.associateOfficerToVideos(partnerId)
            Constants.AUDIO_LIST -> viewModel.associatePartnerIdToAudios(partnerId)
        }
    }

    private fun enableButtonAssociateOfficer(activate: Boolean) {
        binding.buttonAssociateOfficer.apply {
            isVisible = activate
            isActivated = activate
        }
    }

    override fun onBackPressed() {
        if (isAssociateDialogOpen) {
            isAssociateDialogOpen = false
            return
        } else super.onBackPressed()
    }
}
