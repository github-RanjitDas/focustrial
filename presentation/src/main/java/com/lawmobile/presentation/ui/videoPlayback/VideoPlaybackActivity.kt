package com.lawmobile.presentation.ui.videoPlayback

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.linkSnapshotsToVideo.LinkSnapshotsActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.lawmobile.presentation.utils.Constants.SNAPSHOTS_DATE_SELECTED
import com.lawmobile.presentation.utils.Constants.SNAPSHOTS_LINKED
import com.lawmobile.presentation.utils.Constants.SNAPSHOTS_SELECTED
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.hideKeyboard
import kotlinx.android.synthetic.main.activity_video_playback.*
import javax.inject.Inject

class VideoPlaybackActivity : BaseActivity() {

    @Inject
    lateinit var videoPlaybackViewModel: VideoPlaybackViewModel

    private val eventList = mutableListOf<String>()
    private val raceList = mutableListOf<String>()
    private val genderList = mutableListOf<String>()
    private var linkedPhotoList: ArrayList<PhotoAssociated>? = ArrayList()
    private var linkedPhotoDateList: ArrayList<String>? = ArrayList()

    private lateinit var dialog: AlertDialog
    private var currentAttempts = 0
    private var areLinkedSnapshotsChangesSaved = true
    private var isVideoMetadataChangesSaved = false
    private lateinit var currentMetadata: CameraConnectVideoMetadata

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback)
        createDialog()
        setCatalogLists()
        setObservers()
        configureListeners()
    }

    override fun onResume() {
        super.onResume()

        hideKeyboard()
        verifyIfSelectedVideoWasChanged()

        domainInformationVideo?.let {
            createVideoPlaybackInSurface(it)
            playVideoPlayback()
            setProgressToVideo(currentProgressInVideo)
            updateCurrentTimeInVideo()
            configureObserveCurrentTimeVideo()
            setVideoInformation()
        } ?: run {
            getInformationOfVideo()
        }

        getVideoMetadata()
    }

    override fun onPause() {
        super.onPause()
        if (videoPlaybackViewModel.isMediaPlayerPlaying())
            manageButtonPlayPause()
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

    private fun setObservers() {
        videoPlaybackViewModel.domainInformationVideoLiveData.observe(
            this,
            Observer(::manageGetVideoInformationResult)
        )
        videoPlaybackViewModel.saveVideoMetadataLiveData.observe(
            this,
            Observer(::manageSaveVideoResult)
        )
        videoPlaybackViewModel.videoMetadataLiveData.observe(
            this,
            Observer(::manageGetVideoMetadataResult)
        )
    }

    private fun manageSaveVideoResult(result: Result<Unit>) {
        when (result) {
            is Result.Success -> this.showToast(
                getString(R.string.video_metadata_saved_success),
                Toast.LENGTH_SHORT
            )
            is Result.Error -> this.showToast(
                getString(R.string.video_metadata_save_error),
                Toast.LENGTH_SHORT
            )
        }
        dialog.dismiss()
        areLinkedSnapshotsChangesSaved = true
    }

    private fun manageGetVideoMetadataResult(result: Result<CameraConnectVideoMetadata>) {
        when (result) {
            is Result.Success -> {
                currentMetadata = result.data
                setVideoMetadata(result.data)
            }
            is Result.Error -> this.showToast(
                getString(R.string.get_video_metadata_error),
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun manageGetVideoInformationResult(result: Result<DomainInformationVideo>) {
        when (result) {
            is Result.Success -> {
                totalDurationVideoInMilliSeconds = result.data.duration.toLong() * 1000
                domainInformationVideo = result.data
                createVideoPlaybackInSurface(result.data)
                playVideoPlayback()
                updateCurrentTimeInVideo()
                configureObserveCurrentTimeVideo()
                setVideoInformation()
            }
            is Result.Error -> {
                if (isAllowedToAttemptToGetInformation()) {
                    currentAttempts += 1
                    connectVideo?.let { videoPlaybackViewModel.getInformationResourcesVideo(it) }
                    return
                }

                val messageToast = result.exception.message ?: ERROR_IN_GET_INFORMATION_OF_VIDEO
                this.showToast(messageToast, Toast.LENGTH_SHORT)
                finish()
            }
        }
    }

    private fun setVideoMetadata(cameraConnectVideoMetadata: CameraConnectVideoMetadata) {
        cameraConnectVideoMetadata.metadata?.run {
            eventValue.setSelection(getSpinnerSelection(eventList, event?.name))
            partnerIdValue.setText(partnerID)
            ticket1Value.setText(ticketNumber)
            ticket2Value.setText(ticketNumber2)
            case1Value.setText(caseNumber)
            case2Value.setText(caseNumber2)
            dispatch1Value.setText(dispatchNumber)
            dispatch2Value.setText(dispatchNumber2)
            locationValue.setText(location)
            remarksValue.setText(remarks)
            firstNameValue.setText(firstName)
            lastNameValue.setText(lastName)
            genderValue.setSelection(getSpinnerSelection(genderList, gender))
            raceValue.setSelection(getSpinnerSelection(raceList, race))
            driverLicenseValue.setText(driverLicense)
            licensePlateValue.setText(licensePlate)
        }
        if (linkedPhotoList.isNullOrEmpty()) {
            linkedPhotoList =
                cameraConnectVideoMetadata.photos as ArrayList<PhotoAssociated>?
            linkedPhotoDateList =
                cameraConnectVideoMetadata.photos?.map { it.date } as ArrayList<String>?

        }
        updateLinkedPhotosField()
        dialog.dismiss()
    }

    private fun updateLinkedPhotosField() {
        linkedPhotosValue.text = ""
        linkedPhotoDateList?.forEach {
            linkedPhotosValue.append(it)
            linkedPhotosValue.append("\n")
        }
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

    private fun createDialog() {
        dialog = this.createAlertProgress()
    }

    private fun configureListeners() {
        buttonPlay.setOnClickListenerCheckConnection {
            manageButtonPlayPause()
        }
        buttonFullScreen.setOnClickListenerCheckConnection {
            changeOrientationScreen()
        }
        buttonAspect.setOnClickListenerCheckConnection {
            videoPlaybackViewModel.changeAspectRatio()
        }
        saveButtonVideoPlayback.setOnClickListenerCheckConnection {
            saveVideoMetadataInCamera()
        }
        cancelButtonVideoPlayback.setOnClickListenerCheckConnection {
            onBackPressed()
        }
        buttonLinkSnapshots.setOnClickListenerCheckConnection {
            val intent = Intent(this, LinkSnapshotsActivity::class.java)
            linkedPhotoList?.run {
                intent.putStringArrayListExtra(
                    SNAPSHOTS_LINKED,
                    map { it.name } as ArrayList<String>)
                intent.putStringArrayListExtra(
                    SNAPSHOTS_DATE_SELECTED,
                    map { it.date } as ArrayList<String>)
            }
            startActivityForResult(intent, 1)
        }
        configureListenerSeekBar()
    }

    private fun saveVideoMetadataInCamera() {
        if (eventValue.selectedItem == eventList[0]) {
            this.showToast(getString(R.string.event_mandatory), Toast.LENGTH_SHORT)
            return
        }
        dialog.show()
        videoPlaybackViewModel.saveVideoMetadata(getNewMetadataFromForm())
        isVideoMetadataChangesSaved = true
    }

    private fun manageButtonPlayPause() {
        if (videoPlaybackViewModel.isMediaPlayerPlaying()) {
            buttonPlay.setImageResource(R.drawable.ic_media_play)
            pauseVideoPlayback()
        } else {
            buttonPlay.setImageResource(R.drawable.ic_media_pause)
            playVideoPlayback()
        }
    }

    private fun getInformationOfVideo() {
        dialog.show()
        connectVideo = getCameraConnectFileFromIntent()
        connectVideo?.run {
            videoPlaybackViewModel.getInformationResourcesVideo(this)
        }
    }

    private fun getVideoMetadata() {
        connectVideo?.run {
            videoPlaybackViewModel.getVideoMetadata(name, nameFolder)
        }
    }

    private fun getCameraConnectFileFromIntent(): CameraConnectFile {
        return intent?.getSerializableExtra(CAMERA_CONNECT_FILE) as CameraConnectFile
    }

    private fun isAllowedToAttemptToGetInformation() = currentAttempts <= ATTEMPTS_ALLOWED

    private fun setVideoInformation() {
        videoNameValue.text = connectVideo?.name
        startTimeValue.text = connectVideo?.getVideoStartTime()
        durationValue.text = totalDurationVideoInMilliSeconds.convertMilliSecondsToString()
    }

    private fun createVideoPlaybackInSurface(domainInformationVideo: DomainInformationVideo) {
        videoPlaybackViewModel.createVLCMediaPlayer(
            domainInformationVideo.urlVideo,
            surfaceVideoPlayback
        )
    }

    private fun playVideoPlayback() {
        updateLiveOrPlaybackActive(true)
        videoPlaybackViewModel.playVLCMediaPlayer()
    }

    private fun pauseVideoPlayback() {
        updateLiveOrPlaybackActive(false)
        videoPlaybackViewModel.pauseMediaPlayer()
    }

    private fun changeOrientationScreen() {
        requestedOrientation =
            if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
    }

    override fun onBackPressed() {
        if (areLinkedSnapshotsChangesSaved && !verifyVideoMetadataWasEdited()) {
            videoPlaybackViewModel.stopMediaPlayer()
            super.onBackPressed()
        } else {
            val alertInformation = AlertInformation(
                R.string.metadata_confirmation,
                R.string.metadata_confirmation_message,
                ::closeWithoutSave,
                {}, null
            )
            this.createAlertInformation(alertInformation)
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
                remarks = remarksValue.text.toString(),
                firstName = firstNameValue.text.toString(),
                lastName = lastNameValue.text.toString(),
                gender = gender,
                race = race,
                driverLicense = driverLicenseValue.text.toString(),
                licensePlate = licensePlateValue.text.toString()
            ),
            linkedPhotoList
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

    private fun closeWithoutSave(dialogInterface: DialogInterface) {
        dialogInterface.dismiss()
        finish()
    }

    private fun updateCurrentTimeInVideo() {
        videoPlaybackViewModel.getTimeInMillisMediaPlayer()
    }

    private fun configureObserveCurrentTimeVideo() {
        videoPlaybackViewModel.currentTimeVideo.observe(this, Observer {
            updateCurrentTimeInVideo()
            currentTimeVideoInMilliSeconds = it
            updateProgressVideoInView()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                this.showToast(getString(R.string.image_linked_success), Toast.LENGTH_SHORT)
                handleLinkedSnapshotsResult(data)
            }
        }
    }

    private fun handleLinkedSnapshotsResult(data: Intent?) {
        val snapshotsNameList = data?.getStringArrayListExtra(SNAPSHOTS_SELECTED)
        val tmpList: ArrayList<PhotoAssociated>? = ArrayList()
        linkedPhotoDateList = data?.getStringArrayListExtra(SNAPSHOTS_DATE_SELECTED)
        snapshotsNameList?.forEachIndexed { index, fileName ->
            tmpList?.add(
                PhotoAssociated(
                    fileName,
                    linkedPhotoDateList?.get(index) ?: ""
                )
            )
        }
        linkedPhotoList = tmpList
        areLinkedSnapshotsChangesSaved = false
        updateLinkedPhotosField()
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
        Thread.sleep(500)
        videoPlaybackViewModel.setProgressMediaPlayer(progress.toFloat())
        seekProgressVideo.progress = progress
        updateCurrentTimeInVideo()
    }

    private fun restartObjectOfCompanion() {
        domainInformationVideo = null
        totalDurationVideoInMilliSeconds = 0
        currentTimeVideoInMilliSeconds = 0
        currentProgressInVideo = 0
    }

    companion object {
        private var connectVideo: CameraConnectFile? = null
        private var domainInformationVideo: DomainInformationVideo? = null
        private var totalDurationVideoInMilliSeconds: Long = 0
        private var currentTimeVideoInMilliSeconds: Long = 0
        private var currentProgressInVideo = 0
        const val ERROR_IN_GET_INFORMATION_OF_VIDEO = "Error in get information of video"
        const val ATTEMPTS_ALLOWED = 2
    }
}
