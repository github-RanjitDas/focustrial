package com.lawmobile.presentation.ui.live

import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.setOnCheckedChangeListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CatalogTypes
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.activity_live_view.*
import javax.inject.Inject

class LiveActivity : BaseActivity() {

    @Inject
    lateinit var liveActivityViewModel: LiveActivityViewModel

    private lateinit var dialogProgressSnapShot: AlertDialog
    private lateinit var dialogProgressVideo: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_view)
        configureObservers()
        getCatalogInfo()
        dialogProgressSnapShot = createAlertProgress(R.string.taking_snapshot)
        dialogProgressVideo = createAlertProgress()
    }

    override fun onResume() {
        super.onResume()
        updateLiveOrPlaybackActive(buttonSwitchLiveView.isChecked)
        setUrlLive()
        startLiveVideoView()
        configureListeners()
    }

    private fun getCatalogInfo() {
        liveActivityViewModel.getCatalogInfo()
    }

    private fun configureListeners() {
        toggleFullScreenLiveView.setOnClickListenerCheckConnection {
            changeOrientationLive()
        }
        buttonSnapshot.setOnClickListenerCheckConnection {
            dialogProgressSnapShot.show()
            changeStatusSwitch(true)
            takePhoto()
        }
        buttonStreaming.setOnClickListenerCheckConnection {
            dialogProgressVideo.show()
            changeStatusSwitch(true)
            manageRecordingVideo()
        }
        buttonSwitchLiveView.setOnCheckedChangeListenerCheckConnection { _, isChecked ->
            changeStatusSwitch(isChecked)
        }

        buttonSnapshotList.setOnClickListenerCheckConnection {
            startFileListIntent(SNAPSHOT_LIST)
        }

        buttonVideoList.setOnClickListenerCheckConnection {
            startFileListIntent(VIDEO_LIST)
        }
    }

    private fun startFileListIntent(fileType: String) {
        updateLiveOrPlaybackActive(false)
        val fileListIntent = Intent(this, FileListActivity::class.java)
        fileListIntent.putExtra(FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
    }

    private fun changeStatusSwitch(isChecked: Boolean) {
        updateLiveOrPlaybackActive(isChecked)
        changeVisibilityView(isChecked)
        buttonSwitchLiveView.isChecked = isChecked
        toggleFullScreenLiveView.isClickable = isChecked
        if (isChecked) {
            buttonSwitchLiveView.setBackgroundResource(R.drawable.ic_switch_on)
            return
        }

        buttonSwitchLiveView.setBackgroundResource(R.drawable.ic_switch_off)
    }

    private fun changeVisibilityView(isVisible: Boolean) {
        if (isVisible) {
            liveStreamingView.setBackgroundResource(R.color.transparent)
            return
        }
        liveStreamingView.setBackgroundResource(R.color.black)
    }

    private fun configureObservers() {
        liveActivityViewModel.stopRecordVideo.observe(this, Observer {
            dialogProgressVideo.dismiss()
            manageResultInRecordingVideo(it)
        })

        liveActivityViewModel.startRecordVideo.observe(this, Observer {
            dialogProgressVideo.dismiss()
            manageResultInRecordingVideo(it)
        })

        liveActivityViewModel.resultTakePhotoLiveData.observe(this, Observer {
            dialogProgressSnapShot.dismiss()
            manageResultTakePhoto(it)
        })

        liveActivityViewModel.catalogInfoLiveData.observe(this, Observer(::setCatalogInfo))
    }

    private fun setCatalogInfo(catalogInfoList: Result<List<CameraConnectCatalog>>) {
        when (catalogInfoList) {
            is Result.Success -> {
                val eventNames =
                    catalogInfoList.data.filter { it.type == CatalogTypes.EVENT.value }
                CameraInfo.events.addAll(eventNames)
            }
            is Result.Error -> {
                this.showToast(getString(R.string.catalog_error), Toast.LENGTH_SHORT)
            }
        }
    }

    private fun manageResultTakePhoto(result: Result<Unit>) {
        if (result is Result.Success) {
            liveActivityViewModel.playSoundTakePhoto()
            this.showToast(getString(R.string.live_view_take_photo_success), Toast.LENGTH_LONG)
        } else {
            this.showToast(getString(R.string.live_view_take_photo_failed), Toast.LENGTH_LONG)
        }
    }

    private fun manageRecordingVideo() {
        if (isRecordingVideo) {
            liveActivityViewModel.stopRecordVideo()
            return
        }

        liveActivityViewModel.startRecordVideo()
    }

    private fun manageResultInRecordingVideo(result: Result<Unit>) {
        when (result) {
            is Result.Success -> {
                changeImageDependsRecordingVideo()
            }
            is Result.Error -> {
                Toast.makeText(this, result.exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeImageDependsRecordingVideo() {
        isRecordingVideo = !isRecordingVideo
        if (isRecordingVideo) {
            imageRecordingIndicator.visibility = View.VISIBLE
            buttonStreaming.setBackgroundResource(R.drawable.ic_record_active)
            return
        }

        imageRecordingIndicator.visibility = View.INVISIBLE
        buttonStreaming.setBackgroundResource(R.drawable.ic_record)
    }

    private fun changeOrientationLive() {
        requestedOrientation =
            if (resources.configuration.orientation == SCREEN_ORIENTATION_PORTRAIT) {
                SCREEN_ORIENTATION_LANDSCAPE
            } else {
                SCREEN_ORIENTATION_PORTRAIT
            }
    }

    override fun onStop() {
        super.onStop()
        liveActivityViewModel.stopVLCMediaPlayer()
    }

    private fun setUrlLive() {
        val url = liveActivityViewModel.getUrlLive()
        liveActivityViewModel.createVLCMediaPlayer(url, liveStreamingView)
    }

    private fun startLiveVideoView() {
        liveActivityViewModel.startVLCMediaPlayer()
    }

    private fun takePhoto() {
        liveActivityViewModel.takePhoto()
    }
}
