package com.lawmobile.presentation.ui.videoPlayback

import android.graphics.Rect
import android.os.Bundle
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityVideoPlaybackBinding
import com.lawmobile.presentation.entities.MediaPlayerControls
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createAlertDialogUnsavedChanges
import com.lawmobile.presentation.extensions.detachFragment
import com.lawmobile.presentation.extensions.milliSecondsToString
import com.lawmobile.presentation.extensions.runWithDelay
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setPortraitOrientation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.extensions.toggleDeXFullScreen
import com.lawmobile.presentation.ui.associateFiles.AssociateFilesFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.videoPlayback.state.VideoPlaybackState
import com.lawmobile.presentation.utils.Constants.DOMAIN_CAMERA_FILE
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetFilterTag
import kotlinx.coroutines.Dispatchers

class VideoPlaybackActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoPlaybackBinding

    private val viewModel: VideoPlaybackViewModel by viewModels()

    private val metadataManager get() = viewModel.informationManager
    private val editedVideoInformation: DomainVideoMetadata
        get() = metadataManager.getEditedInformation(currentVideoInformation)

    private var currentAttempts = 0
    private var isVideoMetadataChangesSaved = false
    private lateinit var currentVideoInformation: DomainVideoMetadata

    private var associateSnapshotsFragment = AssociateFilesFragment()
    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetAssociate.bottomSheetAssociate)
    }

    private var isAssociateDialogOpen: Boolean
        get() = viewModel.isAssociateDialogOpen
        set(value) {
            viewModel.isAssociateDialogOpen = value
            toggleAssociateDialog(value)
        }

    private var state: VideoPlaybackState
        get() = viewModel.getState()
        set(value) {
            toggleDeXFullScreen()
            viewModel.mediaPlayer.stop()
            viewModel.setState(value)
        }

    private lateinit var mediaPlayerControls: MediaPlayerControls
    private lateinit var videoSurface: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        metadataManager.setup(binding.layoutMetadataForm, getCameraConnectFileFromIntent())
        toggleAssociateDialog(isAssociateDialogOpen)
        setObservers()
        setCollectors()
        verifyIfSelectedVideoWasChanged()
        showLoadingDialog()

        if (videoMediaInformation != null) setVideoInformation()
        else getMediaInformation()

        getVideoInformation()
    }

    private fun toggleAssociateDialog(isOpen: Boolean) {
        binding.shadowPlaybackView.isVisible = isOpen
        bottomSheetBehavior.state =
            if (isOpen) {
                setAssociateFilesFragment()
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                hideKeyboard()
                detachAssociationFragment()
                FilesAssociatedByUser.temporal.addAll(FilesAssociatedByUser.value)
                BottomSheetBehavior.STATE_HIDDEN
            }
    }

    private fun detachAssociationFragment() {
        supportFragmentManager.detachFragment(binding.bottomSheetAssociate.fragmentAssociateHolder.id)
    }

    private fun setViews() {
        setAppBar()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        verifyEventEmpty()
    }

    private fun setAppBar() = with(binding.layoutAppBar) {
        textViewTitle.text = getString(R.string.video_detail)
        buttonSimpleList.isVisible = false
        buttonThumbnailList.isVisible = false
    }

    private fun verifyEventEmpty() {
        if (CameraInfo.metadataEvents.isEmpty()) showErrorInEvents()
    }

    private fun showErrorInEvents() {
        binding.layoutVideoPlayback.showErrorSnackBar(
            getString(R.string.catalog_error_video_playback),
            Snackbar.LENGTH_LONG
        )
    }

    private fun setCollectors() {
        collectVideoPlaybackState()
        collectMediaInformation()
        collectVideoInformation()
        collectUpdateMetadataResult()
    }

    private fun collectVideoPlaybackState() {
        activityCollect(viewModel.state) {
            with(it) {
                onDefault {
                    if (!isInPortraitMode()) setPortraitOrientation()
                    setFullscreenVisibility(false)
                    setViews()
                    setListeners()
                    setDefaultViews()
                }
                onFullScreen {
                    setFullscreenVisibility(true)
                    onPlayingListener()
                    buttonNormalScreenListener()
                    setFullScreenViews()
                }
            }
            videoMediaInformation?.run { createVideoPlayer(this) }
        }
    }

    private fun setFullScreenViews() {
        mediaPlayerControls = getFullscreenPlayerControls()
        videoSurface = binding.layoutFullScreenPlayback.surfaceVideoPlayback
    }

    private fun setDefaultViews() {
        mediaPlayerControls = getNormalPlayerControls()
        videoSurface = binding.layoutNormalPlayback.surfaceVideoPlayback
    }

    private fun setFullscreenVisibility(isVisible: Boolean) = with(binding) {
        layoutFullScreenPlayback.layoutVideoPlayback.isVisible = isVisible
        scrollView.isVisible = !isVisible
        buttonSaveMetadata.isVisible = !isVisible
    }

    private fun collectVideoInformation() {
        activityCollect(viewModel.videoInformation) { result ->
            result?.run {
                doIfSuccess(::setVideoMetadata)
                doIfError {
                    this@VideoPlaybackActivity.showToast(
                        getString(R.string.get_video_metadata_error),
                        Toast.LENGTH_SHORT
                    )
                    finish()
                }
            }
        }
    }

    private fun collectUpdateMetadataResult() {
        activityCollect(viewModel.updateMetadataResult) { result ->
            when (result) {
                is Result.Success -> {
                    CameraInfo.areNewChanges = true
                    this.showToast(
                        getString(R.string.video_metadata_saved_success),
                        Toast.LENGTH_SHORT
                    )
                    runWithDelay(dispatcher = Dispatchers.Main.immediate) { onBackPressed() }
                }
                is Result.Error -> showToast(
                    getString(R.string.video_metadata_save_error),
                    Toast.LENGTH_SHORT
                )
            }
            hideLoadingDialog()
        }
    }

    private fun collectMediaInformation() {
        activityCollect(viewModel.mediaInformation) { result ->
            result?.run {
                doIfSuccess {
                    videoMediaInformation = it
                    createVideoPlayer(it)
                    setVideoInformation()
                }
                doIfError {
                    if (isAllowedToAttemptToGetInformation()) {
                        currentAttempts += 1
                        currentVideo?.let(viewModel::getMediaInformation)
                    } else {
                        showToast(
                            getString(R.string.error_get_information_metadata),
                            Toast.LENGTH_SHORT
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun setObservers() {
        isNetworkAlertShowing.observe(this) { isShowing ->
            if (isShowing) viewModel.mediaPlayer.pause()
        }
    }

    private fun setListeners() {
        bottomSheetListeners()
        buttonFullScreenListener()
        saveButtonListener()
        backButtonListener()
        buttonAssociateSnapshotsListener()
        onAssociateSnapshots()
        onPlayingListener()
        stopVideoWhenScrolling()
    }

    private fun bottomSheetListeners() {
        bottomSheetBehavior.isDraggable = false
        binding.bottomSheetAssociate.buttonCloseAssociateFiles.setOnClickListener {
            isAssociateDialogOpen = false
        }
    }

    private fun onPlayingListener() {
        viewModel.mediaPlayer.isPlayingCallback = {
            if (viewModel.mediaPlayer.isEndReached) updateLastInteraction()
            updateLiveOrPlaybackActive(it)
        }
    }

    private fun onAssociateSnapshots() {
        associateSnapshotsFragment.onAssociateFiles = {
            isAssociateDialogOpen = false
            associateSnapshotsFragment.setFilesAssociatedFromMetadata(FilesAssociatedByUser.temporal)
            FilesAssociatedByUser.setFinalValue(FilesAssociatedByUser.temporal)
            showSnapshotsAssociated()
            binding.layoutVideoPlayback.showSuccessSnackBar(getString(R.string.snapshots_added_success))
        }
    }

    private fun buttonAssociateSnapshotsListener() {
        binding.layoutMetadataForm.buttonAssociateSnapshots.setOnClickListenerCheckConnection {
            isAssociateDialogOpen = true
        }
    }

    private fun backButtonListener() {
        binding.layoutAppBar.imageButtonBackArrow.setOnClickListenerCheckConnection {
            onBackPressed()
        }
    }

    private fun saveButtonListener() {
        binding.buttonSaveMetadata.setOnClickListenerCheckConnection {
            saveVideoInformation()
        }
    }

    private fun buttonFullScreenListener() =
        with(binding.layoutNormalPlayback.buttonFullScreen) {
            isActivated = false
            setOnClickListenerCheckConnection {
                state = VideoPlaybackState.FullScreen
            }
        }

    private fun buttonNormalScreenListener() =
        with(binding.layoutFullScreenPlayback.buttonFullScreen) {
            isActivated = true
            setOnClickListenerCheckConnection {
                state = VideoPlaybackState.Default
            }
        }

    private fun stopVideoWhenScrolling() = with(binding) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollBounds = Rect()
            scrollView.getHitRect(scrollBounds)
            if (videoIsNotVisible(scrollBounds)) viewModel.mediaPlayer.pause()
        }
    }

    private fun ActivityVideoPlaybackBinding.videoIsNotVisible(scrollBounds: Rect) =
        (!fakeSurfaceVideoPlayback.getLocalVisibleRect(scrollBounds) && viewModel.mediaPlayer.isPlaying)

    private fun setVideoMetadata(videoInformation: DomainVideoMetadata) =
        with(binding.layoutMetadataForm) {
            if (!this@VideoPlaybackActivity::currentVideoInformation.isInitialized) {
                currentVideoInformation = videoInformation
                metadataManager.setInformation(videoInformation)
                videoInformation.associatedFiles?.let {
                    associateSnapshotsFragment.setFilesAssociatedFromMetadata(it as MutableList)
                }
            }

            hideLoadingDialog()
        }

    private fun verifyIfSelectedVideoWasChanged() {
        val videoWasChanged = getCameraConnectFileFromIntent() != currentVideo
        if (videoWasChanged) restartObjectOfCompanion()
    }

    private fun showSnapshotsAssociated() = with(binding.layoutMetadataForm) {
        layoutAssociatedSnapshots.removeAllViews()
        layoutAssociatedSnapshots.isVisible = !FilesAssociatedByUser.value.isNullOrEmpty()
        FilesAssociatedByUser.value.forEach {
            createTagInPosition(layoutAssociatedSnapshots.childCount, it.date)
        }
    }

    private fun createTagInPosition(position: Int, text: String) {
        binding.layoutMetadataForm.layoutAssociatedSnapshots.addView(
            SafeFleetFilterTag(this, null, 0).apply {
                tagText = text
                onClicked = {
                    removeAssociatedSnapshot(text)
                    showSnapshotsAssociated()
                }
            },
            position
        )
    }

    private fun removeAssociatedSnapshot(date: String) {
        val index = FilesAssociatedByUser.value.indexOfFirst {
            it.date == date
        }
        if (index >= 0) {
            FilesAssociatedByUser.value.removeAt(index)
            FilesAssociatedByUser.setTemporalValue(FilesAssociatedByUser.value)
            associateSnapshotsFragment.setFilesAssociatedFromMetadata(FilesAssociatedByUser.value)
        }
    }

    private fun setAssociateFilesFragment() {
        viewModel.mediaPlayer.pause()
        supportFragmentManager.attachFragment(
            R.id.fragmentAssociateHolder,
            associateSnapshotsFragment,
            AssociateFilesFragment.TAG
        )
        FilesAssociatedByUser.setTemporalValue(FilesAssociatedByUser.value)
    }

    private fun saveVideoInformation() = with(binding) {
        viewModel.mediaPlayer.pause()
        hideKeyboard()

        if (!metadataManager.isEventSelected()) {
            layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
            return
        }
        CameraInfo.areNewChanges = true
        showLoadingDialog()
        viewModel.saveVideoInformation(editedVideoInformation)
        isVideoMetadataChangesSaved = true
    }

    private fun getMediaInformation() {
        currentVideo = getCameraConnectFileFromIntent()
        currentVideo?.run {
            viewModel.getMediaInformation(this)
        }
    }

    private fun getVideoInformation() {
        currentVideo?.run {
            viewModel.getVideoInformation(name, nameFolder)
        }
    }

    private fun getCameraConnectFileFromIntent() =
        intent?.getSerializableExtra(DOMAIN_CAMERA_FILE) as DomainCameraFile

    private fun isAllowedToAttemptToGetInformation() = currentAttempts <= ATTEMPTS_ALLOWED

    private fun setVideoInformation() = with(binding.layoutMetadataForm) {
        videoNameValue.text = currentVideo?.name
        startTimeValue.text = currentVideo?.getDateDependingOnNameLength()
        val durationText = videoMediaInformation?.duration?.toLong()?.times(1000)
            ?.milliSecondsToString()
        durationValue.text = durationText
    }

    private fun createVideoPlayer(domainInformationVideo: DomainInformationVideo) {
        viewModel.mediaPlayer.apply {
            setControls(
                mediaPlayerControls,
                getVideoDurationMillis(domainInformationVideo),
                lifecycle
            )
            create(domainInformationVideo.urlVideo, videoSurface)
            if (!isEndReached && !isPaused) play()
        }
    }

    private fun getNormalPlayerControls() = binding.layoutNormalPlayback.run {
        MediaPlayerControls(
            buttonPlay,
            textViewPlayerTime,
            textViewPlayerDuration,
            seekProgressVideo,
            buttonAspect
        )
    }

    private fun getFullscreenPlayerControls() = binding.layoutFullScreenPlayback.run {
        MediaPlayerControls(
            buttonPlay,
            textViewPlayerTime,
            textViewPlayerDuration,
            seekProgressVideo,
            buttonAspect
        )
    }

    private fun getVideoDurationMillis(domainInformationVideo: DomainInformationVideo) =
        domainInformationVideo.duration.toLong().times(1000)

    private fun theMetadataWasEdited(): Boolean {
        if (!isVideoMetadataChangesSaved) {
            val newMetadata = editedVideoInformation.metadata
            val oldMetadata = currentVideoInformation.metadata?.apply { convertNullParamsToEmpty() }
                ?: return newMetadata?.hasAnyInformation() == true

            return currentVideoInformation.associatedFiles ?: emptyList() != FilesAssociatedByUser.value ||
                newMetadata?.isDifferentFrom(oldMetadata) ?: false
        }
        return false
    }

    private fun restartObjectOfCompanion() {
        videoMediaInformation = null
    }

    override fun onBackPressed() {
        if (state is VideoPlaybackState.Default) {
            if (!isAssociateDialogOpen) {
                viewModel.mediaPlayer.pause()
                if (theMetadataWasEdited()) createAlertDialogUnsavedChanges()
                else super.onBackPressed()
            } else isAssociateDialogOpen = false
        } else state = VideoPlaybackState.Default
    }

    override fun onDestroy() {
        super.onDestroy()
        FilesAssociatedByUser.cleanList()
    }

    companion object {
        private var currentVideo: DomainCameraFile? = null
        private var videoMediaInformation: DomainInformationVideo? = null
        const val ATTEMPTS_ALLOWED = 2
    }
}
