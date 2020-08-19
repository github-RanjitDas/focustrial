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
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseFragment
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.fragment_pairing_result.*

class PairingResultFragment : BaseFragment() {

    private val pairingViewModel: PairingViewModel by viewModels()
    lateinit var connectionSuccess: (isSuccess: Boolean) -> Unit
    private lateinit var animation: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pairing_result, container, false)
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
        buttonRetry.setOnClickListener {
            startConnectionToHotspotCamera()
        }
    }

    private fun showLoadingProgress() {
        pairingProgressLayout.isVisible = true
        pairingResultLayout.isVisible = false
        buttonRetry.isVisible = false
    }

    private fun startConnectionToHotspotCamera() {
        showLoadingProgress()
        saveSerialNumberIfItIsCorrect()
        verifyConnectionWithTheCamera()
    }

    private fun saveSerialNumberIfItIsCorrect(){
        val serialNumberCamera = pairingViewModel.getNetworkName()
        if (pairingViewModel.isValidNumberCameraBWC(serialNumberCamera)) {
            CameraInfo.serialNumber = serialNumberCamera
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
        textViewProgressConnection.text = percent
        if (progress == PERCENT_TOTAL_CONNECTION_CAMERA) {
            showSuccessResult()
        }
    }

    private fun setObservers() {
        pairingViewModel.progressConnectionWithTheCamera.observe(
            viewLifecycleOwner,
            Observer(::manageResponseProgressInConnectionCamera)
        )
        pairingViewModel.isWaitFinishedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if (it) connectionSuccess(true)
            }
        )
    }

    private fun manageResponseProgressInConnectionCamera(result: Result<Int>) {
        when (result) {
            is Result.Success -> setProgressInViewOfProgress(result.data)
            is Result.Error -> showErrorResult()
        }
    }

    private fun showSuccessResult() {
        pairingProgressLayout.isVisible = false
        pairingResultLayout.isVisible = true
        buttonRetry.isVisible = false
        imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_successful_green,
                null
            )
        )
        textViewResultPairing.setText(R.string.success_connection_to_camera)
        pairingResultLayout.startAnimation(animation)
        pairingViewModel.waitToFinish(ANIMATION_DURATION)
    }

    private fun showErrorResult() {
        pairingProgressLayout.isVisible = false
        pairingResultLayout.isVisible = true
        buttonRetry.isVisible = true
        imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_error_big,
                null
            )
        )
        textViewResultPairing.setText(R.string.error_connection_to_camera)
        pairingResultLayout.startAnimation(animation)
    }

    companion object {
        val TAG = PairingResultFragment::class.java.simpleName
        private const val PERCENT_TOTAL_CONNECTION_CAMERA = 100
        private const val ANIMATION_DURATION = 1200L

        fun createInstance(connectionSuccess: (isSuccess: Boolean) -> Unit): PairingResultFragment =
            PairingResultFragment().apply { this.connectionSuccess = connectionSuccess }
    }


}