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
import com.lawmobile.domain.entities.*
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityVideoPlaybackBinding
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.associateSnapshots.AssociateSnapshotsFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.DOMAIN_CAMERA_FILE
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.commons.helpers.hideKeyboard
import com.safefleet.mobile.commons.widgets.SafeFleetFilterTag
import org.videolan.libvlc.MediaPlayer

class VideoPlaybackActivity : BaseActivity() {

    private lateinit var activityVideoPlaybackBinding: ActivityVideoPlaybackBinding

    private val videoPlaybackViewModel: VideoPlaybackViewModel by viewModels()
    private val eventList = mutableListOf<String>()
    private val raceList = mutableListOf<String>()
    private val genderList = mutableListOf<String>()

    private var currentAttempts = 0
    private var isVideoMetadataChangesSaved = false
    private lateinit var currentMetadata: DomainVideoMetadata

    private var associateSnapshotsFragment = AssociateSnapshotsFragment()
    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(activityVideoPlaybackBinding.bottomSheetAssociate!!.bottomSheetAssociate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVideoPlaybackBinding = ActivityVideoPlaybackBinding.inflate(layoutInflater)
        setContentView(activityVideoPlaybackBinding.root)

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
        activityVideoPlaybackBinding.scrollLayoutMetadata.viewTreeObserver.addOnScrollChangedListener {
            val scrollBounds = Rect()
            activityVideoPlaybackBinding.scrollLayoutMetadata.getHitRect(scrollBounds)
            if (!activityVideoPlaybackBinding.fakeSurfaceVideoPlayback.getLocalVisibleRect(
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

        activityVideoPlaybackBinding.bottomSheetAssociate?.buttonCloseAssociateSnapshots?.setOnClickListener {
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
                        activityVideoPlaybackBinding.shadowPlaybackView.isVisible = false
                        activityVideoPlaybackBinding.bottomSheetAssociate?.fragmentAssociateHolder?.id?.let {
                            supportFragmentManager.detachFragment(
                                it
                            )
                        }
                    }
                    else -> activityVideoPlaybackBinding.shadowPlaybackView.isVisible = true
                }
            }
        })
    }

    private fun setAppBar() {
        activityVideoPlaybackBinding.layoutFileListAppBar?.fileListAppBarTitle?.text =
            getString(R.string.video_detail)
        activityVideoPlaybackBinding.layoutFileListAppBar?.buttonSimpleList?.isVisible = false
        activityVideoPlaybackBinding.layoutFileListAppBar?.buttonThumbnailList?.isVisible = false
    }

    private fun addEditTextFilter() {
        activityVideoPlaybackBinding.partnerIdValue.filters = getFiltersWithLength(20)
        activityVideoPlaybackBinding.ticket1Value.filters = getFiltersWithLength(20)
        activityVideoPlaybackBinding.ticket2Value.filters = getFiltersWithLength(20)
        activityVideoPlaybackBinding.case1Value.filters = getFiltersWithLength(50)
        activityVideoPlaybackBinding.case2Value.filters = getFiltersWithLength(50)
        activityVideoPlaybackBinding.dispatch1Value.filters = getFiltersWithLength(30)
        activityVideoPlaybackBinding.dispatch2Value.filters = getFiltersWithLength(30)
        activityVideoPlaybackBinding.locationValue.filters = getFiltersWithLength(30)
        activityVideoPlaybackBinding.notesValue.filters = getFiltersWithLength(100)
        activityVideoPlaybackBinding.firstNameValue.filters = getFiltersWithLength(30)
        activityVideoPlaybackBinding.lastNameValue.filters = getFiltersWithLength(30)
        activityVideoPlaybackBinding.driverLicenseValue.filters = getFiltersWithLength(30)
        activityVideoPlaybackBinding.licensePlateValue.filters = getFiltersWithLength(30)
    }

    private fun getFiltersWithLength(length: Int): Array<InputFilter> {
        val comma = ","
        val ampersand = "&"
        val quotes = "\""

        val lengthFilter = InputFilter.LengthFilter(length)
        val charactersFilter = InputFilter { source, _, _, _, _, _ ->
            if (source != null
                && (comma.contains("" + source)
                        || ampersand.contains("" + source)
                        || quotes.contains("" + source))
            ) {
                ""
            } else null
        }

        return arrayOf(lengthFilter, charactersFilter)
    }

    private fun verifyEventEmpty() {
        if (CameraInfo.events.isEmpty()) {
            showErrorInEvents()
        }
    }

    private fun setCatalogLists() {
        eventList.add(getString(R.string.select))
        eventList.addAll(CameraInfo.events.map { it.name })
        raceList.addAll(resources.getStringArray(R.array.race_spinner))
        genderList.addAll(resources.getStringArray(R.array.gender_spinner))

        activityVideoPlaybackBinding.eventValue.adapter =
            ArrayAdapter(this, R.layout.spinner_item, eventList)
        activityVideoPlaybackBinding.raceValue.adapter =
            ArrayAdapter(this, R.layout.spinner_item, raceList)
        activityVideoPlaybackBinding.genderValue.adapter =
            ArrayAdapter(this, R.layout.spinner_item, genderList)
    }

    private fun showErrorInEvents() {
        activityVideoPlaybackBinding.layoutVideoPlayback.showErrorSnackBar(
            getString(R.string.catalog_error_video_playback),
            Snackbar.LENGTH_LONG
        )
    }

    private fun setObservers() {
        isMobileDataAlertShowing.observe(this, Observer(::managePlaybackOnAlert))
        videoPlaybackViewModel.domainInformationVideoLiveData.observe(
            this,
            Observer(::manageGetVideoInformationResult)
        )
        videoPlaybackViewModel.saveVideoMetadataLiveData.observe(
            this,
            Observer(::manageSaveVideoMetadataResult)
        )
        videoPlaybackViewModel.videoMetadataLiveData.observe(
            this,
            Observer(::manageGetVideoMetadataResult)
        )
    }

    private fun configureListeners() {
        activityVideoPlaybackBinding.buttonPlay.setOnClickListenerCheckConnection {
            manageButtonPlayPause()
        }
        activityVideoPlaybackBinding.buttonFullScreen.setOnClickListenerCheckConnection {
            changeScreenOrientation()
        }
        activityVideoPlaybackBinding.buttonAspect.setOnClickListenerCheckConnection {
            videoPlaybackViewModel.changeAspectRatio()
        }
        activityVideoPlaybackBinding.saveButtonVideoPlayback.setOnClickListenerCheckConnection {
            saveVideoMetadataInCamera()
        }
        activityVideoPlaybackBinding.layoutFileListAppBar?.backArrowFileListAppBar?.setOnClickListenerCheckConnection {
            onBackPressed()
        }
        activityVideoPlaybackBinding.buttonAssociateSnapshots.setOnClickListenerCheckConnection {
            showAssociateSnapshotsBottomSheet()
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
            activityVideoPlaybackBinding.buttonPlay.setImageResource(R.drawable.ic_media_play)
            updateLiveOrPlaybackActive(false)
            activityVideoPlaybackBinding.buttonAspect.isClickable = false
        }
    }

    private fun managePlaybackOnAlert(isShowing: Boolean) {
        if (isShowing) {
            pauseVideoPlayback()
        } else {
            setProgressToVideo(0)
            activityVideoPlaybackBinding.buttonPlay.setImageResource(R.drawable.ic_media_pause)
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
        videoMetadata.metadata?.run {
            activityVideoPlaybackBinding.eventValue.setSelection(
                getSpinnerSelection(
                    eventList,
                    event?.name
                )
            )
            activityVideoPlaybackBinding.partnerIdValue.setText(partnerID)
            activityVideoPlaybackBinding.ticket1Value.setText(ticketNumber)
            activityVideoPlaybackBinding.ticket2Value.setText(ticketNumber2)
            activityVideoPlaybackBinding.case1Value.setText(caseNumber)
            activityVideoPlaybackBinding.case2Value.setText(caseNumber2)
            activityVideoPlaybackBinding.dispatch1Value.setText(dispatchNumber)
            activityVideoPlaybackBinding.dispatch2Value.setText(dispatchNumber2)
            activityVideoPlaybackBinding.locationValue.setText(location)
            activityVideoPlaybackBinding.notesValue.setText(remarks)
            activityVideoPlaybackBinding.firstNameValue.setText(firstName)
            activityVideoPlaybackBinding.lastNameValue.setText(lastName)
            activityVideoPlaybackBinding.genderValue.setSelection(
                getSpinnerSelection(
                    genderList,
                    gender
                )
            )
            activityVideoPlaybackBinding.raceValue.setSelection(getSpinnerSelection(raceList, race))
            activityVideoPlaybackBinding.driverLicenseValue.setText(driverLicense)
            activityVideoPlaybackBinding.licensePlateValue.setText(licensePlate)
        }

        (videoMetadata.associatedPhotos)?.let {
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
        activityVideoPlaybackBinding.layoutVideoPlayback.showSuccessSnackBar(getString(R.string.snapshots_added_success))
    }

    private fun showSnapshotsAssociated() {
        activityVideoPlaybackBinding.layoutAssociatedSnapshots.removeAllViews()
        activityVideoPlaybackBinding.layoutAssociatedSnapshots.isVisible =
            !SnapshotsAssociatedByUser.value.isNullOrEmpty()
        SnapshotsAssociatedByUser.value.forEach {
            activityVideoPlaybackBinding.layoutAssociatedSnapshots.childCount.let { position ->
                createTagInPosition(position, it.date)
            }
        }
    }

    private fun createTagInPosition(position: Int, text: String) {
        activityVideoPlaybackBinding.layoutAssociatedSnapshots.addView(
            SafeFleetFilterTag(this, null, 0).apply {
                tagText = text
                onClicked = {
                    removeAssociatedSnapshot(text)
                    showSnapshotsAssociated()
                }
            }, position
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
        if (activityVideoPlaybackBinding.eventValue.selectedItem == eventList[0]) {
            activityVideoPlaybackBinding.layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
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
        activityVideoPlaybackBinding.videoNameValue.text = currentVideo?.name
        activityVideoPlaybackBinding.startTimeValue.text = currentVideo?.getCreationDate()
        activityVideoPlaybackBinding.durationValue.text =
            totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
    }

    private fun createVideoPlaybackInSurface(domainInformationVideo: DomainInformationVideo) {
        videoPlaybackViewModel.createVLCMediaPlayer(
            domainInformationVideo.urlVideo,
            activityVideoPlaybackBinding.surfaceVideoPlayback
        )
    }

    private fun playVideoPlayback() {
        activityVideoPlaybackBinding.buttonPlay.setImageResource(R.drawable.ic_media_pause)
        updateLiveOrPlaybackActive(true)
        videoPlaybackViewModel.playMediaPlayer()
        setProgressToVideo(currentProgressInVideo)
        activityVideoPlaybackBinding.buttonAspect.isClickable = true
    }

    private fun pauseVideoPlayback() {
        activityVideoPlaybackBinding.buttonPlay.setImageResource(R.drawable.ic_media_play)
        updateLiveOrPlaybackActive(false)
        videoPlaybackViewModel.pauseMediaPlayer()
        activityVideoPlaybackBinding.buttonAspect.isClickable = false
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
            if (activityVideoPlaybackBinding.eventValue.selectedItemPosition != 0)
                CameraInfo.events[activityVideoPlaybackBinding.eventValue.selectedItemPosition - 1]
            else null

        if (activityVideoPlaybackBinding.genderValue.selectedItem != genderList[0]) {
            gender = activityVideoPlaybackBinding.genderValue.selectedItem.toString()
        }

        if (activityVideoPlaybackBinding.raceValue.selectedItem != raceList[0]) {
            race = activityVideoPlaybackBinding.raceValue.selectedItem.toString()
        }

        return DomainVideoMetadata(
            activityVideoPlaybackBinding.videoNameValue.text.toString(),
            DomainMetadata(
                event = event,
                partnerID = activityVideoPlaybackBinding.partnerIdValue.text.toString(),
                ticketNumber = activityVideoPlaybackBinding.ticket1Value.text.toString(),
                ticketNumber2 = activityVideoPlaybackBinding.ticket2Value.text.toString(),
                caseNumber = activityVideoPlaybackBinding.case1Value.text.toString(),
                caseNumber2 = activityVideoPlaybackBinding.case2Value.text.toString(),
                dispatchNumber = activityVideoPlaybackBinding.dispatch1Value.text.toString(),
                dispatchNumber2 = activityVideoPlaybackBinding.dispatch2Value.text.toString(),
                location = activityVideoPlaybackBinding.locationValue.text.toString(),
                remarks = activityVideoPlaybackBinding.notesValue.text.toString(),
                firstName = activityVideoPlaybackBinding.firstNameValue.text.toString(),
                lastName = activityVideoPlaybackBinding.lastNameValue.text.toString(),
                gender = gender,
                race = race,
                driverLicense = activityVideoPlaybackBinding.driverLicenseValue.text.toString(),
                licensePlate = activityVideoPlaybackBinding.licensePlateValue.text.toString()
            ),
            currentVideo?.nameFolder,
            CameraInfo.officerId,
            currentVideo?.path,
            SnapshotsAssociatedByUser.value,
            CameraInfo.serialNumber
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
        activityVideoPlaybackBinding.seekProgressVideo.progress = progressVideo
        val elapsedTime = currentTimeVideoInMilliSeconds.convertMilliSecondsToString()
        updateTextElapsedTimeAndLeftTime(elapsedTime)
    }

    private fun updateTextElapsedTimeAndLeftTime(elapsedTime: String) {
        runOnUiThread {
            activityVideoPlaybackBinding.textViewPlayerTime.text = elapsedTime
            activityVideoPlaybackBinding.textViewPlayerDuration.text =
                totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
        }
    }

    private fun configureListenerSeekBar() {
        var isFromUser = false
        activityVideoPlaybackBinding.seekProgressVideo.setOnSeekBarChangeListener(object :
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
        activityVideoPlaybackBinding.seekProgressVideo.progress = progress
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
