package com.lawmobile.presentation.ui.audioDetail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityAudioDetailBinding
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AudioDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAudioDetailBinding

    private val audioDetailViewModel: AudioDetailViewModel by viewModels()
    private lateinit var audioFile: DomainCameraFile
    private var domainAudioInformation: DomainInformationAudioMetadata? = null
    private lateinit var currentAssociatedOfficerId: String

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetPartnerId.bottomSheetAssociateOfficer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAppBar()
        getInformationFromIntent()
        setInformationOfAudio()
        setObservers()
        configureListeners()
        configureBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        restartVisibility()
        getAudioBytes()
        hideKeyboard()
    }

    private fun getAudioBytes() {
        showLoadingDialog()
        audioDetailViewModel.getAudioBytes(audioFile)
    }

    private fun getInformationFromIntent() {
        val fileIntent =
            intent.getSerializableExtra(Constants.DOMAIN_CAMERA_FILE) as? DomainCameraFile
        fileIntent?.let { audioFile = it }
    }

    private fun setAppBar() {
        binding.layoutCustomAppBar.textViewTitle.text = getString(R.string.audio_item_detail_title)
        binding.layoutCustomAppBar.buttonSimpleList.isVisible = false
        binding.layoutCustomAppBar.buttonThumbnailList.isVisible = false
    }

    private fun configureListeners() {
        binding.buttonAssociatePartnerIdList.setOnClickListenerCheckConnection {
            showAssignToOfficerBottomSheet()
        }
        binding.layoutCustomAppBar.imageButtonBackArrow.setOnClickListenerCheckConnection { onBackPressed() }
        binding.imageReload.setOnClickListenerCheckConnection {
            showLoadingDialog()
            audioDetailViewModel.getAudioBytes(audioFile)
        }
    }

    private fun setObservers() {
        audioDetailViewModel.audioBytesResult.observe(this, ::manageGetBytesAudio)
        audioDetailViewModel.audioInformationResult.observe(this, ::manageAudioInformation)
        audioDetailViewModel.savePartnerIdResult.observe(this, ::manageSavePartnerId)
        audioDetailViewModel.associatedVideosResult.observe(this, ::manageAssociatedVideos)
    }

    private fun manageAssociatedVideos(result: Result<List<DomainCameraFile>>) {
        with(result) {
            doIfSuccess {
                if (it.isNotEmpty()) {
                    var textInVideos = ""
                    it.forEachIndexed { position, domainCameraFile ->
                        textInVideos += domainCameraFile.getDateDependingOnNameLength()
                        if (position != it.lastIndex) textInVideos += "\n"
                    }

                    binding.videosAssociatedValue.text = textInVideos
                } else binding.videosAssociatedValue.text = getString(R.string.none)
            }
            doIfError {
                binding.videosAssociatedValue.text = getString(R.string.not_available)
                showInformationError { audioDetailViewModel.getAssociatedVideos(audioFile) }
            }
            hideLoadingDialog()
        }
    }

    private fun manageGetBytesAudio(result: Result<ByteArray>) {
        with(result) {
            doIfSuccess {
                val path = it.getPathFromTemporalFile(
                    context = applicationContext,
                    name = audioFile.name
                )
                // pending to use the audio bytes
            }
            doIfError {
                binding.imageReload.isVisible = true
            }
        }

        if (domainAudioInformation == null) audioDetailViewModel.getAudioInformation(audioFile)
        else hideLoadingDialog()
    }

    private fun manageAudioInformation(result: Result<DomainInformationAudioMetadata>) {
        with(result) {
            doIfSuccess { domainInformation ->
                domainAudioInformation = domainInformation
                setAudioMetadata()
                audioDetailViewModel.getAssociatedVideos(audioFile)
            }
            doIfError {
                showInformationNotAvailable()
                showInformationError { audioDetailViewModel.getAudioInformation(audioFile) }
            }
        }
    }

    private fun showInformationError(callback: () -> Unit) {
        binding.constraintLayoutDetail.showErrorSnackBar(
            getString(R.string.audio_detail_metadata_error),
            AUDIO_DETAIL_ERROR_ANIMATION_DURATION
        ) {
            this@AudioDetailActivity.verifySessionBeforeAction {
                showLoadingDialog()
                callback()
            }
        }
    }

    private fun showInformationNotAvailable() {
        binding.officerValue.text = getString(R.string.not_available)
        binding.videosAssociatedValue.text = getString(R.string.not_available)
    }

    private fun manageSavePartnerId(result: Result<Unit>) {
        hideLoadingDialog()
        with(result) {
            doIfSuccess {
                binding.constraintLayoutDetail.showSuccessSnackBar(
                    getString(R.string.file_list_associate_partner_id_success)
                )
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                setCurrentOfficerAssociatedInView()
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
        binding.bottomSheetPartnerId.buttonAssignToOfficer.setOnClickListenerCheckConnection {
            currentAssociatedOfficerId =
                binding.bottomSheetPartnerId.editTextAssignToOfficer.text.toString()
            associatePartnerId(currentAssociatedOfficerId)
            hideKeyboard()
            cleanPartnerIdField()
        }
        binding.bottomSheetPartnerId.buttonClose.setOnClickListenerCheckConnection {
            hideKeyboard()
            cleanPartnerIdField()
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

    private fun cleanPartnerIdField() {
        binding.bottomSheetPartnerId.editTextAssignToOfficer.text?.clear()
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
        audioDetailViewModel.savePartnerId(audioFile, partnerId)
    }

    private fun setInformationOfAudio() {
        binding.audioNameValue.text = audioFile.name
        binding.dateTimeValue.text = audioFile.date
    }

    private fun setAudioMetadata() {
        setOfficerAssociatedInView()
        // setVideosAssociatedInView()
    }

    private fun setCurrentOfficerAssociatedInView() {
        binding.officerValue.text =
            if (currentAssociatedOfficerId.isEmpty()) getString(R.string.none)
            else currentAssociatedOfficerId
    }

    private fun setOfficerAssociatedInView() {
        domainAudioInformation?.audioMetadata?.metadata?.partnerID?.let {
            currentAssociatedOfficerId = if (it.isEmpty()) getString(R.string.none) else it
        } ?: run {
            currentAssociatedOfficerId = getString(R.string.none)
        }

        binding.officerValue.text = currentAssociatedOfficerId
    }

    private fun setVideosAssociatedInView() {
        domainAudioInformation?.videosAssociated?.let {
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

    private fun restartVisibility() {
        binding.photoItemDetailHolder.isVisible = true
        binding.imageReload.isVisible = false
    }

    companion object {
        private const val AUDIO_DETAIL_ERROR_ANIMATION_DURATION = 7000
    }
}
