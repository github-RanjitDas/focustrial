package com.lawmobile.presentation.ui.videoPlayback

import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityVideoPlaybackBinding
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.convertMilliSecondsToString
import com.lawmobile.presentation.extensions.createAlertDialogUnsavedChanges
import com.lawmobile.presentation.extensions.detachFragment
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.associateSnapshots.AssociateSnapshotsFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.DOMAIN_CAMERA_FILE
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetFilterTag
import org.videolan.libvlc.MediaPlayer

class VideoPlaybackActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoPlaybackBinding

    private val videoPlaybackViewModel: VideoPlaybackViewModel by viewModels()
    private val eventList = mutableListOf<String>()
    private val raceList = mutableListOf<String>()
    private val genderList = mutableListOf<String>()

    private var currentAttempts = 0
    private var isVideoMetadataChangesSaved = false
    private lateinit var currentMetadata: DomainVideoMetadata

    private var associateSnapshotsFragment = AssociateSnapshotsFragment()
    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheetAssociate!!.bottomSheetAssociate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAppBar()
        showLoadingDialog()
        configureBottomSheet()
        setCatalogLists()
        addEditTextFilter()
        setObservers()
        configureListeners()
        hideKeyboard()
        verifyIfSelectedVideoWasChanged()

        currentTimeVideoInMilliSeconds = 0
        currentProgressInVideo = 0

        domainInformationVideo?.let {
            setVideoInformation()
            createVideoPlaybackInSurface(it)
        } ?: run {
            getInformationOfVideo()
        }

        stopVideoWhenScrolling()
        getVideoMetadata()
    }

    override fun onResume() {
        super.onResume()
        verifyEventEmpty()
        if (!videoPlaybackViewModel.isMediaPlayerPlaying()) {
            domainInformationVideo?.let {
                createVideoPlaybackInSurface(it)
                playVideoPlayback()
            }
        }
    }

    private fun stopVideoWhenScrolling() {
        binding.scrollLayoutMetadata.viewTreeObserver.addOnScrollChangedListener {
            val scrollBounds = Rect()
            binding.scrollLayoutMetadata.getHitRect(scrollBounds)
            if (!binding.fakeSurfaceVideoPlayback.getLocalVisibleRect(
                    scrollBounds
                ) && videoPlaybackViewModel.isMediaPlayerPlaying()
            ) {
                pauseVideoPlayback()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoPlaybackViewModel.isMediaPlayerPlaying()) {
            pauseVideoPlayback()
        }
    }

    private fun configureBottomSheet() {
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.bottomSheetAssociate?.buttonCloseAssociateSnapshots?.setOnClickListener {
            SnapshotsAssociatedByUser.temporal.addAll(SnapshotsAssociatedByUser.value)
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
                            binding.shadowPlaybackView.isVisible = false
                            binding.bottomSheetAssociate?.fragmentAssociateHolder?.id?.let {
                                supportFragmentManager.detachFragment(
                                    it
                                )
                            }
                        }
                        else -> binding.shadowPlaybackView.isVisible = true
                    }
                }
            })
    }

    private fun setAppBar() {
        binding.layoutCustomAppBar?.run {
            textViewTitle.text = getString(R.string.video_detail)
            buttonSimpleList.isVisible = false
            buttonThumbnailList.isVisible = false
        }
    }

    private fun addEditTextFilter() {
        with(binding) {
            partnerIdValue.filters = getFiltersWithLength(20)
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
        if (CameraInfo.metadataEvents.isEmpty()) {
            showErrorInEvents()
        }
    }

    private fun setCatalogLists() {
        eventList.add(getString(R.string.select))
        eventList.addAll(CameraInfo.metadataEvents.map { it.name })
        raceList.addAll(resources.getStringArray(R.array.race_spinner))
        genderList.addAll(resources.getStringArray(R.array.gender_spinner))

        with(binding) {
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

    private fun setObservers() {
        isNetworkAlertShowing.observe(this, Observer(::managePlaybackOnAlert))
        videoPlaybackViewModel.let {
            it.domainInformationVideoLiveData.observe(this, ::manageGetVideoInformationResult)
            it.saveVideoMetadataLiveData.observe(this, ::manageSaveVideoMetadataResult)
            it.videoMetadataLiveData.observe(this, ::manageGetVideoMetadataResult)
        }
    }

    private fun configureListeners() {
        with(binding) {
            buttonPlay.setOnClickListenerCheckConnection {
                manageButtonPlayPause()
            }
            buttonFullScreen.setOnClickListenerCheckConnection {
                changeScreenOrientation()
            }
            buttonAspect.setOnClickListenerCheckConnection {
                videoPlaybackViewModel.changeAspectRatio()
            }
            saveButtonVideoPlayback.setOnClickListenerCheckConnection {
                saveVideoMetadataInCamera()
            }
            layoutCustomAppBar?.imageButtonBackArrow?.setOnClickListenerCheckConnection {
                onBackPressed()
            }
            buttonAssociateSnapshots.setOnClickListenerCheckConnection {
                showAssociateSnapshotsBottomSheet()
            }
        }
        configureListenerSeekBar()
        configureMediaEventListener()
        associateSnapshotsFragment.onAssociateSnapshots = ::handleAssociateSnapshots
    }

    private fun manageCurrentTimeInVideo(time: Long) {
        if (time > currentTimeVideoInMilliSeconds) {
            currentTimeVideoInMilliSeconds = time
            updateProgressVideoInView()
        }

        if (currentProgressInVideo == 100 && videoPlaybackViewModel.isMediaPlayerPlaying()) {
            updateLastInteraction()
            binding.buttonPlay.setImageResource(R.drawable.ic_media_play)
            updateLiveOrPlaybackActive(false)
            binding.buttonAspect.isClickable = false
        }
    }

    private fun managePlaybackOnAlert(isShowing: Boolean) {
        if (isShowing) {
            pauseVideoPlayback()
        } else {
            setProgressToVideo(0)
            binding.buttonPlay.setImageResource(R.drawable.ic_media_pause)
        }
    }

    private fun manageSaveVideoMetadataResult(result: Result<Unit>) {
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

    private fun manageGetVideoMetadataResult(result: Result<DomainVideoMetadata>) {
        with(result) {
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

    private fun manageGetVideoInformationResult(result: Result<DomainInformationVideo>) {
        with(result) {
            doIfSuccess {
                totalDurationVideoInMilliSeconds = it.duration.toLong() * 1000
                domainInformationVideo = it
                createVideoPlaybackInSurface(it)
                playVideoPlayback()
                setVideoInformation()
            }
            doIfError {
                if (isAllowedToAttemptToGetInformation()) {
                    currentAttempts += 1
                    currentVideo?.let(videoPlaybackViewModel::getInformationOfVideo)
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

        videoMetadata.associatedPhotos?.let {
            SnapshotsAssociatedByUser.setTemporalValue(it as MutableList)
            SnapshotsAssociatedByUser.setFinalValue(it)
            associateSnapshotsFragment.setSnapshotsAssociatedFromMetadata(it)
        }

        showSnapshotsAssociated()
        hideLoadingDialog()
    }

    private fun getSpinnerSelection(list: List<String>, value: String?): Int {
        return if (value == null || value.isEmpty()) 0
        else list.indexOfFirst { it == value }
    }

    private fun verifyIfSelectedVideoWasChanged() {
        val videoWasChanged = getCameraConnectFileFromIntent() != currentVideo
        if (videoWasChanged) {
            restartObjectOfCompanion()
        }
    }

    private fun handleAssociateSnapshots() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        associateSnapshotsFragment.setSnapshotsAssociatedFromMetadata(SnapshotsAssociatedByUser.temporal)
        SnapshotsAssociatedByUser.setFinalValue(SnapshotsAssociatedByUser.temporal)
        showSnapshotsAssociated()
        binding.layoutVideoPlayback.showSuccessSnackBar(getString(R.string.snapshots_added_success))
    }

    private fun showSnapshotsAssociated() {
        binding.layoutAssociatedSnapshots.removeAllViews()
        binding.layoutAssociatedSnapshots.isVisible =
            !SnapshotsAssociatedByUser.value.isNullOrEmpty()
        SnapshotsAssociatedByUser.value.forEach {
            binding.layoutAssociatedSnapshots.childCount.let { position ->
                createTagInPosition(position, it.date)
            }
        }
    }

    private fun createTagInPosition(position: Int, text: String) {
        binding.layoutAssociatedSnapshots.addView(
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
        val index = SnapshotsAssociatedByUser.value.indexOfFirst {
            it.date == date
        }
        if (index >= 0) {
            SnapshotsAssociatedByUser.value.removeAt(index)
            SnapshotsAssociatedByUser.setTemporalValue(SnapshotsAssociatedByUser.value)
            associateSnapshotsFragment.setSnapshotsAssociatedFromMetadata(SnapshotsAssociatedByUser.value)
        }
    }

    private fun showAssociateSnapshotsBottomSheet() {
        pauseVideoPlayback()
        supportFragmentManager.attachFragment(
            R.id.fragmentAssociateHolder,
            associateSnapshotsFragment,
            AssociateSnapshotsFragment.TAG
        )
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        SnapshotsAssociatedByUser.setTemporalValue(SnapshotsAssociatedByUser.value)
    }

    private fun configureMediaEventListener() {
        videoPlaybackViewModel.setMediaEventListener {
            when (it.type) {
                MediaPlayer.Event.EncounteredError -> {
                    domainInformationVideo?.let(::createVideoPlaybackInSurface)
                }
                MediaPlayer.Event.MediaChanged -> {
                    playVideoPlayback()
                }
                MediaPlayer.Event.TimeChanged -> {
                    manageCurrentTimeInVideo(videoPlaybackViewModel.getTimeInMillisMediaPlayer())
                }
            }
        }
    }

    private fun saveVideoMetadataInCamera() {
        hideKeyboard()
        if (binding.eventValue.selectedItem == eventList[0]) {
            binding.layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
            return
        }
        CameraInfo.areNewChanges = true
        showLoadingDialog()
        videoPlaybackViewModel.saveVideoMetadata(getNewMetadataFromForm())
        isVideoMetadataChangesSaved = true
    }

    private fun manageButtonPlayPause() {
        if (videoPlaybackViewModel.isMediaPlayerPlaying()) {
            if (currentProgressInVideo == 100) {
                currentProgressInVideo = 0
                playVideoPlayback()
            } else {
                pauseVideoPlayback()
            }
        } else {
            playVideoPlayback()
        }
    }

    private fun getInformationOfVideo() {
        currentVideo = getCameraConnectFileFromIntent()
        currentVideo?.run {
            videoPlaybackViewModel.getInformationOfVideo(this)
        }
    }

    private fun getVideoMetadata() {
        currentVideo?.run {
            videoPlaybackViewModel.getVideoMetadata(name, nameFolder)
        }
    }

    private fun getCameraConnectFileFromIntent() =
        intent?.getSerializableExtra(DOMAIN_CAMERA_FILE) as DomainCameraFile

    private fun isAllowedToAttemptToGetInformation() = currentAttempts <= ATTEMPTS_ALLOWED

    private fun setVideoInformation() {
        with(binding) {
            videoNameValue.text = currentVideo?.name
            startTimeValue.text = currentVideo?.getCreationDate()
            durationValue.text = totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
        }
    }

    private fun createVideoPlaybackInSurface(domainInformationVideo: DomainInformationVideo) {
        videoPlaybackViewModel.createVLCMediaPlayer(
            domainInformationVideo.urlVideo,
            binding.surfaceVideoPlayback
        )
    }

    private fun playVideoPlayback() {
        binding.buttonPlay.setImageResource(R.drawable.ic_media_pause)
        updateLiveOrPlaybackActive(true)
        videoPlaybackViewModel.playMediaPlayer()
        setProgressToVideo(currentProgressInVideo)
        binding.buttonAspect.isClickable = true
    }

    private fun pauseVideoPlayback() {
        binding.buttonPlay.setImageResource(R.drawable.ic_media_play)
        updateLiveOrPlaybackActive(false)
        videoPlaybackViewModel.pauseMediaPlayer()
        binding.buttonAspect.isClickable = false
    }

    private fun changeScreenOrientation() {
        requestedOrientation =
            if (isInPortraitMode()) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
    }

    override fun onBackPressed() {
        if (isInPortraitMode()) {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                videoPlaybackViewModel.stopMediaPlayer()
                if (verifyVideoMetadataWasEdited())
                    this.createAlertDialogUnsavedChanges()
                else super.onBackPressed()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        } else {
            changeScreenOrientation()
        }
    }

    private fun verifyVideoMetadataWasEdited(): Boolean {
        if (!isVideoMetadataChangesSaved) {
            verifyVideoMetadataParameters()

            val newMetadata = getNewMetadataFromForm().metadata
            val oldMetadata = currentMetadata.metadata
                ?: return newMetadata?.run {
                    event != null ||
                        !partnerID.isNullOrEmpty() ||
                        !ticketNumber.isNullOrEmpty() ||
                        !ticketNumber2.isNullOrEmpty() ||
                        !caseNumber.isNullOrEmpty() ||
                        !dispatchNumber.isNullOrEmpty() ||
                        !dispatchNumber2.isNullOrEmpty() ||
                        !location.isNullOrEmpty() ||
                        !remarks.isNullOrEmpty() ||
                        !firstName.isNullOrEmpty() ||
                        !lastName.isNullOrEmpty() ||
                        !driverLicense.isNullOrEmpty() ||
                        !licensePlate.isNullOrEmpty() ||
                        !gender.isNullOrEmpty() ||
                        !race.isNullOrEmpty()
                } ?: false

            return oldMetadata.let { fromJson ->
                newMetadata?.let { fromForm ->
                    fromJson.event?.name != fromForm.event?.name ||
                        fromJson.partnerID != fromForm.partnerID ||
                        fromJson.ticketNumber != fromForm.ticketNumber ||
                        fromJson.ticketNumber2 != fromForm.ticketNumber2 ||
                        fromJson.caseNumber != fromForm.caseNumber ||
                        fromJson.caseNumber2 != fromForm.caseNumber2 ||
                        fromJson.dispatchNumber != fromForm.dispatchNumber ||
                        fromJson.dispatchNumber2 != fromForm.dispatchNumber2 ||
                        fromJson.location != fromForm.location ||
                        fromJson.remarks != fromForm.remarks ||
                        fromJson.firstName != fromForm.firstName ||
                        fromJson.lastName != fromForm.lastName ||
                        fromJson.driverLicense != fromForm.driverLicense ||
                        fromJson.licensePlate != fromForm.licensePlate ||
                        fromJson.gender != fromForm.gender ||
                        fromJson.race != fromForm.race ||
                        currentMetadata.associatedPhotos != SnapshotsAssociatedByUser.value
                } ?: false
            }
        }
        return false
    }

    private fun getNewMetadataFromForm(): DomainVideoMetadata {
        var gender: String? = null
        var race: String? = null
        val event =
            if (binding.eventValue.selectedItemPosition != 0)
                CameraInfo.metadataEvents[binding.eventValue.selectedItemPosition - 1]
            else null

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
            associatedPhotos = SnapshotsAssociatedByUser.value,
            serialNumber = CameraInfo.serialNumber,
            endTime = currentMetadata.endTime,
            gmtOffset = currentMetadata.gmtOffset,
            hash = currentMetadata.hash,
            preEvent = currentMetadata.preEvent,
            startTime = currentMetadata.startTime,
            videoSpecs = currentMetadata.videoSpecs
        )
    }

    private fun verifyVideoMetadataParameters() {
        currentMetadata.metadata?.apply {
            partnerID = handleNullParameter(partnerID)
            ticketNumber = handleNullParameter(ticketNumber)
            ticketNumber2 = handleNullParameter(ticketNumber2)
            caseNumber = handleNullParameter(caseNumber)
            caseNumber2 = handleNullParameter(caseNumber2)
            dispatchNumber = handleNullParameter(dispatchNumber)
            dispatchNumber2 = handleNullParameter(dispatchNumber2)
            location = handleNullParameter(location)
            remarks = handleNullParameter(remarks)
            firstName = handleNullParameter(firstName)
            lastName = handleNullParameter(lastName)
            driverLicense = handleNullParameter(driverLicense)
            licensePlate = handleNullParameter(licensePlate)
        }
    }

    private fun handleNullParameter(string: String?) = string ?: ""

    private fun updateProgressVideoInView() {
        val progressVideo =
            ((currentTimeVideoInMilliSeconds.toDouble() / totalDurationVideoInMilliSeconds.toDouble()) * 100).toInt()
        binding.seekProgressVideo.progress = progressVideo
        val elapsedTime = currentTimeVideoInMilliSeconds.convertMilliSecondsToString()
        updateTextElapsedTimeAndLeftTime(elapsedTime)
    }

    private fun updateTextElapsedTimeAndLeftTime(elapsedTime: String) {
        runOnUiThread {
            binding.textViewPlayerTime.text = elapsedTime
            binding.textViewPlayerDuration.text =
                totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
        }
    }

    private fun configureListenerSeekBar() {
        var isFromUser = false
        binding.seekProgressVideo.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    isFromUser = fromUser
                    currentProgressInVideo = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.d("onStartTrackingTouch", seekBar.toString())
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (isFromUser) {
                        seekBar?.let {
                            setProgressToVideo(it.progress)
                        }
                    }
                }
            })
    }

    private fun setProgressToVideo(progress: Int) {
        currentProgressInVideo = progress
        videoPlaybackViewModel.setProgressMediaPlayer(progress.toFloat())
        binding.seekProgressVideo.progress = progress
        currentTimeVideoInMilliSeconds = totalDurationVideoInMilliSeconds * (progress / 100)
    }

    private fun restartObjectOfCompanion() {
        domainInformationVideo = null
        totalDurationVideoInMilliSeconds = 0
        currentTimeVideoInMilliSeconds = 0
        currentProgressInVideo = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        SnapshotsAssociatedByUser.cleanList()
    }

    companion object {
        private var currentVideo: DomainCameraFile? = null
        private var domainInformationVideo: DomainInformationVideo? = null
        private var totalDurationVideoInMilliSeconds: Long = 0
        private var currentTimeVideoInMilliSeconds: Long = 0
        private var currentProgressInVideo = 0
        const val ATTEMPTS_ALLOWED = 2
    }
}
