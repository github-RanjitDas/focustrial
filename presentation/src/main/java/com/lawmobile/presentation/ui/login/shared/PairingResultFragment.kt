package com.lawmobile.presentation.ui.login.shared

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentPairingResultBinding
import com.lawmobile.presentation.extensions.runWithDelay
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.helpers.Result

class PairingResultFragment : BaseFragment(), LoginBaseActivity.OnHandleConnectionAnimation {

    private var _binding: FragmentPairingResultBinding? = null
    private val binding get() = _binding!!
    private var mLastClickTime: Long = 0

    private val pairingViewModel: PairingViewModel by activityViewModels()
    lateinit var onConnectionSuccessful: () -> Unit
    lateinit var onHandleConnectionAnimation: (LoginBaseActivity.OnHandleConnectionAnimation) -> Unit
    private lateinit var animation: Animation
    private var isOnlyShowSuccessAnimation: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPairingResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
        setAnimation()
        if (CameraInfo.isBackOfficeCC()) {
            onHandleConnectionAnimation.invoke(this)
        }
        println("CC_PWD_VALIDATION PairingResultFragment:$isOnlyShowSuccessAnimation")
        pairingViewModel.connectWithCamera()
    }

    private fun setAnimation() {
        animation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom_anim).apply {
            startOffset = 0
        }
    }

    private fun setListeners() {
        binding.buttonRetry.setOnClickListener {
            pairingViewModel.resetProgress()
            startConnectionToHotspotCamera()
        }
    }

    private fun showLoadingProgress() {
        binding.pairingProgressLayout.isVisible = true
        binding.pairingResultLayout.isVisible = false
        binding.buttonRetry.isVisible = false
    }

    private fun startConnectionToHotspotCamera() {
        showLoadingProgress()
        verifyConnectionWithTheCamera()
    }

    private fun saveSerialNumber() {
        val serialNumberCamera = pairingViewModel.getNetworkName()
        CameraInfo.serialNumber = serialNumberCamera.replace("X", "")
    }

    private fun verifyConnectionWithTheCamera() {
        activity?.runOnUiThread { pairingViewModel.connectWithCamera() }
    }

    private fun setProgressInViewOfProgress(progress: Int) {
        val percent = "$progress%"
        _binding?.textViewProgressConnection?.text = percent
        if (progress == PERCENT_TOTAL_CONNECTION_CAMERA) {
            saveSerialNumber()
            if (CameraInfo.isBackOfficeCC()) {
                onConnectionSuccessful()
            } else {
                showSuccessResult()
            }
        }
    }

    private fun setObservers() {
        pairingViewModel.connectionProgress.observe(viewLifecycleOwner, ::handleConnectionProgress)
    }

    private fun handleConnectionProgress(result: Result<Int>) {
        when (result) {
            is Result.Success -> setProgressInViewOfProgress(result.data)
            is Result.Error -> {
                result.doIfError {
                    SFConsoleLogs.log(
                        SFConsoleLogs.Level.ERROR,
                        SFConsoleLogs.Tags.TAG_HOTSPOT_CONNECTION_ERRORS,
                        it,
                        "Error in pairing with camera"
                    )
                }
                showErrorResult()
            }
        }
    }

    private fun showSuccessResult() {
        println("CC_PWD_VALIDATION showSuccessResult:")
        binding.pairingProgressLayout.isVisible = false
        binding.pairingResultLayout.isVisible = true
        binding.buttonRetry.isVisible = false
        binding.imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ic_successful_green, null
            )
        )
        binding.textViewResultPairing.setText(R.string.success_connection_to_camera)
        binding.pairingResultLayout.startAnimation(animation)
        waitToFinishAnimation()
    }

    private fun waitToFinishAnimation() {
        println("CC_PWD_VALIDATION waitToFinishAnimation:$isOnlyShowSuccessAnimation")
        runWithDelay(ANIMATION_DURATION) {
            if (isOnlyShowSuccessAnimation && CameraInfo.isBackOfficeCC()) {
                startLiveViewActivity()
            } else {
                onConnectionSuccessful()
            }
        }
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = Intent(activity, LiveX2Activity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun showErrorResult() {
        binding.pairingProgressLayout.isVisible = false
        binding.pairingResultLayout.isVisible = true
        binding.buttonRetry.isVisible = true
        binding.imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ic_error_big, null
            )
        )
        binding.textViewResultPairing.setText(R.string.error_connection_to_camera)
        binding.pairingResultLayout.startAnimation(animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = PairingResultFragment::class.java.simpleName
        private const val PERCENT_TOTAL_CONNECTION_CAMERA = 100
        private const val ANIMATION_DURATION = 1200L

        fun createInstance(
            onConnectionSuccess: () -> Unit,
            handleAnimation: (LoginBaseActivity.OnHandleConnectionAnimation) -> Unit
        ): PairingResultFragment = PairingResultFragment().apply {
            this.onConnectionSuccessful = onConnectionSuccess
            this.onHandleConnectionAnimation = handleAnimation
        }
    }

    override fun onShowSuccessAnimation() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        isOnlyShowSuccessAnimation = true
        showSuccessResult()
    }
}
