package com.lawmobile.presentation.ui.videoPlayback

import android.content.pm.ActivityInfo
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
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.associateSnapshots.AssociateSnapshotsFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import com.safefleet.mobile.commons.helpers.hideKeyboard
import com.safefleet.mobile.commons.widgets.SafeFleetFilterTag
import kotlinx.android.synthetic.main.activity_video_playback.*
import kotlinx.android.synthetic.main.bottom_sheet_associate_snapshots.*
import kotlinx.android.synthetic.main.custom_app_bar.*
import org.videolan.libvlc.MediaPlayer

class VideoPlaybackActivity : BaseActivity() {

    private val videoPlaybackViewModel: VideoPlaybackViewModel by viewModels()
    private val eventList = mutableListOf<String>()
    private val raceList = mutableListOf<String>()
    private val genderList = mutableListOf<String>()

    private var currentAttempts = 0
    private var areLinkedSnapshotsChangesSaved = true
    private var isVideoMetadataChangesSaved = false
    private lateinit var currentMetadata: CameraConnectVideoMetadata

    private var associateSnapshotsFragment = AssociateSnapshotsFragment()
    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(bottomSheetAssociate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback)

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

    override fun onPause() {
        super.onPause()
        if (videoPlaybackViewModel.isMediaPlayerPlaying()) {
            pauseVideoPlayback()
        }
    }

