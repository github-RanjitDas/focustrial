package com.lawmobile.presentation.ui.snapshotDetail

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivitySnapshotItemDetailBinding
import com.lawmobile.presentation.entities.ImageFilesPathManager
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.imageHasCorrectFormat
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setPortraitOrientation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.toggleDeXFullScreen
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment.Companion.PATH_ERROR_IN_PHOTO
import com.lawmobile.presentation.ui.snapshotDetail.model.SnapshotDetailState
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import java.io.File

class SnapshotDetailActivity : BaseActivity() {

    private lateinit var binding: ActivitySnapshotItemDetailBinding

    private lateinit var imageContainer: ImageView
    private lateinit var imageReload: ImageView
    private lateinit var imageFailed: ImageView

    private val viewModel: SnapshotDetailViewModel by viewModels()

    private var state: SnapshotDetailState
        get() = viewModel.getState()
        set(value) {
            toggleDeXFullScreen()
            viewModel.setState(value)
        }

    private var isAssociateDialogOpen: Boolean
        get() = viewModel.isAssociateDialogOpen
        set(value) {
            viewModel.isAssociateDialogOpen = value
            toggleAssociateDialog(value)
        }

    private lateinit var file: DomainCameraFile
    private var domainInformationImageMetadata: DomainInformationImageMetadata? = null
    private lateinit var currentAssociatedOfficerId: String

