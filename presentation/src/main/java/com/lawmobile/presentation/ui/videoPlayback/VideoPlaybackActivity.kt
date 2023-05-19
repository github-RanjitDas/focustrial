package com.lawmobile.presentation.ui.videoPlayback

import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.Display
import android.view.SurfaceView
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.domain.extensions.getDurationMinutesLong
import com.lawmobile.domain.extensions.getDurationMinutesString
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityVideoPlaybackBinding
import com.lawmobile.presentation.entities.MediaPlayerControls
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createAlertDialogUnsavedChanges
import com.lawmobile.presentation.extensions.detachFragment
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
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetFilterTag
import kotlinx.coroutines.Dispatchers

class VideoPlaybackActivity : BaseActivity() {

    private var exoPlayerListener: Player.Listener? = null
    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var binding: ActivityVideoPlaybackBinding

    private val viewModel: VideoPlaybackViewModel by viewModels()
    private val metadataManager get() = viewModel.informationManager

    private var associateSnapshotsFragment = AssociateFilesFragment()
    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetAssociate.bottomSheetAssociate)
    }

    private var exoPlayer: ExoPlayer? = null

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
            viewModel.setState(value)
        }

    private lateinit var mediaPlayerControls: MediaPlayerControls
    private lateinit var videoSurface: SurfaceView

    private var isPortrait: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val videoFile = getCameraConnectFileFromIntent()
        if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) {
            binding.layoutMetadataForm.eventTitle.text = getString(R.string.category)
        } else {
            binding.layoutMetadataForm.eventTitle.text = getString(R.string.event)
        }
        videoFile?.let {
            setVideoNameAndDate(it)
            metadataManager.setup(binding.layoutMetadataForm, it)
            toggleAssociateDialog(isAssociateDialogOpen)
            setFeatures()
            setObservers()
            setCollectors()
            showLoadingDialog()
            viewModel.getVideoPlaybackInfo(it)
        }
        initRestoreMetaData(savedInstanceState)
    }

    private fun initRestoreMetaData(savedInstanceState: Bundle?) {
        viewModel.informationManager.setRestoreVideoMetaDataCallback(object :
                RestoreVideoMetaDataFromCache {
                override fun onRestoreVideoMetaData() {
                    if (savedInstanceState != null) {
                        exoPlayer?.seekTo(CameraInfo.playbackPosition ?: 0)
                        if (isInPortraitMode()) {
                            viewModel.informationManager.restoreVideoDetailMetaDataFromCache()
                        }
                    }
                }
            })
    }

    private fun setVideoNameAndDate(videoFile: DomainCameraFile) {
        binding.layoutMetadataForm.videoNameValue.text = videoFile.name
        binding.layoutMetadataForm.startTimeValue.text = videoFile.getDateDependingOnNameLength()
    }

    private fun setFeatures() = with(binding.layoutMetadataForm) {
        associateAudioTitle.isVisible = FeatureSupportHelper.supportAudioAssociation
        layoutAssociatedAudios.isVisible = FeatureSupportHelper.supportAudioAssociation
        buttonAssociateAudios.isVisible = FeatureSupportHelper.supportAudioAssociation
        partnerIdTitle.isVisible = FeatureSupportHelper.supportAssociateOfficerID
        partnerIdValue.isVisible = FeatureSupportHelper.supportAssociateOfficerID
    }

    private fun toggleAssociateDialog(isOpen: Boolean) {
        binding.shadowPlaybackView.isVisible = isOpen
        bottomSheetBehavior.state = if (isOpen) {
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

    private fun setAppBar() = with(binding.layoutAppBar) {
        textViewTitle.text = getString(R.string.video_detail)
        buttonSimpleList.isVisible = false
        buttonThumbnailList.isVisible = false
    }

    private fun setCollectors() {
        collectVideoPlaybackState()
        collectMediaInformation()
        collectVideoInformation()
        collectInformationExceptions()
        collectUpdateMetadataResult()
    }

    private fun collectVideoPlaybackState() {
        activityCollect(viewModel.state) {
            with(it) {
                onDefault {
                    if (!isPortrait) {
                        setPortraitOrientation()
                    }
                    binding.layoutMetadataForm.layoutMetadataForm.visibility = View.VISIBLE
                    binding.buttonSaveMetadata.visibility = View.VISIBLE
                    binding.layoutAppBar.layoutCustomAppBar.visibility = View.VISIBLE
                    val sizeInPixel: Int =
                        resources.getDimensionPixelSize(R.dimen.size_260dp)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        sizeInPixel
                    )
                    binding.scrollView.setOnTouchListener { _, _ -> false }
                    binding.layoutPlayer.layoutParams = params
                    setViews()
                    setListeners()
                }
                onFullScreen {
                    binding.layoutMetadataForm.layoutMetadataForm.visibility = View.GONE
                    binding.buttonSaveMetadata.visibility = View.GONE
                    binding.shadowPlaybackView.visibility = View.GONE
                    binding.layoutAppBar.layoutCustomAppBar.visibility = View.GONE
                    binding.scrollView.setOnTouchListener { _, _ -> true }
                    val display: Display = windowManager.defaultDisplay
                    val size = Point()
                    display.getSize(size)
                    val width = size.x
                    val sizeInPixel: Int =
                        resources.getDimensionPixelSize(R.dimen.margin_21dp)

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        width - sizeInPixel
                    )
                    binding.layoutPlayer.layoutParams = params
//                    val paramsSV = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT
//                    )
//                    paramsSV.bottomMargin = 0
//                    binding.scrollView.layoutParams = paramsSV
                    onPlayingListener()
                    buttonNormalScreenListener()
                }
            }
        }
    }

    private fun collectMediaInformation() {
        activityCollect(viewModel.mediaInformation) { result ->
            result?.let {
                createVideoPlayerExo(it)
                setMediaInformation(it)
            }
        }
    }

    private fun collectVideoInformation() {
        activityCollect(viewModel.videoInformation) { result ->
            result?.let(::setVideoInformation)
        }
    }

    private fun collectInformationExceptions() {
        activityCollect(viewModel.videoInformationException) { exception ->
            hideLoadingDialog()
            exception.let {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_CAMERA_ERRORS, it,
                    getString(R.string.error_get_information_metadata)
                )
                if (isPortrait) {
                    showToast(
                        getString(R.string.error_get_information_metadata), Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }

    private fun collectUpdateMetadataResult() {
        activityCollect(viewModel.updateMetadataResult) { result ->
            when (result) {
                is Result.Success -> {
                    CameraInfo.areNewChanges = true
                    showToast(
                        getString(R.string.video_metadata_saved_success), Toast.LENGTH_SHORT
                    )
                    runWithDelay(
                        dispatcher = Dispatchers.Main.immediate, callback = ::finish
                    )
                }

                is Result.Error -> showToast(
                    getString(R.string.video_metadata_save_error), Toast.LENGTH_SHORT
                )
            }
            hideLoadingDialog()
        }
    }

    private fun setObservers() {
        isNetworkAlertShowing.observe(this) { isShowing ->
            if (isShowing) exoPlayer?.pause()
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
//        viewModel.mediaPlayer.isPlayingCallback = {
//            if (viewModel.mediaPlayer.isEndReached) updateLastInteraction()
//            updateLiveOrPlaybackActive(it)
//        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isPortrait = !isPortrait
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
        with(binding.buttonFullScreenExo) {
            isActivated = false
            setOnClickListenerCheckConnection {
                CameraInfo.playbackPosition = exoPlayer?.currentPosition
                state = if (isPortrait) {
                    VideoPlaybackState.FullScreen
                } else {
                    VideoPlaybackState.Default
                }
            }
            VideoPlaybackViewModel.isVidePlayerInFullScreen = true
        }

    private fun buttonNormalScreenListener() =
        with(binding.layoutFullScreenPlayback.buttonFullScreenExo) {
            isActivated = true
            setOnClickListenerCheckConnection {
                CameraInfo.playbackPosition = exoPlayer?.currentPosition
                state = VideoPlaybackState.Default
            }
            VideoPlaybackViewModel.isVidePlayerInFullScreen = false
        }

    private fun stopVideoWhenScrolling() = with(binding) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollBounds = Rect()
            scrollView.getHitRect(scrollBounds)
            if (videoIsNotVisible(scrollBounds)) exoPlayer?.pause()
        }
    }

    private fun ActivityVideoPlaybackBinding.videoIsNotVisible(scrollBounds: Rect) =
        (!fakeSurfaceVideoPlayback.getLocalVisibleRect(scrollBounds) && exoPlayer?.isPlaying!!)

    private fun setVideoInformation(videoInformation: DomainVideoMetadata) {
        videoInformation.associatedFiles?.let {
            associateSnapshotsFragment.setFilesAssociatedFromMetadata(it as MutableList)
        }
        hideLoadingDialog()
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
        exoPlayer?.pause()
        supportFragmentManager.attachFragment(
            R.id.fragmentAssociateHolder, associateSnapshotsFragment, AssociateFilesFragment.TAG
        )
        FilesAssociatedByUser.setTemporalValue(FilesAssociatedByUser.value)
    }

    private fun saveVideoInformation() = with(binding) {
        exoPlayer?.pause()
        hideKeyboard()

        if (!CameraInfo.metadataEvents.isNullOrEmpty() && !metadataManager.isEventSelected()) {
            if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) {
                layoutVideoPlayback.showErrorSnackBar(getString(R.string.category_mandatory))
            } else {
                layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
            }

            return
        }

        showLoadingDialog()
        viewModel.saveVideoMetadata()
    }

    private fun getCameraConnectFileFromIntent() =
        intent?.getSerializableExtra(DOMAIN_CAMERA_FILE) as DomainCameraFile?

    private fun setMediaInformation(mediaInformation: DomainInformationVideo) {
        val durationText = mediaInformation.getDurationMinutesString()
        binding.layoutMetadataForm.durationValue.text = durationText
    }

    private fun createVideoPlayer(mediaInformation: DomainInformationVideo) {
        viewModel.mediaPlayer.apply {
            setControls(
                mediaPlayerControls, mediaInformation.getDurationMinutesLong(), lifecycle
            )
            create(mediaInformation.urlVideo, videoSurface)
            if (!isEndReached && !isPaused) play()
        }
    }

    private fun createVideoPlayerExo(mediaInformation: DomainInformationVideo) {
        releasePlayer()
        exoPlayer = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                if (binding.layoutFullScreenPlayback.layoutVideoPlayback.isVisible) {
                    binding.layoutFullScreenPlayback.videoView.setShowNextButton(false)
                    binding.layoutFullScreenPlayback.videoView.setShowPreviousButton(false)
                    binding.layoutFullScreenPlayback.videoView.setShowVrButton(false)
                    binding.layoutFullScreenPlayback.videoView.setShowShuffleButton(false)
                    binding.layoutFullScreenPlayback.videoView.player = exoPlayer
                } else {
                    binding.videoView.setShowNextButton(false)
                    binding.videoView.setShowPreviousButton(false)
                    binding.videoView.setShowVrButton(false)
                    binding.videoView.setShowShuffleButton(false)
                    binding.videoView.player = exoPlayer
                }
                val mediaSource: MediaSource = RtspMediaSource.Factory().setForceUseRtpTcp(true)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(mediaInformation.urlVideo)))
                exoPlayer.setMediaSource(mediaSource)
            }
        exoPlayerListener = object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {

                when (playbackState) {
                    ExoPlayer.STATE_ENDED -> {
                        val mediaSource: MediaSource =
                            RtspMediaSource.Factory().setForceUseRtpTcp(true)
                                .createMediaSource(MediaItem.fromUri(Uri.parse(mediaInformation.urlVideo)))
                        exoPlayer?.setMediaSource(mediaSource)
                        exoPlayer?.pause()
                    }

                    else -> {}
                }
            }
        }
        exoPlayer?.addListener(exoPlayerListener!!)
        // exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }

    private fun releasePlayer() {
        exoPlayer?.stop()
        exoPlayer?.removeMediaItem(0)
        exoPlayerListener.let { exoPlayer?.removeListener(it!!) }
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onBackPressed() {
        if (state is VideoPlaybackState.Default) {
            if (!isAssociateDialogOpen) {
                if (viewModel.theMetadataWasEdited()) createAlertDialogUnsavedChanges()
                else {
                    CameraInfo.playbackPosition = 0
                    super.onBackPressed()
                }
            } else isAssociateDialogOpen = false
        } else {
            CameraInfo.playbackPosition = exoPlayer?.currentPosition
            state = VideoPlaybackState.Default
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        FilesAssociatedByUser.cleanList()
        if (!VideoPlaybackViewModel.isVidePlayerInFullScreen) {
            viewModel.informationManager.saveVideoDetailMetaData(CameraInfo.playbackPosition!!.toInt())
        }
    }
}
