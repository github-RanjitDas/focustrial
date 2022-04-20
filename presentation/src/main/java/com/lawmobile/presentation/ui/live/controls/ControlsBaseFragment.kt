package com.lawmobile.presentation.ui.live.controls

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.fragmentCollect
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.widgets.CustomAudioButton
import com.lawmobile.presentation.widgets.CustomRecordButton
import com.lawmobile.presentation.widgets.CustomSnapshotButton
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.animations.Animations
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetSwitch

abstract class ControlsBaseFragment : BaseFragment() {
    private val sharedViewModel: ControlsBaseViewModel by activityViewModels()

    var onLiveStreamSwitchClick: ((Boolean) -> Unit)? = null
    var onCameraOperation: ((String) -> Unit)? = null
    var onCameraOperationFinished: ((Boolean) -> Unit)? = null

    lateinit var buttonTakeSnapshot: CustomSnapshotButton
    lateinit var buttonRecordVideo: CustomRecordButton
    lateinit var buttonResetViewFinder: Button
    var buttonRecordAudio: CustomAudioButton? = null
    lateinit var buttonSwitchLiveView: SafeFleetSwitch
    lateinit var parentLayout: ConstraintLayout
    lateinit var viewDisableButtons: View
    lateinit var imageRecordingIndicator: ImageView
    lateinit var textLiveViewRecording: TextView

    private fun disableButtons() {
        viewDisableButtons.isVisible = true
    }

    suspend fun checkCameraIsRecordingVideo() {
        sharedViewModel.checkCameraIsRecordingVideo()
    }

    fun setLiveViewSwitchState(isActive: Boolean = true) {
        buttonSwitchLiveView.isActivated = isActive
    }

    fun setSharedObservers() {
        with(sharedViewModel) {
            resultRecordVideoLiveData.observe(viewLifecycleOwner, ::manageResultInRecordingVideo)
            resultStopVideoLiveData.observe(viewLifecycleOwner, ::manageResultInRecordingVideo)
            resultRecordAudioLiveData.observe(viewLifecycleOwner, ::manageResultInRecordingAudio)
            resultStopAudioLiveData.observe(viewLifecycleOwner, ::manageResultInRecordingAudio)
            resultTakePhotoLiveData.observe(viewLifecycleOwner, ::manageResultTakePhoto)
            fragmentCollect(isCameraRecordingVideo, ::updateVideoRecordingStatus)
        }
    }

    private fun updateVideoRecordingStatus(isRecording: Boolean) {
        BaseActivity.isRecordingVideo = isRecording
        buttonRecordVideo.isActivated = isRecording
        if (isRecording) textLiveViewRecording.text = getString(R.string.video_recording)
        showRecordingIndicator(isRecording)
    }

    fun setSharedListeners() {

        buttonResetViewFinder.setOnClickListener {
            CameraHelper.getInstance().resetViewFinder()
        }

        buttonTakeSnapshot.setClickListenerCheckConnection {
            disableButtons()
            onCameraOperation?.invoke(getString(R.string.taking_snapshot))
            onLiveStreamSwitchClick?.invoke(true)
            sharedViewModel.takePhoto()
            setLiveViewSwitchState()
        }

        buttonRecordVideo.setClickListenerCheckConnection {
            if (!BaseActivity.isRecordingVideo) {
                disableButtons()
                onCameraOperation?.invoke(getString(R.string.starting_recording))
            } else {
                disableButtons()
                onCameraOperation?.invoke(getString(R.string.stopping_recording))
            }
            setLiveViewSwitchState()
            onLiveStreamSwitchClick?.invoke(true)
            manageRecordingVideo()
        }

        buttonRecordAudio?.setClickListenerCheckConnection {
            if (!BaseActivity.isRecordingAudio) {
                disableButtons()
                onCameraOperation?.invoke(getString(R.string.starting_recording))
            } else {
                disableButtons()
                onCameraOperation?.invoke(getString(R.string.stopping_recording))
            }
            manageRecordingAudio()
        }

        buttonSwitchLiveView.setClickListenerCheckConnection {
            onLiveStreamSwitchClick?.invoke(buttonSwitchLiveView.isActivated)
        }

        BaseActivity.onVideoRecordingStatus = ::updateVideoRecordingStatus
    }

    private fun manageRecordingVideo() {
        buttonRecordAudio?.setEnabledState(BaseActivity.isRecordingVideo)
        if (BaseActivity.isRecordingVideo) {
            sharedViewModel.stopRecordVideo()
        } else {
            sharedViewModel.startRecordVideo()
        }
    }

    private fun manageRecordingAudio() {
        buttonTakeSnapshot.setEnabledState(BaseActivity.isRecordingAudio)
        if (BaseActivity.isRecordingAudio) {
            sharedViewModel.stopRecordAudio()
            return
        }
        sharedViewModel.startRecordAudio()
    }

    private fun manageResultInRecordingVideo(result: Event<Result<Unit>>) {
        hideLoading()
        updateVideoRecordingStatus(!BaseActivity.isRecordingVideo)
        result.getContentIfNotHandled()?.run {
            doIfError {
                buttonRecordAudio?.setEnabledState(true)
                parentLayout.showErrorSnackBar(getString(R.string.error_saving_video))
            }
        }
    }

    private fun manageResultInRecordingAudio(result: Event<Result<Unit>>) {
        BaseActivity.isRecordingAudio = !BaseActivity.isRecordingAudio
        buttonRecordAudio?.isActivated = BaseActivity.isRecordingAudio
        textLiveViewRecording.text = getString(R.string.audio_recording)
        hideLoading(BaseActivity.isRecordingAudio)
        setLiveViewSwitchState(!BaseActivity.isRecordingAudio)
        showRecordingIndicator(BaseActivity.isRecordingAudio)
        result.getContentIfNotHandled()?.run {
            doIfError {
                parentLayout.showErrorSnackBar(getString(R.string.error_saving_audio))
            }
        }
    }

    private fun showRecordingIndicator(show: Boolean) {
        imageRecordingIndicator.isVisible = show
        textLiveViewRecording.isVisible = show
        if (show) {
            val animation = Animations.createBlinkAnimation(BLINK_ANIMATION_DURATION)
            imageRecordingIndicator.startAnimationIfEnabled(animation)
        } else {
            imageRecordingIndicator.clearAnimation()
        }
    }

    private fun hideLoading(isAudio: Boolean = false) {
        viewDisableButtons.isVisible = false
        onCameraOperationFinished?.invoke(isAudio)
    }

    private fun manageResultTakePhoto(result: Event<Result<Unit>>) {
        hideLoading()
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                sharedViewModel.playSoundTakePhoto()
                parentLayout.showSuccessSnackBar(getString(R.string.live_view_take_photo_success))
            }
            doIfError {
                parentLayout.showErrorSnackBar(getString(R.string.live_view_take_photo_failed))
            }
        }
    }

    companion object {
        private const val BLINK_ANIMATION_DURATION = 1000L
    }
}
