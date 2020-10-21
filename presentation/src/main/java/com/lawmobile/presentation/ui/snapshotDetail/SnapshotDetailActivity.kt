package com.lawmobile.presentation.ui.snapshotDetail

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.ImageFilesPathManager
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment.Companion.PATH_ERROR_IN_PHOTO
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.*
import kotlinx.android.synthetic.main.activity_snapshot_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_assign_to_officer.*
import kotlinx.android.synthetic.main.custom_app_bar.*
import java.io.File


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
        getInformationFromIntent()
        setInformationOfSnapshot()
        configureObserve()
        configureListeners()
        configureBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        restartVisibility()
        getSnapshotBytes()
        hideKeyboard()
    }

    private fun getSnapshotBytes() {
        showLoadingDialog()
        val fileSaved = ImageFilesPathManager.getImageIfExist(file.name)
        fileSaved?.let {
            if (it.absolutePath != PATH_ERROR_IN_PHOTO && File(it.absolutePath).exists()) {
                setImageWithPath(it.absolutePath)
                snapshotDetailViewModel.getInformationImageMetadata(file)
                return
            }
        }

        snapshotDetailViewModel.getImageBytes(file)
    }

    private fun getInformationFromIntent() {
        val fileIntent =
            intent.getSerializableExtra(Constants.CAMERA_CONNECT_FILE) as? CameraConnectFile
        fileIntent?.let {
            file = it
        }
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
        buttonFullScreen.setOnClickListenerCheckConnection { changeOrientationInView() }
        imageReload.setOnClickListenerCheckConnection {
            showLoadingDialog()
            snapshotDetailViewModel.getImageBytes(file)
        }
    }

    private fun changeOrientationInView() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            return
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun configureObserve() {
        snapshotDetailViewModel.imageBytesLiveData.observe(this, Observer(::manageGetBytesImage))
        snapshotDetailViewModel.informationImageLiveData.observe(
            this,
            Observer(::manageInformationImage)
        )
        snapshotDetailViewModel.savePartnerIdLiveData.observe(this, Observer(::manageSavePartnerId))
    }

    private fun manageGetBytesImage(event: Event<Result<ByteArray>>) {
        event.getContentIfNotHandled()?.run {
            with(this) {
                doIfSuccess {
                    val path = it.getPathFromTemporalFile(
                        context = applicationContext,
                        name = file.name
                    )
                    setImageWithPath(path)
                }
                doIfError {
                    imageReload.isVisible = true
                }
            }
        }

        if (domainInformationImageMetadata == null)
            snapshotDetailViewModel.getInformationImageMetadata(file)
        else hideLoadingDialog()
    }

    private fun manageInformationImage(event: Event<Result<DomainInformationImageMetadata>>) {
        event.getContentIfNotHandled()?.run {
            with(this) {
                doIfSuccess {
                    domainInformationImageMetadata = it
                    setSnapshotMetadata()
                }
                doIfError {
                    showMetadataNotAvailable()
                    constraintLayoutDetail.showErrorSnackBar(
                        getString(R.string.snapshot_detail_metadata_error),
                        SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION
                    ) {
                        showLoadingDialog()
                        snapshotDetailViewModel.getInformationImageMetadata(file)
                    }
                }
            }
            hideLoadingDialog()
        }
    }

    private fun showMetadataNotAvailable() {
        officerValue.text = getString(R.string.not_available)
        videosAssociatedValue.text = getString(R.string.not_available)
    }

    private fun manageSavePartnerId(result: Result<Unit>) {
        hideLoadingDialog()
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
            override fun onSlide(bottomSheet: View, slideOffset: Float){}

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

    private fun setImageWithPath(path: String) {
        if (path.imageHasCorrectFormat()) {
            try {
                ImageFilesPathManager.saveImageWithPath(ImageWithPathSaved(file.name, path))
                Glide.with(this).load(File(path)).into(photoItemDetailHolder)
                imageReload.isVisible = false
            } catch (e: Exception) {
                imageFailed.isVisible = true
                showFailedLoadImageError()
            }
        } else {
            imageFailed.isVisible = true
            showFailedLoadImageError()
        }
    }

    private fun showFailedLoadImageError() {
        constraintLayoutDetail.showErrorSnackBar(
            getString(R.string.snapshot_detail_load_failed),
            SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION
        ) {
            snapshotDetailViewModel.getImageBytes(file)
        }
    }

    private fun setInformationOfSnapshot() {
        photoNameValue.text = file.name
        dateTimeValue.text = file.date
    }

    private fun setSnapshotMetadata() {
        setOfficerAssociatedInView()
        setVideosAssociatedInView()
    }

    private fun setOfficerAssociatedInView() {
        domainInformationImageMetadata?.cameraConnectPhotoMetadata?.metadata?.partnerID?.let {
            officerValue.text = it
        } ?: run {
            officerValue.text = getString(R.string.none)
        }
    }

    private fun setVideosAssociatedInView() {
        domainInformationImageMetadata?.videosAssociated?.let {
            if (it.isNotEmpty()) {
                var textInVideos = ""
                for (position in it.indices) {
                    textInVideos += it[position].getCreationDate()
                    if (position != it.lastIndex) textInVideos += "\n"
                }

                videosAssociatedValue.text = textInVideos
                return
            }
        }

        videosAssociatedValue.text = getString(R.string.none)
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeOrientationInView()
        } else {
            super.onBackPressed()
        }
    }

    private fun restartVisibility() {
        photoItemDetailHolder.isVisible = true
        imageReload.isVisible = false
        imageFailed.isVisible = false
    }

    companion object {
        private const val SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION = 7000
    }

}
