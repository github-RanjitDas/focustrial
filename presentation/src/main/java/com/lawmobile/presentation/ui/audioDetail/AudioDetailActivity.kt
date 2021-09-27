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
import com.lawmobile.presentation.databinding.ActivityAudioItemDetailBinding
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
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AudioDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAudioItemDetailBinding

    private val audioDetailViewModel: AudioDetailViewModel by viewModels()
    private lateinit var file: DomainCameraFile
    private var domainAudioInformation: DomainInformationAudioMetadata? = null
    private lateinit var currentAssociatedOfficerId: String

    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            binding.bottomSheetPartnerId.bottomSheetPartnerId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioItemDetailBinding.inflate(layoutInflater)
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
        audioDetailViewModel.getAudioBytes(file)
    }

    private fun getInformationFromIntent() {
        val fileIntent =
            intent.getSerializableExtra(Constants.DOMAIN_CAMERA_FILE) as? DomainCameraFile
        fileIntent?.let { file = it }
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
            audioDetailViewModel.getAudioBytes(file)
        }
    }

    private fun setObservers() {
        audioDetailViewModel.audioBytesLiveData.observe(this, ::manageGetBytesAudio)
        audioDetailViewModel.informationAudioLiveData.observe(this, ::manageInformationAudio)
        audioDetailViewModel.savePartnerIdLiveData.observe(this, ::manageSavePartnerId)
    }

    private fun manageGetBytesAudio(event: Event<Result<ByteArray>>) {
        event.getContentIfNotHandled()?.run {
            with(this) {
                doIfSuccess {
                    val path = it.getPathFromTemporalFile(
                        context = applicationContext,
                        name = file.name
                    )
                    // pending to use the audio bytes
                }
                doIfError {
                    binding.imageReload.isVisible = true
                }
            }
        }

        if (domainAudioInformation == null)
            audioDetailViewModel.getInformationAudioMetadata(file)
        else hideLoadingDialog()
    }

    private fun manageInformationAudio(event: Event<Result<DomainInformationAudioMetadata>>) {
        event.getContentIfNotHandled()?.run {
            with(this) {
                doIfSuccess { domainInformation ->
                    domainAudioInformation = domainInformation
                    setAudioMetadata()
                }
                doIfError {
                    showMetadataNotAvailable()
                    binding.constraintLayoutDetail.showErrorSnackBar(
                        getString(R.string.audio_detail_metadata_error),
                        AUDIO_DETAIL_ERROR_ANIMATION_DURATION
                    ) {
                        this@AudioDetailActivity.verifySessionBeforeAction {
                            showLoadingDialog()
                            audioDetailViewModel.getInformationAudioMetadata(file)
                        }
                    }
                }
            }
            hideLoadingDialog()
        }
    }

    private fun showMetadataNotAvailable() {
        binding.officerValue.text = getString(R.string.not_available)
        binding.videosAssociatedValue.text = getString(R.string.not_available)
    }

    private fun manageSavePartnerId(result: Event<Result<Unit>>) {
        hideLoadingDialog()
        with(result.getContentIfNotHandled()) {
            this?.doIfSuccess {
                binding.constraintLayoutDetail.showSuccessSnackBar(
                    getString(R.string.file_list_associate_partner_id_success)
                )
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                setCurrentOfficerAssociatedInView()
            }
            this?.doIfError {
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
        binding.bottomSheetPartnerId.buttonCloseAssignToOfficer.setOnClickListenerCheckConnection {
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
        audioDetailViewModel.savePartnerId(file, partnerId)
    }

    private fun setInformationOfAudio() {
        binding.audioNameValue.text = file.name
        binding.dateTimeValue.text = file.date
    }

    private fun setAudioMetadata() {
        setOfficerAssociatedInView()
        setVideosAssociatedInView()
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
