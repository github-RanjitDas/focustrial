package com.lawmobile.presentation.ui.videoPlayback

import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ArrayAdapter
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
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.enums.MediaType
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityVideoPlaybackBinding
import com.lawmobile.presentation.entities.FilesAssociatedByUser
import com.lawmobile.presentation.entities.MediaPlayerControls
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.createAlertDialogUnsavedChanges
import com.lawmobile.presentation.extensions.detachFragment
import com.lawmobile.presentation.extensions.milliSecondsToString
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.associateFiles.AssociateFilesFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.AUDIO_LIST
import com.lawmobile.presentation.utils.Constants.DOMAIN_CAMERA_FILE
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
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

    private var associateFilesFragment = AssociateFilesFragment()
    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetAssociate!!.bottomSheetAssociate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setViews()
        setObservers()
        binding.setListeners()

        verifyIfSelectedVideoWasChanged()

        domainInformationVideo?.let {
            setVideoInformation()
            createVideoPlayer(it)
        } ?: run {
            getInformationOfVideo()
        }

        getVideoMetadata()
    }

    private fun ActivityVideoPlaybackBinding.setViews() {
        setAppBar()
        showLoadingDialog()
        configureBottomSheet()
        setCatalogLists()
        addEditTextFilter()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        verifyEventEmpty()
    }

    private fun ActivityVideoPlaybackBinding.configureBottomSheet() {
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetAssociate?.buttonCloseAssociateFiles?.setOnClickListener {
            FilesAssociatedByUser.temporal.addAll(FilesAssociatedByUser.value)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // The interface requires to implement this method but not needed
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            shadowPlaybackView.isVisible = false
                            bottomSheetAssociate?.fragmentAssociateHolder?.id?.let {
                                supportFragmentManager.detachFragment(it)
                            }
                        }
                        else -> shadowPlaybackView.isVisible = true
                    }
                }
            })
    }

    private fun ActivityVideoPlaybackBinding.setAppBar() {
        layoutCustomAppBar?.run {
            textViewTitle.text = getString(R.string.video_detail)
            buttonSimpleList.isVisible = false
            buttonThumbnailList.isVisible = false
        }
    }

    private fun ActivityVideoPlaybackBinding.addEditTextFilter() {
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
        val comma = ","
        val ampersand = "&"
        val quotes = "\""

        val lengthFilter = InputFilter.LengthFilter(length)
        val charactersFilter = InputFilter { source, _, _, _, _, _ ->
            if (source != null &&
                (
                    comma.contains("" + source) ||
                        ampersand.contains("" + source) ||
                        quotes.contains("" + source)
                    )
            ) {
                ""
            } else null
        }

        return arrayOf(lengthFilter, charactersFilter)
    }

    private fun verifyEventEmpty() {
        if (CameraInfo.metadataEvents.isEmpty()) showErrorInEvents()
    }

    private fun ActivityVideoPlaybackBinding.setCatalogLists() {
        eventList.add(getString(R.string.select))
        eventList.addAll(CameraInfo.metadataEvents.map { it.name })
        raceList.addAll(resources.getStringArray(R.array.race_spinner))
        genderList.addAll(resources.getStringArray(R.array.gender_spinner))

        eventValue.adapter =
            ArrayAdapter(this@VideoPlaybackActivity, R.layout.spinner_item, eventList)
        raceValue.adapter =
            ArrayAdapter(this@VideoPlaybackActivity, R.layout.spinner_item, raceList)
        genderValue.adapter =
            ArrayAdapter(this@VideoPlaybackActivity, R.layout.spinner_item, genderList)
    }

    private fun showErrorInEvents() {
        binding.layoutVideoPlayback.showErrorSnackBar(
            getString(R.string.catalog_error_video_playback),
            Snackbar.LENGTH_LONG
        )
    }

    private fun setObservers() {
        isNetworkAlertShowing.observe(this, ::managePlaybackOnAlert)
        viewModel.let {
            it.domainInformationVideoLiveData.observe(this, ::manageGetVideoInformationResult)
            it.saveVideoMetadataLiveData.observe(this, ::manageSaveVideoMetadataResult)
            it.videoMetadataLiveData.observe(this, ::manageGetVideoMetadataResult)
        }
    }

    private fun ActivityVideoPlaybackBinding.setListeners() {
        buttonFullScreenListener()
        saveButtonListener()
        arrowBackListener()
        buttonAssociateSnapshotsListener()
        buttonAssociateAudiosListener()
        associateFilesListener()
        mediaPlayerListener()
        scrollListener()
    }

    private fun mediaPlayerListener() {
        viewModel.mediaPlayer.isPlayingCallback = {
            if (viewModel.mediaPlayer.isEndReached) updateLastInteraction()
            updateLiveOrPlaybackActive(it)
        }
    }

    private fun ActivityVideoPlaybackBinding.associateFilesListener() {
        associateFilesFragment.onAssociateFiles = {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            associateFilesFragment.setFilesAssociatedFromMetadata(FilesAssociatedByUser.temporal)
            FilesAssociatedByUser.setFinalValue(FilesAssociatedByUser.temporal)
            showAssociatedFiles()
            layoutVideoPlayback.showSuccessSnackBar(getString(R.string.snapshots_added_success))
        }
    }

    private fun ActivityVideoPlaybackBinding.buttonAssociateAudiosListener() {
        buttonAssociateAudios?.setOnClickListenerCheckConnection {
            showFragmentAssociateFilesOfType(AUDIO_LIST)
        }
    }

    private fun ActivityVideoPlaybackBinding.buttonAssociateSnapshotsListener() {
        buttonAssociateSnapshots.setOnClickListenerCheckConnection {
            showFragmentAssociateFilesOfType(SNAPSHOT_LIST)
        }
    }

    private fun ActivityVideoPlaybackBinding.arrowBackListener() {
        layoutCustomAppBar?.imageButtonBackArrow?.setOnClickListenerCheckConnection {
            onBackPressed()
        }
    }

    private fun ActivityVideoPlaybackBinding.saveButtonListener() {
        saveButtonVideoPlayback.setOnClickListenerCheckConnection {
            saveVideoMetadataInCamera()
        }
    }

    private fun ActivityVideoPlaybackBinding.buttonFullScreenListener() {
        buttonFullScreen.setOnClickListenerCheckConnection {
            changeScreenOrientation()
        }
    }

    private fun ActivityVideoPlaybackBinding.scrollListener() {
        scrollLayoutMetadata.viewTreeObserver.addOnScrollChangedListener {
            val scrollBounds = Rect()
            scrollLayoutMetadata.getHitRect(scrollBounds)
            if (!fakeSurfaceVideoPlayback.getLocalVisibleRect(
                    scrollBounds
                ) && viewModel.mediaPlayer.isPlaying
            ) {
                viewModel.mediaPlayer.pause()
            }
        }
    }

    private fun managePlaybackOnAlert(isShowing: Boolean) {
        if (isShowing) viewModel.mediaPlayer.pause()
    }

    private fun manageSaveVideoMetadataResult(result: Result<Unit>) {
        with(result) {
            doIfSuccess {
                showToast(getString(R.string.video_metadata_saved_success))
                onBackPressed()
            }
            doIfError {
                showToast(getString(R.string.video_metadata_save_error))
            }
        }
        hideLoadingDialog()
    }

    private fun manageGetVideoMetadataResult(result: Result<DomainVideoMetadata>) {
        with(result) {
            doIfSuccess {
                currentMetadata = it
                setVideoMetadata(it)
                CameraInfo.areNewChanges = true
            }
            doIfError {
                showToast(getString(R.string.get_video_metadata_error))
                finish()
            }
        }
    }

    private fun manageGetVideoInformationResult(result: Result<DomainInformationVideo>) {
        with(result) {
            doIfSuccess {
                domainInformationVideo = it
                createVideoPlayer(it)
                setVideoInformation()
            }
            doIfError {
                if (isAllowedToAttemptToGetInformation()) {
                    currentAttempts += 1
                    currentVideo?.let(viewModel::getInformationOfVideo)
                } else {
                    showToast(getString(R.string.error_get_information_metadata))
                    finish()
                }
            }
        }
    }

    private fun setVideoMetadata(videoMetadata: DomainVideoMetadata) {
        videoMetadata.metadata?.let {
            with(binding) {
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
        }

        videoMetadata.associatedFiles?.let {
            FilesAssociatedByUser.setTemporalValue(it as MutableList)
            FilesAssociatedByUser.setFinalValue(it)
            associateFilesFragment.setFilesAssociatedFromMetadata(it)
        }

        binding.showAssociatedFiles()
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

    private fun ActivityVideoPlaybackBinding.showAssociatedFiles() {
        showAssociatedSnapshots()
        if (CameraInfo.cameraType == CameraType.X2) showAssociatedAudios()
    }

    private fun ActivityVideoPlaybackBinding.showAssociatedSnapshots() {
        layoutAssociatedSnapshots.removeAllViews()
        val associatedSnapshots =
            FilesAssociatedByUser.value.filter { it.name.contains(MediaType.JPG.name) }
        layoutAssociatedSnapshots.isVisible = associatedSnapshots.isNullOrEmpty().not()
        associatedSnapshots.forEach {
            createSnapshotTagInPosition(layoutAssociatedSnapshots.childCount, it.date)
        }
    }

    private fun ActivityVideoPlaybackBinding.showAssociatedAudios() {
        layoutAssociatedAudios?.removeAllViews()
        val associatedAudios =
            FilesAssociatedByUser.value.filter { it.name.contains(MediaType.WAV.name) }
        layoutAssociatedAudios?.isVisible = associatedAudios.isNullOrEmpty().not()
        associatedAudios.forEach {
            layoutAssociatedAudios?.childCount?.let { position ->
                createAudioTagInPosition(position, it.date)
            }
        }
    }

    private fun ActivityVideoPlaybackBinding.createSnapshotTagInPosition(
        position: Int,
        text: String
    ) = layoutAssociatedSnapshots.addView(createFilterTag(text), position)

    private fun ActivityVideoPlaybackBinding.createAudioTagInPosition(
        position: Int,
        text: String
    ) = layoutAssociatedAudios?.addView(createFilterTag(text), position)

    private fun ActivityVideoPlaybackBinding.createFilterTag(text: String) =
        SafeFleetFilterTag(baseContext, null, 0).apply {
            tagText = text
            onClicked = { onTagRemoved(text) }
        }

    private fun ActivityVideoPlaybackBinding.onTagRemoved(text: String) {
        removeAssociatedFile(text)
        showAssociatedFiles()
    }

    private fun removeAssociatedFile(date: String) {
        val index = FilesAssociatedByUser.value.indexOfFirst { it.date == date }
        if (index >= 0) {
            FilesAssociatedByUser.value.removeAt(index)
            FilesAssociatedByUser.setTemporalValue(FilesAssociatedByUser.value)
            associateFilesFragment.setFilesAssociatedFromMetadata(FilesAssociatedByUser.value)
        }
    }

    private fun ActivityVideoPlaybackBinding.showFragmentAssociateFilesOfType(listType: String) {
        viewModel.mediaPlayer.pause()

        associateFilesFragment.arguments = Bundle().apply {
            putString(FILE_LIST_TYPE, listType)
        }

        supportFragmentManager.attachFragment(
            R.id.fragmentAssociateHolder,
            associateFilesFragment,
            AssociateFilesFragment.TAG
        )

        setBottomSheetTitle(listType)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        FilesAssociatedByUser.setTemporalValue(FilesAssociatedByUser.value)
    }

    private fun ActivityVideoPlaybackBinding.setBottomSheetTitle(listType: String) {
        bottomSheetAssociate?.textViewAssociateFiles?.text = when (listType) {
            SNAPSHOT_LIST -> getString(R.string.associate_snapshots_to_video)
            else -> getString(R.string.associate_audios_to_video)
        }
    }

    private fun ActivityVideoPlaybackBinding.saveVideoMetadataInCamera() {
        hideKeyboard()
        if (eventValue.selectedItem == eventList[0]) {
            layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
            return
        }
        CameraInfo.areNewChanges = true
        showLoadingDialog()
        viewModel.saveVideoMetadata(getNewMetadataFromForm())
        isVideoMetadataChangesSaved = true
    }

    private fun getInformationOfVideo() {
        currentVideo = getCameraConnectFileFromIntent()
        currentVideo?.run {
            viewModel.getInformationOfVideo(this)
        }
    }

    private fun getVideoMetadata() {
        currentVideo?.run {
            viewModel.getVideoMetadata(name, nameFolder)
        }
    }

    private fun getCameraConnectFileFromIntent() =
        intent?.getSerializableExtra(DOMAIN_CAMERA_FILE) as DomainCameraFile

    private fun isAllowedToAttemptToGetInformation() = currentAttempts <= ATTEMPTS_ALLOWED

    private fun setVideoInformation() {
        with(binding) {
            videoNameValue.text = currentVideo?.name
            startTimeValue.text = currentVideo?.getDateDependingOnNameLength()
            val durationText = domainInformationVideo?.duration?.toLong()?.times(1000)
                ?.milliSecondsToString()
            durationValue.text = durationText
        }
    }

    private fun createVideoPlayer(domainInformationVideo: DomainInformationVideo) {
        viewModel.mediaPlayer.apply {
            val controls = binding.getMediaPlayerControls()
            val duration = getVideoDurationMillis(domainInformationVideo)
            setControls(controls, duration, lifecycle)
            create(domainInformationVideo.urlVideo, binding.surfaceVideoPlayback)
            if (!isEndReached && !isPaused) play()
        }
    }

    private fun ActivityVideoPlaybackBinding.getMediaPlayerControls() =
        MediaPlayerControls(
            buttonPlay,
            textViewPlayerTime,
            textViewPlayerDuration,
            seekProgressVideo,
            buttonAspect
        )

    private fun getVideoDurationMillis(domainInformationVideo: DomainInformationVideo) =
        domainInformationVideo.duration.toLong().times(1000)

    private fun changeScreenOrientation() {
        requestedOrientation =
            if (isInPortraitMode()) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

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

    private fun getNewMetadataFromForm(): DomainVideoMetadata {
        var gender = ""
        var race = ""
        val event = if (binding.eventValue.selectedItemPosition != 0)
            CameraInfo.metadataEvents[binding.eventValue.selectedItemPosition - 1]
        else MetadataEvent("", "", "")

        if (binding.genderValue.selectedItem != genderList[0]) {
            gender = binding.genderValue.selectedItem.toString()
        }

        if (binding.raceValue.selectedItem != raceList[0]) {
            race = binding.raceValue.selectedItem.toString()
        }

        return DomainVideoMetadata(
            fileName = binding.videoNameValue.text.toString(),
            metadata = DomainMetadata(
                event = event,
                partnerID = binding.partnerIdValue.text.toString(),
                ticketNumber = binding.ticket1Value.text.toString(),
                ticketNumber2 = binding.ticket2Value.text.toString(),
                caseNumber = binding.case1Value.text.toString(),
                caseNumber2 = binding.case2Value.text.toString(),
                dispatchNumber = binding.dispatch1Value.text.toString(),
                dispatchNumber2 = binding.dispatch2Value.text.toString(),
                location = binding.locationValue.text.toString(),
                remarks = binding.notesValue.text.toString(),
                firstName = binding.firstNameValue.text.toString(),
                lastName = binding.lastNameValue.text.toString(),
                gender = gender,
                race = race,
                driverLicense = binding.driverLicenseValue.text.toString(),
                licensePlate = binding.licensePlateValue.text.toString()
            ),
            nameFolder = currentVideo?.nameFolder,
            officerId = CameraInfo.officerId,
            path = currentMetadata.path ?: currentVideo?.path,
            associatedFiles = FilesAssociatedByUser.value,
            serialNumber = CameraInfo.serialNumber,
            endTime = currentMetadata.endTime,
            gmtOffset = currentMetadata.gmtOffset,
            hash = currentMetadata.hash,
            preEvent = currentMetadata.preEvent,
            startTime = currentMetadata.startTime,
            videoSpecs = currentMetadata.videoSpecs
        )
    }

    private fun restartObjectOfCompanion() {
        domainInformationVideo = null
    }

    override fun onBackPressed() {
        if (isInPortraitMode()) {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                viewModel.mediaPlayer.pause()
                if (theMetadataWasEdited()) createAlertDialogUnsavedChanges()
                else super.onBackPressed()
            } else bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else changeScreenOrientation()
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
