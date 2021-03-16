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
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.imageHasCorrectFormat
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment.Companion.PATH_ERROR_IN_PHOTO
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import java.io.File

class SnapshotDetailActivity : BaseActivity() {

    private lateinit var binding: ActivitySnapshotItemDetailBinding

    private val snapshotDetailViewModel: SnapshotDetailViewModel by viewModels()
    private lateinit var file: DomainCameraFile
    private var domainInformationImageMetadata: DomainInformationImageMetadata? = null
    private var temporalPartnerToShowIfItSaved: String = ""

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            binding.bottomSheetPartnerId!!.bottomSheetPartnerId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivitySnapshotItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.layoutCustomAppBar?.textViewTitle?.text =
            getString(R.string.snapshot_item_detail_title)
        binding.layoutCustomAppBar?.buttonSimpleList?.isVisible = false
        binding.layoutCustomAppBar?.buttonThumbnailList?.isVisible =
            false
    }

    private fun configureListeners() {
        binding.buttonAssociatePartnerIdList.setOnClickListenerCheckConnection {
            showAssignToOfficerBottomSheet()
        }
        binding.layoutCustomAppBar?.imageButtonBackArrow?.setOnClickListenerCheckConnection { onBackPressed() }
        binding.buttonFullScreen.setOnClickListenerCheckConnection { changeOrientationInView() }
        binding.imageReload.setOnClickListenerCheckConnection {
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
                    binding.imageReload.isVisible = true
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
                    binding.constraintLayoutDetail.showErrorSnackBar(
                        getString(R.string.snapshot_detail_metadata_error),
                        SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION
                    ) {
                        this@SnapshotDetailActivity.verifySessionBeforeAction {
                            showLoadingDialog()
                            snapshotDetailViewModel.getInformationImageMetadata(file)
                        }
                    }
                }
            }
            hideLoadingDialog()
        }
    }

    private fun showMetadataNotAvailable() {
        binding.officerValue.text = getString(R.string.not_available)
        binding.videosAssociatedValue.text =
            getString(R.string.not_available)
    }

    private fun manageSavePartnerId(result: Result<Unit>) {
        hideLoadingDialog()
        with(result) {
            doIfSuccess {
                binding.constraintLayoutDetail.showSuccessSnackBar(
                    getString(R.string.file_list_associate_partner_id_success)
                )
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.officerValue.text = temporalPartnerToShowIfItSaved
            }
            doIfError {
                binding.constraintLayoutDetail.showErrorSnackBar(
                    getString(
                        R.string.file_list_associate_partner_id_error
                    )
                )
            }
        }
    }

    private fun configureBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.bottomSheetPartnerId?.buttonAssignToOfficer?.setOnClickListenerCheckConnection {
            associatePartnerId(binding.bottomSheetPartnerId?.editTextAssignToOfficer?.text.toString())
            hideKeyboard()
        }
        binding.bottomSheetPartnerId?.buttonCloseAssignToOfficer?.setOnClickListenerCheckConnection {
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
                        binding.shadowDetailView.isVisible = false
                    }
                    else -> binding.shadowDetailView.isVisible = true
                }
            }
        })
    }

    private fun showAssignToOfficerBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun associatePartnerId(partnerId: String) {

        if (partnerId.isEmpty()) {
            binding.constraintLayoutDetail.showErrorSnackBar(getString(R.string.valid_partner_id_message))
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
                    .into(binding.photoItemDetailHolder)
                binding.imageReload.isVisible = false
            } catch (e: Exception) {
                binding.imageFailed.isVisible = true
                showFailedLoadImageError()
            }
        } else {
            binding.imageFailed.isVisible = true
            showFailedLoadImageError()
        }
    }

    private fun showFailedLoadImageError() {
        binding.constraintLayoutDetail.showErrorSnackBar(
            getString(R.string.snapshot_detail_load_failed),
            SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION
        ) {
            this.verifySessionBeforeAction { snapshotDetailViewModel.getImageBytes(file) }
        }
    }

    private fun setInformationOfSnapshot() {
        binding.photoNameValue.text = file.name
        binding.dateTimeValue.text = file.date
    }

    private fun setSnapshotMetadata() {
        setOfficerAssociatedInView()
        setVideosAssociatedInView()
    }

    private fun setOfficerAssociatedInView() {
        domainInformationImageMetadata?.cameraConnectPhotoMetadata?.metadata?.partnerID?.let {
            binding.officerValue.text = it
        } ?: run {
            binding.officerValue.text = getString(R.string.none)
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

                binding.videosAssociatedValue.text = textInVideos
                return
            }
        }

        binding.videosAssociatedValue.text = getString(R.string.none)
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeOrientationInView()
        } else {
            super.onBackPressed()
        }
    }

    private fun restartVisibility() {
        binding.photoItemDetailHolder.isVisible = true
        binding.imageReload.isVisible = false
        binding.imageFailed.isVisible = false
    }

    companion object {
        private const val SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION = 7000
    }
}
