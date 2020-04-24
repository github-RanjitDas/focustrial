package com.lawmobile.presentation.ui.live

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.activity_live_view.*
import javax.inject.Inject

class LiveActivity : BaseActivity() {

    @Inject
    lateinit var liveActivityViewModel: LiveActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_view)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        setUrlLive()
        startLiveVideoView()
        setButtons()
        observeResult()
    }

    private fun setButtons() {
        buttonSnapshot.setOnClickListener { takePhoto() }
        toggleFullScreenLiveView.setOnClickListener {
            requestedOrientation = if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else{
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
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

    private fun observeResult() {
        liveActivityViewModel.resultTakePhotoLiveData.observe(this, Observer {
            if (it is Result.Success) {
                liveActivityViewModel.playSoundTakePhoto()
                this.showToast(getString(R.string.live_view_take_photo_success), Toast.LENGTH_LONG)
            }
            else {
                this.showToast(getString(R.string.live_view_take_photo_failed), Toast.LENGTH_LONG)
            }
        })
    }
}