    private fun configureBottomSheet() {
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        buttonCloseAssociateSnapshots.setOnClickListener {
            SnapshotsAssociatedByUser.temporalAssociateSnapshot.addAll(SnapshotsAssociatedByUser.value)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        shadowPlaybackView?.isVisible = false
                        supportFragmentManager.detachFragment(fragmentAssociateHolder.id)
                    }
                    else -> shadowPlaybackView?.isVisible = true
                }
            }
        })
    }

    private fun setAppBar() {
        fileListAppBarTitle.text = getString(R.string.video_detail)
        buttonSimpleList.isVisible = false
        buttonThumbnailList.isVisible = false
    }

    private fun addEditTextFilter() {
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

        eventValue.adapter = ArrayAdapter(this, R.layout.spinner_item, eventList)
        raceValue.adapter = ArrayAdapter(this, R.layout.spinner_item, raceList)
        genderValue.adapter = ArrayAdapter(this, R.layout.spinner_item, genderList)
    }

    private fun showErrorInEvents() {
        layoutVideoPlayback.showErrorSnackBar(
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
        backArrowFileListAppBar.setOnClickListenerCheckConnection {
            onBackPressed()
        }
        buttonAssociateSnapshots.setOnClickListenerCheckConnection {
            showAssociateSnapshotsBottomSheet()
        }
        configureListenerSeekBar()
        configureMediaEventListener()
        associateSnapshotsFragment.onSnapshotsAssociated = ::handleAssociateSnapshots
    }

    private fun manageCurrentTimeInVideo(time: Long) {
        if (time > currentTimeVideoInMilliSeconds) {
            currentTimeVideoInMilliSeconds = time
            updateProgressVideoInView()
        }

        if (currentProgressInVideo == 100 && videoPlaybackViewModel.isMediaPlayerPlaying()) {
            updateLastInteraction()
            buttonPlay.setImageResource(R.drawable.ic_media_play)
            updateLiveOrPlaybackActive(false)
            buttonAspect.isClickable = false
        }
    }

    private fun managePlaybackOnAlert(isShowing: Boolean) {
        if (isShowing) {
            pauseVideoPlayback()
        } else {
            setProgressToVideo(0)
            buttonPlay.setImageResource(R.drawable.ic_media_pause)
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
        areLinkedSnapshotsChangesSaved = true
    }

    private fun manageGetVideoMetadataResult(result: Result<CameraConnectVideoMetadata>) {
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
                    connectVideo?.let(videoPlaybackViewModel::getInformationOfVideo)
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

    private fun setVideoMetadata(videoMetadata: CameraConnectVideoMetadata) {
        videoMetadata.metadata?.run {
            eventValue.setSelection(getSpinnerSelection(eventList, event?.name))
            partnerIdValue.setText(partnerID)
            ticket1Value.setText(ticketNumber)
            ticket2Value.setText(ticketNumber2)
            case1Value.setText(caseNumber)
            case2Value.setText(caseNumber2)
            dispatch1Value.setText(dispatchNumber)
            dispatch2Value.setText(dispatchNumber2)
            locationValue.setText(location)
            notesValue.setText(remarks)
            firstNameValue.setText(firstName)
            lastNameValue.setText(lastName)
            genderValue.setSelection(getSpinnerSelection(genderList, gender))
            raceValue.setSelection(getSpinnerSelection(raceList, race))
            driverLicenseValue.setText(driverLicense)
            licensePlateValue.setText(licensePlate)
        }

        (videoMetadata.photos as MutableList<PhotoAssociated>?)?.let {
            SnapshotsAssociatedByUser.temporalAssociateSnapshot = it
            associateSnapshotsFragment.replaceSnapshotsAssociatedFromMetadata(it)
        }

        showSnapshotsAssociated()
        hideLoadingDialog()
    }

    private fun getSpinnerSelection(list: List<String>, value: String?): Int {
        return if (value == null || value.isEmpty()) 0
        else list.indexOfFirst { it == value }
    }

    private fun verifyIfSelectedVideoWasChanged() {
        val videoWasChanged = getCameraConnectFileFromIntent() != connectVideo
        if (videoWasChanged) {
            restartObjectOfCompanion()
        }
    }

    private fun handleAssociateSnapshots() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        associateSnapshotsFragment.replaceSnapshotsAssociatedFromMetadata(SnapshotsAssociatedByUser.temporalAssociateSnapshot)
        showSnapshotsAssociated()
        layoutVideoPlayback.showSuccessSnackBar(getString(R.string.snapshots_added_success))
    }

    private fun showSnapshotsAssociated() {
        layoutAssociatedSnapshots?.removeAllViews()
        layoutAssociatedSnapshots?.isVisible = !SnapshotsAssociatedByUser.value.isNullOrEmpty()
        SnapshotsAssociatedByUser.value.forEach {
            layoutAssociatedSnapshots?.childCount?.let { position ->
                createTagInPosition(position, it.date)
            }
        }
    }

    private fun createTagInPosition(position: Int, text: String) {
        layoutAssociatedSnapshots?.addView(
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
        if (index >= 0){
            SnapshotsAssociatedByUser.value.removeAt(index)
            associateSnapshotsFragment.replaceSnapshotsAssociatedFromMetadata(SnapshotsAssociatedByUser.value)
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
        if (eventValue.selectedItem == eventList[0]) {
            layoutVideoPlayback.showErrorSnackBar(getString(R.string.event_mandatory))
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
        connectVideo = getCameraConnectFileFromIntent()
        connectVideo?.run {
            videoPlaybackViewModel.getInformationOfVideo(this)
        }
    }

    private fun getVideoMetadata() {
        connectVideo?.run {
            videoPlaybackViewModel.getVideoMetadata(name, nameFolder)
        }
    }

    private fun getCameraConnectFileFromIntent() =
        intent?.getSerializableExtra(CAMERA_CONNECT_FILE) as CameraConnectFile


    private fun isAllowedToAttemptToGetInformation() = currentAttempts <= ATTEMPTS_ALLOWED

    private fun setVideoInformation() {
        videoNameValue.text = connectVideo?.name
        startTimeValue.text = connectVideo?.getCreationDate()
        durationValue.text = totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
    }

    private fun createVideoPlaybackInSurface(domainInformationVideo: DomainInformationVideo) {
        videoPlaybackViewModel.createVLCMediaPlayer(
            domainInformationVideo.urlVideo,
            surfaceVideoPlayback
        )
    }

    private fun playVideoPlayback() {
        buttonPlay.setImageResource(R.drawable.ic_media_pause)
        updateLiveOrPlaybackActive(true)
        videoPlaybackViewModel.playMediaPlayer()
        setProgressToVideo(currentProgressInVideo)
        buttonAspect.isClickable = true
    }

    private fun pauseVideoPlayback() {
        buttonPlay.setImageResource(R.drawable.ic_media_play)
        updateLiveOrPlaybackActive(false)
        videoPlaybackViewModel.pauseMediaPlayer()
        buttonAspect.isClickable = false
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
                if (!areLinkedSnapshotsChangesSaved || verifyVideoMetadataWasEdited())
                    this.createAlertDialogMetadataExit()
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
            verifyVideoMetadataIsNullOrEmpty()

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
                            fromJson.race != fromForm.race
                } ?: false
            }
        }
        return false
    }

    private fun getNewMetadataFromForm(): CameraConnectVideoMetadata {

        var gender: String? = null
        var race: String? = null
        val event =
            if (eventValue.selectedItemPosition != 0)
                CameraInfo.events[eventValue.selectedItemPosition - 1]
            else null

        if (genderValue.selectedItem != genderList[0]) {
            gender = genderValue.selectedItem.toString()
        }

        if (raceValue.selectedItem != raceList[0]) {
            race = raceValue.selectedItem.toString()
        }

        return CameraConnectVideoMetadata(
            videoNameValue.text.toString(),
            CameraInfo.officerId,
            connectVideo?.path,
            connectVideo?.nameFolder,
            CameraInfo.serialNumber,
            VideoMetadata(
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
            SnapshotsAssociatedByUser.value
        )
    }

    private fun verifyVideoMetadataIsNullOrEmpty() {
        currentMetadata.metadata?.apply {
            if (partnerID.isNullOrEmpty()) partnerID = ""
            if (ticketNumber.isNullOrEmpty()) ticketNumber = ""
            if (ticketNumber2.isNullOrEmpty()) ticketNumber2 = ""
            if (caseNumber.isNullOrEmpty()) caseNumber = ""
            if (caseNumber2.isNullOrEmpty()) caseNumber2 = ""
            if (dispatchNumber.isNullOrEmpty()) dispatchNumber = ""
            if (dispatchNumber2.isNullOrEmpty()) dispatchNumber2 = ""
            if (location.isNullOrEmpty()) location = ""
            if (remarks.isNullOrEmpty()) remarks = ""
            if (firstName.isNullOrEmpty()) firstName = ""
            if (lastName.isNullOrEmpty()) lastName = ""
            if (driverLicense.isNullOrEmpty()) driverLicense = ""
            if (licensePlate.isNullOrEmpty()) licensePlate = ""
        }
    }

    private fun updateProgressVideoInView() {
        val progressVideo =
            ((currentTimeVideoInMilliSeconds.toDouble() / totalDurationVideoInMilliSeconds.toDouble()) * 100).toInt()
        seekProgressVideo.progress = progressVideo
        val elapsedTime = currentTimeVideoInMilliSeconds.convertMilliSecondsToString()
        updateTextElapsedTimeAndLeftTime(elapsedTime)
    }

    private fun updateTextElapsedTimeAndLeftTime(elapsedTime: String) {
        runOnUiThread {
            textViewPlayerTime.text = elapsedTime
            textViewPlayerDuration.text =
                totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
        }
    }

    private fun configureListenerSeekBar() {
        var isFromUser = false
        seekProgressVideo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

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
        seekProgressVideo.progress = progress
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
        private var connectVideo: CameraConnectFile? = null
        private var domainInformationVideo: DomainInformationVideo? = null
        private var totalDurationVideoInMilliSeconds: Long = 0
        private var currentTimeVideoInMilliSeconds: Long = 0
        private var currentProgressInVideo = 0
        const val ATTEMPTS_ALLOWED = 2
    }
}
