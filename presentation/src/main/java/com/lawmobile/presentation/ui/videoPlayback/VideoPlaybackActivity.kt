package com.lawmobile.presentation.ui.videoPlayback

import android.graphics.Rect
import android.os.Bundle
import android.text.InputFilter
import android.view.SurfaceView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityVideoPlaybackBinding
import com.lawmobile.presentation.entities.FilesAssociatedByUser
import com.lawmobile.presentation.entities.MediaPlayerControls
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createAlertDialogUnsavedChanges
import com.lawmobile.presentation.extensions.detachFragment
import com.lawmobile.presentation.extensions.milliSecondsToString
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setPortraitOrientation
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.extensions.toggleDeXFullScreen
import com.lawmobile.presentation.ui.associateSnapshots.AssociateSnapshotsFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.videoPlayback.state.VideoPlaybackState
import com.lawmobile.presentation.utils.Constants.DOMAIN_CAMERA_FILE
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetFilterTag

class VideoPlaybackActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoPlaybackBinding

    private val viewModel: VideoPlaybackViewModel by viewModels()

    private val eventList = mutableListOf<String>()
    private val raceList = mutableListOf<String>()
    private val genderList = mutableListOf<String>()

    private var currentAttempts = 0
    private var isVideoMetadataChangesSaved = false
    private lateinit var currentMetadata: DomainVideoMetadata

    private var associateSnapshotsFragment = AssociateSnapshotsFragment()
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

        toggleAssociateDialog(isAssociateDialogOpen)
        setObservers()
        setCollectors()
        verifyIfSelectedVideoWasChanged()
        showLoadingDialog()

        if (domainInformationVideo != null) setVideoInformation()
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
        setCatalogLists()
        addEditTextFilter()
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

    private fun addEditTextFilter() = with(binding.layoutMetadataForm) {
        ticket1Value.filters = getFiltersWithLength(20)
        ticket2Value.filters = getFiltersWithLength(20)
        case1Value.filters = getFiltersWithLength(50)
        case2Value.filters = getFiltersWithLength(50)
        dispatch1Value.filters = getFiltersWithLength(30)
        dispatch2Value.filters = getFiltersWithLength(30)
        locationValue.filters = getFiltersWithLength(30)
        notesValue.filters = getFiltersWithLength(100)
        firstNameValue.filters = getFiltersWithLength(30)
        lastNameValue.filters = getFiltersWithLength(30)
        driverLicenseValue.filters = getFiltersWithLength(30)
        licensePlateValue.filters = getFiltersWithLength(30)
    }

    private fun getFiltersWithLength(length: Int): Array<InputFilter> {
        val lengthFilter = InputFilter.LengthFilter(length)
        val charactersFilter = InputFilter { source, _, _, _, _, _ ->
            if (source != null && containsNotAllowedCharacters(source)) ""
            else null
        }

        return arrayOf(lengthFilter, charactersFilter)
    }

    private fun containsNotAllowedCharacters(source: CharSequence): Boolean {
        val comma = ","
        val ampersand = "&"
        val quotes = "\""
        return comma.contains("" + source) ||
            ampersand.contains("" + source) ||
            quotes.contains("" + source)
    }

    private fun verifyEventEmpty() {
        if (CameraInfo.metadataEvents.isEmpty()) showErrorInEvents()
    }

    private fun setCatalogLists() {
        eventList.add(getString(R.string.select))
        eventList.addAll(CameraInfo.metadataEvents.map { it.name })
        raceList.addAll(resources.getStringArray(R.array.race_spinner))
        genderList.addAll(resources.getStringArray(R.array.gender_spinner))

        with(binding.layoutMetadataForm) {
            eventValue.adapter =
                ArrayAdapter(this@VideoPlaybackActivity, R.layout.spinner_item, eventList)
            raceValue.adapter =
                ArrayAdapter(this@VideoPlaybackActivity, R.layout.spinner_item, raceList)
            genderValue.adapter =
                ArrayAdapter(this@VideoPlaybackActivity, R.layout.spinner_item, genderList)
        }
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
            domainInformationVideo?.run { createVideoPlayer(this) }
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
                doIfSuccess {
                    currentMetadata = it
                    setVideoMetadata(it)
                    CameraInfo.areNewChanges = true
                }
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
                    this.showToast(
                        getString(R.string.video_metadata_saved_success),
                        Toast.LENGTH_SHORT
                    )
                    onBackPressed()
                }
                is Result.Error -> this.showToast(
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
                    domainInformationVideo = it
                    createVideoPlayer(it)
                    setVideoInformation()
                }
                doIfError {
                    if (isAllowedToAttemptToGetInformation()) {
                        currentAttempts += 1
                        currentVideo?.let(viewModel::getMediaInformation)
                    } else {
                        baseContext.showToast(
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
        binding.bottomSheetAssociate.buttonCloseAssociateSnapshots.setOnClickListener {
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
        associateSnapshotsFragment.onAssociateSnapshots = {
            isAssociateDialogOpen = false
            associateSnapshotsFragment.setSnapshotsAssociatedFromMetadata(FilesAssociatedByUser.temporal)
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
            updateVideoInformationInCamera()
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

    private fun setVideoMetadata(videoMetadata: DomainVideoMetadata) =
        with(binding.layoutMetadataForm) {
            videoMetadata.metadata?.let {
                eventValue.setSelection(getSpinnerSelection(eventList, it.event?.name))
                partnerIdValue.setText(it.partnerID)
                ticket1Value.setText(it.ticketNumber)
                ticket2Value.setText(it.ticketNumber2)
                case1Value.setText(it.caseNumber)
                case2Value.setText(it.caseNumber2)
                dispatch1Value.setText(it.dispatchNumber)
                dispatch2Value.setText(it.dispatchNumber2)
                locationValue.setText(it.location)
                notesValue.setText(it.remarks)
                firstNameValue.setText(it.firstName)
                lastNameValue.setText(it.lastName)
                genderValue.setSelection(getSpinnerSelection(genderList, it.gender))
                raceValue.setSelection(getSpinnerSelection(raceList, it.race))
                driverLicenseValue.setText(it.driverLicense)
                licensePlateValue.setText(it.licensePlate)
            }

            videoMetadata.associatedFiles?.let {
                FilesAssociatedByUser.setTemporalValue(it as MutableList)
                FilesAssociatedByUser.setFinalValue(it)
                associateSnapshotsFragment.setSnapshotsAssociatedFromMetadata(it)
            }

            hideLoadingDialog()
        }

    private fun getSpinnerSelection(list: List<String>, value: String?): Int {
        return if (value == null || value.isEmpty()) 0
        else list.indexOfFirst { it == value }
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
            associateSnapshotsFragment.setSnapshotsAssociatedFromMetadata(FilesAssociatedByUser.value)
        }
    }

    private fun setAssociateFilesFragment() {
        viewModel.mediaPlayer.pause()
        supportFragmentManager.attachFragment(
            R.id.fragmentAssociateHolder,
            associateSnapshotsFragment,
            AssociateSnapshotsFragment.TAG
        )
        FilesAssociatedByUser.setTemporalValue(FilesAssociatedByUser.value)
    }

    private fun updateVideoInformationInCamera() = with(binding) {
        viewModel.mediaPlayer.pause()
        hideKeyboard()
        if (layoutMetadataForm.eventValue.selectedItem == eventList[0]) {
            layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
            return
        }
        CameraInfo.areNewChanges = true
        showLoadingDialog()
        viewModel.saveVideoInformation(getNewMetadataFromForm())
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
        val durationText = domainInformationVideo?.duration?.toLong()?.times(1000)
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
            val newMetadata = getNewMetadataFromForm().metadata
            val oldMetadata = currentMetadata.metadata?.apply { convertNullParamsToEmpty() }
                ?: return newMetadata?.hasAnyInformation() ?: false

            return currentMetadata.associatedFiles ?: emptyList() != FilesAssociatedByUser.value ||
                newMetadata?.isDifferentFrom(oldMetadata) ?: false
        }
        return false
    }

    private fun getNewMetadataFromForm(): DomainVideoMetadata = binding.layoutMetadataForm.run {
        var gender = ""
        var race = ""
        val event = if (eventValue.selectedItemPosition != 0)
            CameraInfo.metadataEvents[eventValue.selectedItemPosition - 1]
        else MetadataEvent("", "", "")

        if (genderValue.selectedItem != genderList[0]) {
            gender = genderValue.selectedItem.toString()
        }

        if (raceValue.selectedItem != raceList[0]) {
            race = raceValue.selectedItem.toString()
        }

        return DomainVideoMetadata(
            fileName = videoNameValue.text.toString(),
            metadata = DomainMetadata(
                event = event,
                partnerID = partnerIdValue.text.toString(),
                ticketNumber = ticket1Value.text.toString(),
                ticketNumber2 = ticket2Value.text.toString(),
                caseNumber = case1Value.text.toString(),
                caseNumber2 = case2Value.text.toString(),
                dispatchNumber = dispatch1Value.text.toString(),
                dispatchNumber2 = dispatch2Value.text.toString(),
                location = locationValue.text.toString(),
                remarks = notesValue.text.toString(),
                firstName = firstNameValue.text.toString(),
                lastName = lastNameValue.text.toString(),
                gender = gender,
                race = race,
                driverLicense = driverLicenseValue.text.toString(),
                licensePlate = licensePlateValue.text.toString()
            ),
            nameFolder = currentVideo?.nameFolder,
            officerId = CameraInfo.officerId,
            path = currentMetadata.path ?: currentVideo?.path,
            associatedFiles = FilesAssociatedByUser.value,
            annotations = currentMetadata.annotations,
            serialNumber = CameraInfo.serialNumber,
            endTime = currentMetadata.endTime,
            gmtOffset = currentMetadata.gmtOffset,
            hash = currentMetadata.hash,
            preEvent = currentMetadata.preEvent,
            startTime = currentMetadata.startTime,
            videoSpecs = currentMetadata.videoSpecs,
            trigger = currentMetadata.trigger,
            x2sn = currentMetadata.x2sn,
            x1sn = currentMetadata.x1sn
        )
    }

    private fun restartObjectOfCompanion() {
        domainInformationVideo = null
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
        private var domainInformationVideo: DomainInformationVideo? = null
        const val ATTEMPTS_ALLOWED = 2
    }
}
