package com.lawmobile.presentation.ui.snapshotDetail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.convertBitmap
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import com.safefleet.mobile.commons.helpers.hideKeyboard
import kotlinx.android.synthetic.main.activity_snapshot_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_assign_to_officer.*
import kotlinx.android.synthetic.main.custom_app_bar.*

class SnapshotDetailActivity : BaseActivity() {

    private val snapshotDetailViewModel: SnapshotDetailViewModel by viewModels()
    private lateinit var file: CameraConnectFile
    private var domainInformationImageMetadata: DomainInformationImageMetadata? = null
    private var temporalPartnerToShowIfItSaved: String = ""

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            bottomSheetPartnerId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot_item_detail)
        setAppBar()
        configureObserve()
        configureListeners()
        configureBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        showLoadingDialog()
        file = intent.getSerializableExtra(Constants.CAMERA_CONNECT_FILE) as CameraConnectFile
        snapshotDetailViewModel.getInformationImageMetadata(file)
        hideKeyboard()
    }

    private fun setAppBar() {
        fileListAppBarTitle.text = getString(R.string.snapshot_item_detail_title)
        buttonSimpleList.isVisible = false
        buttonThumbnailList.isVisible = false
    }

    private fun configureListeners() {
        buttonAssociatePartnerIdList.setOnClickListenerCheckConnection {
            showAssignToOfficerBottomSheet()
        }
        backArrowFileListAppBar.setOnClickListenerCheckConnection { onBackPressed() }
    }

    private fun configureObserve() {
        snapshotDetailViewModel.imageBytesLiveData.observe(this, Observer(::manageGetBytesImage))
        snapshotDetailViewModel.informationImageLiveData.observe(
            this,
            Observer(::manageInformationImage)
        )
        snapshotDetailViewModel.savePartnerIdLiveData.observe(this, Observer(::manageSavePartnerId))
    }

    private fun manageGetBytesImage(result: Result<ByteArray>) {
        with(result) {
            doIfSuccess { setImageAndData(it) }
            doIfError {
                constraintLayoutDetail.showErrorSnackBar(getString(R.string.snapshot_detail_load_failed))
            }
        }
        hideLoadingDialog()
    }

    private fun manageInformationImage(result: Result<DomainInformationImageMetadata>) {
        with(result) {
            doIfSuccess {
                domainInformationImageMetadata = it
            }
            doIfError {
                constraintLayoutDetail.showErrorSnackBar(getString(R.string.snapshot_item_detail_get_information_metadata_error))
            }
        }

        snapshotDetailViewModel.getImageBytes(file)
    }

    private fun manageSavePartnerId(result: Result<Unit>) {
        with(result) {
            doIfSuccess {
                constraintLayoutDetail.showSuccessSnackBar(getString(R.string.file_list_associate_partner_id_success))
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                officerValue.text = temporalPartnerToShowIfItSaved
            }
            doIfError {
                constraintLayoutDetail.showErrorSnackBar(getString(R.string.file_list_associate_partner_id_error))
            }
        }

        hideLoadingDialog()
    }

    private fun configureBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        buttonAssignToOfficer.setOnClickListenerCheckConnection {
            associatePartnerId(editTextAssignToOfficer.text.toString())
            hideKeyboard()
        }
        buttonCloseAssignToOfficer.setOnClickListenerCheckConnection {
            hideKeyboard()
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        hideKeyboard()
                        shadowDetailView.isVisible = false
                    }
                    else -> shadowDetailView.isVisible = true
                }
            }
        })
    }

    private fun showAssignToOfficerBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun associatePartnerId(partnerId: String) {

        if (partnerId.isEmpty()) {
            constraintLayoutDetail.showErrorSnackBar(getString(R.string.valid_partner_id_message))
            return
        }
        showLoadingDialog()
        temporalPartnerToShowIfItSaved = partnerId
        snapshotDetailViewModel.savePartnerId(file, partnerId)
    }

    private fun setImageAndData(byteArray: ByteArray) {
        try {
            photoItemDetailHolder.setImageBitmap(byteArray.convertBitmap())
        } catch (e: Exception) {
            photoItemDetailHolder.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_failed_image,
                    null
                )
            )
        }

        photoNameValue.text = file.name
        dateTimeValue.text = file.date
        officerValue.text =
            domainInformationImageMetadata?.cameraConnectPhotoMetadata?.metadata?.partnerID ?: ""
    }
}
