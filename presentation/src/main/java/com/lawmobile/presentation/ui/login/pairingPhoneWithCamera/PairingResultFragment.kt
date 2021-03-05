package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentPairingResultBinding
import com.lawmobile.presentation.ui.base.BaseFragment
import com.safefleet.mobile.kotlin_commons.helpers.Result

class PairingResultFragment : BaseFragment() {

    private var _fragmentPairingResultBinding: FragmentPairingResultBinding? = null
    private val fragmentPairingResultBinding get() = _fragmentPairingResultBinding!!

    private val pairingViewModel: PairingViewModel by viewModels()
    lateinit var connectionSuccess: (isSuccess: Boolean) -> Unit
    private lateinit var animation: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentPairingResultBinding =
            FragmentPairingResultBinding.inflate(inflater, container, false)
        return fragmentPairingResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairingViewModel.resetProgress()
        setObservers()
        setListeners()
        setAnimation()
        startConnectionToHotspotCamera()
    }

    private fun setAnimation() {
        animation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom_anim).apply {
            startOffset = 0
        }
    }

    private fun setListeners() {
        fragmentPairingResultBinding.buttonRetry.setOnClickListener {
            startConnectionToHotspotCamera()
        }
    }

    private fun showLoadingProgress() {
        fragmentPairingResultBinding.pairingProgressLayout.isVisible = true
        fragmentPairingResultBinding.pairingResultLayout.isVisible = false
        fragmentPairingResultBinding.buttonRetry.isVisible = false
    }

    private fun startConnectionToHotspotCamera() {
        showLoadingProgress()
        saveSerialNumberIfItIsCorrect()
        verifyConnectionWithTheCamera()
    }

    private fun saveSerialNumberIfItIsCorrect() {
        val serialNumberCamera = pairingViewModel.getNetworkName()
        if (CameraType.isValidNumberCameraBWC(serialNumberCamera)) {
            CameraInfo.setCameraType(serialNumberCamera)
            CameraInfo.serialNumber = serialNumberCamera.replace("X", "")
        }
    }

    private fun verifyConnectionWithTheCamera() {
        activity?.runOnUiThread {
            verifyProgressConnectionWithTheCamera()
        }
    }

    private fun verifyProgressConnectionWithTheCamera() {
        pairingViewModel.getProgressConnectionWithTheCamera()
    }

    private fun setProgressInViewOfProgress(progress: Int) {
        val percent = "$progress%"
        fragmentPairingResultBinding.textViewProgressConnection.text = percent
        if (progress == PERCENT_TOTAL_CONNECTION_CAMERA) {
            showSuccessResult()
        }
    }

    private fun setObservers() {
        pairingViewModel.cameraPairingProgress = ::manageResponseProgressInConnectionCamera
        pairingViewModel.isWaitFinishedLiveData.observe(
            viewLifecycleOwner
        ) { if (it) connectionSuccess(true) }
    }

    private fun manageResponseProgressInConnectionCamera(result: Result<Int>) {
        when (result) {
            is Result.Success -> setProgressInViewOfProgress(result.data)
            is Result.Error -> showErrorResult()
        }
    }

    private fun showSuccessResult() {
        fragmentPairingResultBinding.pairingProgressLayout.isVisible = false
        fragmentPairingResultBinding.pairingResultLayout.isVisible = true
        fragmentPairingResultBinding.buttonRetry.isVisible = false
        fragmentPairingResultBinding.imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_successful_green,
                null
            )
        )
        fragmentPairingResultBinding.textViewResultPairing.setText(R.string.success_connection_to_camera)
        fragmentPairingResultBinding.pairingResultLayout.startAnimation(animation)
        pairingViewModel.waitToFinish(ANIMATION_DURATION)
    }

    private fun showErrorResult() {
        fragmentPairingResultBinding.pairingProgressLayout.isVisible = false
        fragmentPairingResultBinding.pairingResultLayout.isVisible = true
        fragmentPairingResultBinding.buttonRetry.isVisible = true
        fragmentPairingResultBinding.imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_error_big,
                null
            )
        )
        fragmentPairingResultBinding.textViewResultPairing.setText(R.string.error_connection_to_camera)
        fragmentPairingResultBinding.pairingResultLayout.startAnimation(animation)
    }

    companion object {
        val TAG = PairingResultFragment::class.java.simpleName
        private const val PERCENT_TOTAL_CONNECTION_CAMERA = 100
        private const val ANIMATION_DURATION = 1200L

        fun createInstance(connectionSuccess: (isSuccess: Boolean) -> Unit): PairingResultFragment =
            PairingResultFragment().apply { this.connectionSuccess = connectionSuccess }
    }
}
