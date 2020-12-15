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
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivitySnapshotItemDetailBinding
import com.lawmobile.presentation.entities.ImageFilesPathManager
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment.Companion.PATH_ERROR_IN_PHOTO
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.commons.helpers.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.*
import java.io.File

class SnapshotDetailActivity : BaseActivity() {

    private lateinit var activitySnapshotItemDetailBinding: ActivitySnapshotItemDetailBinding

    private val snapshotDetailViewModel: SnapshotDetailViewModel by viewModels()
    private lateinit var file: DomainCameraFile
    private var domainInformationImageMetadata: DomainInformationImageMetadata? = null
    private var temporalPartnerToShowIfItSaved: String = ""

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            activitySnapshotItemDetailBinding.bottomSheetPartnerId!!.bottomSheetPartnerId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySnapshotItemDetailBinding =
            ActivitySnapshotItemDetailBinding.inflate(layoutInflater)
        setContentView(activitySnapshotItemDetailBinding.root)
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
            intent.getSerializableExtra(Constants.DOMAIN_CAMERA_FILE) as? DomainCameraFile
        fileIntent?.let {
            file = it
        }
    }

    private fun setAppBar() {
        activitySnapshotItemDetailBinding.layoutFileListAppBar?.fileListAppBarTitle?.text =
            getString(R.string.snapshot_item_detail_title)
        activitySnapshotItemDetailBinding.layoutFileListAppBar?.buttonSimpleList?.isVisible = false
        activitySnapshotItemDetailBinding.layoutFileListAppBar?.buttonThumbnailList?.isVisible =
            false
    }

    private fun configureListeners() {
        activitySnapshotItemDetailBinding.buttonAssociatePartnerIdList.setOnClickListenerCheckConnection {
            showAssignToOfficerBottomSheet()
        }
        activitySnapshotItemDetailBinding.layoutFileListAppBar?.backArrowFileListAppBar?.setOnClickListenerCheckConnection { onBackPressed() }
        activitySnapshotItemDetailBinding.buttonFullScreen.setOnClickListenerCheckConnection { changeOrientationInView() }
        activitySnapshotItemDetailBinding.imageReload.setOnClickListenerCheckConnection {
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
                    activitySnapshotItemDetailBinding.imageReload.isVisible = true
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
                    activitySnapshotItemDetailBinding.constraintLayoutDetail.showErrorSnackBar(
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
        activitySnapshotItemDetailBinding.officerValue.text = getString(R.string.not_available)
        activitySnapshotItemDetailBinding.videosAssociatedValue.text =
            getString(R.string.not_available)
    }

    private fun manageSavePartnerId(result: Result<Unit>) {
        hideLoadingDialog()
        with(result) {
            doIfSuccess {
                activitySnapshotItemDetailBinding.constraintLayoutDetail.showSuccessSnackBar(
                    getString(R.string.file_list_associate_partner_id_success)
                )
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                activitySnapshotItemDetailBinding.officerValue.text = temporalPartnerToShowIfItSaved
            }
            doIfError {
                activitySnapshotItemDetailBinding.constraintLayoutDetail.showErrorSnackBar(
                    getString(
                        R.string.file_list_associate_partner_id_error
                    )
                )
            }
        }
    }

    private fun configureBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        activitySnapshotItemDetailBinding.bottomSheetPartnerId?.buttonAssignToOfficer?.setOnClickListenerCheckConnection {
            associatePartnerId(activitySnapshotItemDetailBinding.bottomSheetPartnerId?.editTextAssignToOfficer?.text.toString())
            hideKeyboard()
        }
        activitySnapshotItemDetailBinding.bottomSheetPartnerId?.buttonCloseAssignToOfficer?.setOnClickListenerCheckConnection {
            hideKeyboard()
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // The interface requires to implement this method but not needed
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        hideKeyboard()
                        activitySnapshotItemDetailBinding.shadowDetailView.isVisible = false
                    }
                    else -> activitySnapshotItemDetailBinding.shadowDetailView.isVisible = true
                }
            }
        })
    }

    private fun showAssignToOfficerBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun associatePartnerId(partnerId: String) {

        if (partnerId.isEmpty()) {
            activitySnapshotItemDetailBinding.constraintLayoutDetail.showErrorSnackBar(getString(R.string.valid_partner_id_message))
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
                Glide.with(this).load(File(path))
                    .into(activitySnapshotItemDetailBinding.photoItemDetailHolder)
                activitySnapshotItemDetailBinding.imageReload.isVisible = false
            } catch (e: Exception) {
                activitySnapshotItemDetailBinding.imageFailed.isVisible = true
                showFailedLoadImageError()
            }
        } else {
            activitySnapshotItemDetailBinding.imageFailed.isVisible = true
            showFailedLoadImageError()
        }
    }

    private fun showFailedLoadImageError() {
        activitySnapshotItemDetailBinding.constraintLayoutDetail.showErrorSnackBar(
            getString(R.string.snapshot_detail_load_failed),
            SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION
        ) {
            snapshotDetailViewModel.getImageBytes(file)
        }
    }

    private fun setInformationOfSnapshot() {
        activitySnapshotItemDetailBinding.photoNameValue.text = file.name
        activitySnapshotItemDetailBinding.dateTimeValue.text = file.date
    }

    private fun setSnapshotMetadata() {
        setOfficerAssociatedInView()
        setVideosAssociatedInView()
    }

    private fun setOfficerAssociatedInView() {
        domainInformationImageMetadata?.cameraConnectPhotoMetadata?.metadata?.partnerID?.let {
            activitySnapshotItemDetailBinding.officerValue.text = it
        } ?: run {
            activitySnapshotItemDetailBinding.officerValue.text = getString(R.string.none)
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

                activitySnapshotItemDetailBinding.videosAssociatedValue.text = textInVideos
                return
            }
        }

        activitySnapshotItemDetailBinding.videosAssociatedValue.text = getString(R.string.none)
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeOrientationInView()
        } else {
            super.onBackPressed()
        }
    }

    private fun restartVisibility() {
        activitySnapshotItemDetailBinding.photoItemDetailHolder.isVisible = true
        activitySnapshotItemDetailBinding.imageReload.isVisible = false
        activitySnapshotItemDetailBinding.imageFailed.isVisible = false
    }

    companion object {
        private const val SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION = 7000
    }

}
