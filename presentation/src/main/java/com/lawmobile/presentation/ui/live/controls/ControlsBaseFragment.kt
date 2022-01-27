package com.lawmobile.presentation.ui.live.controls

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showSuccessSnackBar
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.widgets.CustomAudioButton
import com.lawmobile.presentation.widgets.CustomRecordButton
import com.lawmobile.presentation.widgets.CustomSnapshotButton
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.animations.Animations
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetSwitch

open class ControlsBaseFragment : BaseFragment() {
    private val sharedViewModel: ControlsBaseViewModel by activityViewModels()

    var onLiveStreamSwitchClick: ((Boolean) -> Unit)? = null
    var onCameraOperation: ((String) -> Unit)? = null
    var onCameraOperationFinished: ((Boolean) -> Unit)? = null

    lateinit var buttonSnapshot: CustomSnapshotButton
    lateinit var buttonRecord: CustomRecordButton
    var buttonAudio: CustomAudioButton ? = null
    lateinit var buttonSwitchLiveView: SafeFleetSwitch
    lateinit var parentLayout: ConstraintLayout
    lateinit var viewDisableButtons: View
    lateinit var imageRecordingIndicator: ImageView
    lateinit var textLiveViewRecording: TextView

    private fun disableButtons() {
        viewDisableButtons.isVisible = true
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
        }
    }

    fun setSharedListeners() {
        buttonSnapshot.setClickListenerCheckConnection {
            disableButtons()
            onCameraOperation?.invoke(getString(R.string.taking_snapshot))
            onLiveStreamSwitchClick?.invoke(true)
            sharedViewModel.takePhoto()
            setLiveViewSwitchState()
        }

        buttonRecord.setClickListenerCheckConnection {
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

        buttonAudio?.setClickListenerCheckConnection {
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
    }

    private fun manageRecordingVideo() {
        buttonAudio?.setEnabledState(BaseActivity.isRecordingVideo)
        if (BaseActivity.isRecordingVideo) {
            sharedViewModel.stopRecordVideo()
        } else {
            sharedViewModel.startRecordVideo()
        }
    }

    private fun manageRecordingAudio() {
        buttonSnapshot.setEnabledState(BaseActivity.isRecordingAudio)
        if (BaseActivity.isRecordingAudio) {
            sharedViewModel.stopRecordAudio()
            return
        }
        sharedViewModel.startRecordAudio()
    }

    private fun manageResultInRecordingVideo(result: Event<Result<Unit>>) {
        hideLoading()
        BaseActivity.isRecordingVideo = !BaseActivity.isRecordingVideo
        buttonRecord.isActivated = BaseActivity.isRecordingVideo
        textLiveViewRecording.text = getString(R.string.video_recording)
        showRecordingIndicator(BaseActivity.isRecordingVideo)
        result.getContentIfNotHandled()?.run {
            doIfError {
                buttonAudio?.setEnabledState(true)
                parentLayout.showErrorSnackBar(getString(R.string.error_saving_video))
            }
        }
    }

    private fun manageResultInRecordingAudio(result: Event<Result<Unit>>) {
        BaseActivity.isRecordingAudio = !BaseActivity.isRecordingAudio
        buttonAudio?.isActivated = BaseActivity.isRecordingAudio
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
