package com.lawmobile.presentation.ui.fileList

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityFileListBinding
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment.Companion.checkableListInit
import com.lawmobile.presentation.ui.fileList.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.widgets.CustomFilterDialog
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

open class FileListBaseActivity : BaseActivity() {

    lateinit var binding: ActivityFileListBinding
    private val fileListViewModel: FileListViewModel by viewModels()

    lateinit var actualFragment: String
    var listType: String? = null
    var filterDialog: CustomFilterDialog? = null

    val simpleFileListFragment = SimpleFileListFragment()
    val thumbnailFileListFragment = ThumbnailFileListFragment()
    var onPartnerIdAssociated: (() -> Unit)? = null

    val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetPartnerId.bottomSheetPartnerId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listType = intent.extras?.getString(Constants.FILE_LIST_SELECTOR)
        setExtras()
        setObservers()
        configureBottomSheet()
    }

    override fun onRestart() {
        super.onRestart()
        VLCMediaPlayer.currentProgress = 0
    }

    private fun setExtras() {
        simpleFileListFragment.arguments =
            Bundle().apply { putString(Constants.FILE_LIST_TYPE, listType) }
        thumbnailFileListFragment.arguments =
            Bundle().apply { putString(Constants.FILE_LIST_TYPE, listType) }
    }

    fun setObservers() {
        fileListViewModel.snapshotPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
        fileListViewModel.videoPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
    }

    fun activateButtonAssociate() {
        when (actualFragment) {
            Constants.SIMPLE_FILE_LIST -> {
                simpleFileListFragment.showCheckBoxes()
                simpleFileListFragment.simpleFileListAdapter?.uncheckAllItems()
            }
            Constants.THUMBNAIL_FILE_LIST -> {
                thumbnailFileListFragment.showCheckBoxes()
                thumbnailFileListFragment.thumbnailFileListAdapter?.uncheckAllItems()
            }
        }
    }

    fun attachSimpleFileListFragment() {
        actualFragment = Constants.SIMPLE_FILE_LIST
        resetButtonAssociate()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            simpleFileListFragment,
            Constants.SIMPLE_FILE_LIST
        )
    }

    fun attachThumbnailListFragment() {
        actualFragment = Constants.THUMBNAIL_FILE_LIST
        resetButtonAssociate()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            thumbnailFileListFragment,
            Constants.THUMBNAIL_FILE_LIST
        )
    }

    fun resetButtonAssociate() {
        when (actualFragment) {
            Constants.SIMPLE_FILE_LIST -> simpleFileListFragment.reviewIfShowCheckBoxes()
            Constants.THUMBNAIL_FILE_LIST -> thumbnailFileListFragment.reviewIfShowCheckBoxes()
        }
        binding.buttonAssociatePartnerIdList.isVisible = false
    }

    private fun configureBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.bottomSheetPartnerId.buttonAssignToOfficer.setOnClickListenerCheckConnection {
            associatePartnerId(binding.bottomSheetPartnerId.editTextAssignToOfficer.text.toString())
            hideKeyboard()
            cleanPartnerIdField()
        }
        binding.bottomSheetPartnerId.buttonCloseAssignToOfficer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            cleanPartnerIdField()
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // The interface requires to implement this method but not needed
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN ->
                            binding.shadowFileListView.isVisible =
                                false
                        else -> binding.shadowFileListView.isVisible = true
                    }
                }
            })
    }

    private fun cleanPartnerIdField() {
        binding.bottomSheetPartnerId.editTextAssignToOfficer.text.clear()
    }

    private fun associatePartnerId(partnerId: String) {
        if (partnerId.isEmpty()) {
            binding.constraintLayoutFileList.showErrorSnackBar(getString(R.string.valid_partner_id_message))
            return
        }
        showLoadingDialog()
        associateItemsSelected(getListSelectedToAssociatePartnerId(), partnerId)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun getListSelectedToAssociatePartnerId(): List<DomainCameraFile>? {
        return when (actualFragment) {
            Constants.SIMPLE_FILE_LIST ->
                simpleFileListFragment.simpleFileListAdapter?.fileList?.filter { it.isSelected }
                    ?.map { it.domainCameraFile }
            Constants.THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.thumbnailFileListAdapter?.fileList?.filter { it.isSelected }
                    ?.map { it.domainCameraFile }
            else -> null
        }
    }

    private fun associateItemsSelected(
        listSelected: List<DomainCameraFile>?,
        partnerId: String
    ) {
        listSelected?.let {
            when (listType) {
                Constants.SNAPSHOT_LIST -> {
                    fileListViewModel.associatePartnerIdToSnapshotList(it, partnerId)
                }
                Constants.VIDEO_LIST -> {
                    fileListViewModel.associatePartnerIdToVideoList(it, partnerId)
                }
            }
        }
    }

    fun handleOnApplyFilterClick() {
        when (actualFragment) {
            Constants.SIMPLE_FILE_LIST -> simpleFileListFragment.applyFiltersToList()
            Constants.THUMBNAIL_FILE_LIST -> thumbnailFileListFragment.applyFiltersToList()
        }
    }

    private fun handlePartnerIdResult(result: Result<Unit>) {
        with(result) {
            doIfSuccess {
                binding.constraintLayoutFileList.showSuccessSnackBar(getString(R.string.file_list_associate_partner_id_success))
                resetButtonAssociate()
                onPartnerIdAssociated?.invoke()
            }
            doIfError {
                binding.constraintLayoutFileList.showErrorSnackBar(
                    it.message ?: getString(R.string.file_list_associate_partner_id_error)
                )
            }
        }
        hideLoadingDialog()
    }

    fun enableAssociatePartnerButton(activate: Boolean) {
        binding.buttonAssociatePartnerIdList.run {
            isVisible = activate
            isActivated = activate
        }
    }

    fun getListToFilter(): List<DomainInformationForList> {
        return when (actualFragment) {
            Constants.SIMPLE_FILE_LIST ->
                simpleFileListFragment.fileListBackup
            Constants.THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.fileListBackup
            else -> emptyList()
        }
    }

    fun applyFilterDialog(list: List<DomainInformationForList>) {
        filterDialog?.apply {
            this.listToFilter = list
            show()
            when (listType) {
                Constants.VIDEO_LIST -> filterDialog?.isEventSpinnerFilterVisible(true)
                Constants.SNAPSHOT_LIST -> filterDialog?.isEventSpinnerFilterVisible(false)
            }
            simpleFileListFragment.filter = this
            thumbnailFileListFragment.filter = this
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkableListInit = false
    }
}
