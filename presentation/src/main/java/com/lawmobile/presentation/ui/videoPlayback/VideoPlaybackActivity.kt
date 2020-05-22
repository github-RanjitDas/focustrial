package com.lawmobile.presentation.ui.videoPlayback

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CatalogTypes
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.hideKeyboard
import kotlinx.android.synthetic.main.activity_video_playback.*
import javax.inject.Inject

class VideoPlaybackActivity : BaseActivity() {

    @Inject
    lateinit var videoPlaybackViewModel: VideoPlaybackViewModel

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
        domainInformationVideo?.let {
            createVideoPlaybackInSurface(it)
            playVideoPlayback()
            setProgressToVideo(currentProgressInVideo)
            updateCurrentTimeInVideo()
            configureObserveCurrentTimeVideo()
            setMetadata()
        } ?: run {
            createDialog()
            getInformationOfVideo()
        }
        configureListeners()
        getCatalogInfo()
    }

    private fun getCatalogInfo() {
        videoPlaybackViewModel.getCatalogInfo()
        videoPlaybackViewModel.catalogInfoLiveData.observe(this, Observer(::setCatalogLists))
    }

    private fun setCatalogLists(catalogInfoList: Result<List<CameraConnectCatalog>>) {
        val eventList = mutableListOf<String>()
        val raceList = mutableListOf<String>()
        val genderList = mutableListOf<String>()

        eventList.add(getString(R.string.select))

        when (catalogInfoList) {
            is Result.Success -> {
                val eventNames =
                    catalogInfoList.data.filter { it.type == CatalogTypes.EVENT.value }
                        .map { it.name }
                eventList.addAll(eventNames)
            }
            is Result.Error -> {
                this.showToast(getString(R.string.catalog_error), Toast.LENGTH_SHORT)
            }
        }

        raceList.addAll(resources.getStringArray(R.array.race_spinner))
        genderList.addAll(resources.getStringArray(R.array.gender_spinner))

        eventValue.adapter = ArrayAdapter(this, R.layout.spinner_item, eventList)
        raceValue.adapter = ArrayAdapter(this, R.layout.spinner_item, raceList)
        genderValue.adapter = ArrayAdapter(this, R.layout.spinner_item, genderList)
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
        cancelButtonVideoPlayback.setOnClickListenerCheckConnection {
            onBackPressed()
        }
        configureListenerSeekBar()
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
        videoPlaybackViewModel.domainInformationVideoLiveData.observe(this, Observer {
            manageResultObserverGetVideoInformation(it)
        })
    }

    private fun getCameraConnectFileFromIntent(): CameraConnectFile {
        return intent?.getSerializableExtra(CAMERA_CONNECT_FILE) as CameraConnectFile
    }

    private fun manageResultObserverGetVideoInformation(result: Result<DomainInformationVideo>) {
        dialog.hide()
        when (result) {
            is Result.Success -> {
                totalDurationVideoInMilliSeconds = result.data.duration.toLong() * 1000
                domainInformationVideo = result.data
                createVideoPlaybackInSurface(result.data)
                playVideoPlayback()
                updateCurrentTimeInVideo()
                configureObserveCurrentTimeVideo()
                setMetadata()
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

    private fun isAllowedToAttemptToGetInformation() = currentAttempts <= ATTEMPTS_ALLOWED

    private fun setMetadata() {
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
        videoPlaybackViewModel.playVLCMediaPlayer()
    }

    private fun pauseVideoPlayback() {
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