    private var isImageLoaded = false

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetAssociateOfficer.bottomSheetAssociateOfficer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnapshotItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isImageLoaded = false
        getFileFromIntent()
        setCollectors()
        toggleAssociateDialog(isAssociateDialogOpen)
        binding.setListeners()
    }

    private fun getFileFromIntent() {
        val cameraFile =
            intent.getSerializableExtra(Constants.DOMAIN_CAMERA_FILE) as? DomainCameraFile
        cameraFile?.let { file = it }
    }

    private fun toggleAssociateDialog(isOpen: Boolean) {
        binding.shadowDetailView.isVisible = isOpen
        sheetBehavior.state =
            if (isOpen) BottomSheetBehavior.STATE_EXPANDED
            else {
                hideKeyboard()
                cleanPartnerIdField()
                BottomSheetBehavior.STATE_HIDDEN
            }
    }

    override fun onResume() {
        super.onResume()
        restartVisibility()
        if (!isImageLoaded) getSnapshotBytes()
        hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        isImageLoaded = false
    }

    private fun getSnapshotBytes() {
        isImageLoaded = true
        showLoadingDialog()
        val fileSaved = ImageFilesPathManager.getImageIfExist(file.name)
        fileSaved?.let {
            if (it.absolutePath != PATH_ERROR_IN_PHOTO && File(it.absolutePath).exists()) {
                setImageWithPath(it.absolutePath)
                viewModel.getImageInformation(file)
                return
            }
        }

        viewModel.getImageBytes(file)
    }

    private fun ActivitySnapshotItemDetailBinding.setAppBar() {
        layoutCustomAppBar.textViewTitle.text = getString(R.string.snapshot_item_detail_title)
        layoutCustomAppBar.buttonSimpleList.isVisible = false
        layoutCustomAppBar.buttonThumbnailList.isVisible = false
    }

    private fun ActivitySnapshotItemDetailBinding.setListeners() {
        bottomSheetListeners()
        buttonFullScreenListener()
        imageReloadListener()
        buttonAssociateListener()
        buttonBackListener()
    }

    private fun ActivitySnapshotItemDetailBinding.buttonBackListener() {
        layoutCustomAppBar.imageButtonBackArrow.setOnClickListenerCheckConnection {
            onBackPressed()
        }
    }

    private fun ActivitySnapshotItemDetailBinding.buttonAssociateListener() {
        buttonAssociateOfficer.setOnClickListenerCheckConnection {
            toggleAssociateDialog(true)
        }
    }

    private fun ActivitySnapshotItemDetailBinding.buttonFullScreenListener() {
        buttonFullScreen.setOnClickListenerCheckConnection {
            state = SnapshotDetailState.FullScreen
        }
        buttonNormalScreen.setOnClickListenerCheckConnection {
            state = SnapshotDetailState.Default
        }
    }

    private fun ActivitySnapshotItemDetailBinding.imageReloadListener() {
        imageViewReload.setOnClickListenerCheckConnection {
            showLoadingDialog()
            viewModel.getImageBytes(file)
        }
    }

    private fun setCollectors() {
        collectState()
        collectImageBytes()
        collectImageInformation()
        collectAssociationResult()
    }

    private fun collectState() {
        activityCollect(viewModel.state) {
            with(it) {
                onDefault {
                    if (!isInPortraitMode()) setPortraitOrientation()
                    binding.setAppBar()
                    setInformationOfSnapshot()
                    setFullscreenVisibility(false)
                    setDefaultViews()
                }
                onFullScreen {
                    setFullscreenVisibility(true)
                    setFullScreenViews()
                }
            }
            getSnapshotBytes()
        }
    }

    private fun setFullScreenViews() = with(binding) {
        imageContainer = imageViewFullScreenSnapshot
        imageReload = imageViewFullScreenReload
        imageFailed = imageViewFullScreenFailed
    }

    private fun setDefaultViews() = with(binding) {
        imageContainer = imageViewSnapshot
        imageReload = imageViewReload
        imageFailed = imageViewFailed
    }

    private fun setFullscreenVisibility(isVisible: Boolean) = with(binding) {
        imageViewFullScreenSnapshot.isVisible = isVisible
        buttonNormalScreen.isVisible = isVisible
        layoutCustomAppBar.layoutCustomAppBar.isVisible = !isVisible
        imageViewSnapshot.isVisible = !isVisible
        buttonFullScreen.isVisible = !isVisible
        scrollLayoutInformation.isVisible = !isVisible
        buttonAssociateOfficer.isVisible = !isVisible
        bottomSheetAssociateOfficer.bottomSheetAssociateOfficer.isVisible = !isVisible
    }

    private fun collectAssociationResult() = with(binding) {
        activityCollect(viewModel.associationResult) {
            hideLoadingDialog()
            with(it) {
                doIfSuccess {
                    constraintLayoutDetail.showSuccessSnackBar(
                        getString(R.string.file_list_associate_partner_id_success)
                    )
                    isAssociateDialogOpen = false
                    setCurrentOfficerAssociatedInView()
                }
                doIfError {
                    constraintLayoutDetail.showErrorSnackBar(
                        getString(R.string.file_list_associate_partner_id_error)
                    )
                }
            }
        }
    }

    private fun collectImageInformation() {
        activityCollect(viewModel.imageInformation) {
            hideLoadingDialog()
            with(it) {
                doIfSuccess { domainInformation ->
                    domainInformationImageMetadata = domainInformation
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
                            viewModel.getImageInformation(file)
                        }
                    }
                }
            }
        }
    }

    private fun collectImageBytes() {
        activityCollect(viewModel.imageBytes) { result ->
            with(result) {
                doIfSuccess {
                    val path = it.getPathFromTemporalFile(applicationContext, file.name)
                    setImageWithPath(path)
                }
                doIfError {
                    imageReload.isVisible = true
                }
            }

            if (domainInformationImageMetadata == null) viewModel.getImageInformation(file)
            else hideLoadingDialog()
        }
    }

    private fun showMetadataNotAvailable() {
        binding.officerValue.text = getString(R.string.not_available)
        binding.videosAssociatedValue.text = getString(R.string.not_available)
    }

    private fun ActivitySnapshotItemDetailBinding.bottomSheetListeners() {
        bottomSheetAssociateOfficer.buttonAssignToOfficer.setOnClickListenerCheckConnection {
            currentAssociatedOfficerId =
                bottomSheetAssociateOfficer.editTextAssignToOfficer.text.toString()
            associatePartnerId(currentAssociatedOfficerId)
            hideKeyboard()
            cleanPartnerIdField()
        }

        bottomSheetAssociateOfficer.buttonClose.setOnClickListenerCheckConnection {
            isAssociateDialogOpen = false
        }
    }

    private fun cleanPartnerIdField() {
        binding.bottomSheetAssociateOfficer.editTextAssignToOfficer.text?.clear()
    }

    private fun associatePartnerId(partnerId: String) {
        if (partnerId.isEmpty()) {
            binding.constraintLayoutDetail.showErrorSnackBar(getString(R.string.valid_partner_id_message))
            return
        }
        showLoadingDialog()
        viewModel.savePartnerId(file, partnerId)
    }

    private fun setImageWithPath(path: String) {
        if (path.imageHasCorrectFormat()) {
            try {
                ImageFilesPathManager.saveImageWithPath(ImageWithPathSaved(file.name, path))
                Glide.with(binding.root).load(File(path)).into(imageContainer)
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
        binding.constraintLayoutDetail.showErrorSnackBar(
            getString(R.string.snapshot_detail_load_failed),
            SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION
        ) {
            this.verifySessionBeforeAction { viewModel.getImageBytes(file) }
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

    private fun setCurrentOfficerAssociatedInView() {
        binding.officerValue.text =
            if (currentAssociatedOfficerId.isEmpty()) getString(R.string.none)
            else currentAssociatedOfficerId
    }

    private fun setOfficerAssociatedInView() {
        domainInformationImageMetadata?.photoMetadata?.metadata?.partnerID?.let {
            currentAssociatedOfficerId = if (it.isEmpty()) getString(R.string.none) else it
        } ?: run {
            currentAssociatedOfficerId = getString(R.string.none)
        }

        binding.officerValue.text = currentAssociatedOfficerId
    }

    private fun setVideosAssociatedInView() {
        domainInformationImageMetadata?.videosAssociated?.let {
            if (it.isNotEmpty()) {
                var textInVideos = ""
                for (position in it.indices) {
                    textInVideos += it[position].getDateDependingOnNameLength()
                    if (position != it.lastIndex) textInVideos += "\n"
                }

                binding.videosAssociatedValue.text = textInVideos
                return
            }
        }

        binding.videosAssociatedValue.text = getString(R.string.none)
    }

    override fun onBackPressed() {
        if (state is SnapshotDetailState.FullScreen) {
            state = SnapshotDetailState.Default
        } else {
            super.onBackPressed()
        }
    }

    private fun restartVisibility() = with(binding) {
        imageViewSnapshot.isVisible = state is SnapshotDetailState.Default
        imageViewFullScreenSnapshot.isVisible = state is SnapshotDetailState.FullScreen
        imageViewReload.isVisible = false
        imageViewFailed.isVisible = false
    }

    companion object {
        private const val SNAPSHOT_DETAIL_ERROR_ANIMATION_DURATION = 7000
    }
}
