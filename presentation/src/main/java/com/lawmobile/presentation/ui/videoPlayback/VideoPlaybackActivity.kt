package com.lawmobile.presentation.ui.videoPlayback

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.lawmobile.domain.CameraInfo
import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
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

    private lateinit var dialog: AlertDialog
    private var currentAttempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback)
    }

    override fun onResume() {
        super.onResume()

        hideKeyboard()
        verifyIfSelectedVideoWasChanged()
        setCatalogLists()
        setObservers()
        createDialog()

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

        cameraConnectVideoMetadata?.run {
            setVideoMetadata()
        } ?: run {
            getVideoMetadata()
        }

        configureListeners()
    }

    private fun setCatalogLists() {
        eventList.add(getString(R.string.select))
        eventList.addAll(CameraInfo.events)
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
        cameraConnectVideoMetadata = null
    }

    private fun manageGetVideoMetadataResult(result: Result<CameraConnectVideoMetadata>) {
        when (result) {
            is Result.Success -> {
                cameraConnectVideoMetadata = result.data
                setVideoMetadata()
            }
            is Result.Error -> this.showToast(
                getString(R.string.get_video_metadata_error),
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun manageGetVideoInformationResult(result: Result<DomainInformationVideo>) {
        dialog.dismiss()
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

    private fun setVideoMetadata() {
        cameraConnectVideoMetadata?.metadata?.run {
            eventValue.setSelection(getSpinnerSelection(eventList, event))
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
        configureListenerSeekBar()
    }

    private fun saveVideoMetadataInCamera() {
        if (eventValue.selectedItem == eventList[0]) {
            this.showToast(getString(R.string.event_mandatory), Toast.LENGTH_SHORT)
            return
        }

        var gender = ""
        var race = ""

        if (genderValue.selectedItem != genderList[0]) {
            gender = genderValue.selectedItem.toString()
        }

        if (raceValue.selectedItem != raceList[0]) {
            race = raceValue.selectedItem.toString()
        }

        val cameraConnectVideoMetadata = CameraConnectVideoMetadata(
            videoNameValue.text.toString(),
            CameraInfo.officerId,
            connectVideo?.path,
            connectVideo?.nameFolder,
            CameraInfo.cameraSerialNumber,
            VideoMetadata(
                event = eventValue.selectedItem.toString(),
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
            )
        )

        videoPlaybackViewModel.saveVideoMetadata(cameraConnectVideoMetadata)
    }

    private fun manageButtonPlayPause() {
        if (videoPlaybackViewModel.isMediaPlayerPaying()) {
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
        videoPlaybackViewModel.getInformationResourcesVideo(connectVideo!!)
    }

    private fun getVideoMetadata() {
        videoPlaybackViewModel.getVideoMetadata(connectVideo!!.name)
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
        super.onBackPressed()
        videoPlaybackViewModel.stopMediaPlayer()
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
        cameraConnectVideoMetadata = null
        totalDurationVideoInMilliSeconds = 0
        currentTimeVideoInMilliSeconds = 0
        currentProgressInVideo = 0
    }

    companion object {
        private var connectVideo: CameraConnectFile? = null
        private var domainInformationVideo: DomainInformationVideo? = null
        private var cameraConnectVideoMetadata: CameraConnectVideoMetadata? = null
        private var totalDurationVideoInMilliSeconds: Long = 0
        private var currentTimeVideoInMilliSeconds: Long = 0
        private var currentProgressInVideo = 0
        const val ERROR_IN_GET_INFORMATION_OF_VIDEO = "Error in get information of video"
        const val ATTEMPTS_ALLOWED = 2
    }
}
