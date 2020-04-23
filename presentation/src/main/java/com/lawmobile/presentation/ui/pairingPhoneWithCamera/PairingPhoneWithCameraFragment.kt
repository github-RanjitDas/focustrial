package com.lawmobile.presentation.ui.pairingPhoneWithCamera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entity.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.fragment_pairing_phone_with_camera.*
import javax.inject.Inject


class PairingPhoneWithCameraFragment : BaseFragment() {

    @Inject
    lateinit var pairingPhoneWithCameraViewModel: PairingPhoneWithCameraViewModel
    lateinit var connectionSuccess: (isSuccess: Boolean) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_pairing_phone_with_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startWithProgressPairing(false)
        checkIfExistSerialNumberSavedToConnectionToCamera()
        configureListeners()
        setSelectionEditTextOfSerialNumber()
    }

    private fun checkIfExistSerialNumberSavedToConnectionToCamera() {
        when (val result = pairingPhoneWithCameraViewModel.getSSIDSavedIfExist()) {
            is Result.Success -> {
                textInputValidateSSID.editText?.setText(result.data)
                starConnectionToHotspotCamera()
            }
        }
    }

    private fun configureListeners() {
        imageButtonGo.setOnClickListener {
            if ((activity as BaseActivity).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                starConnectionToHotspotCamera()
            } else {
                showAlertToNavigateToPermissions()
            }
        }
    }

    private fun showAlertToNavigateToPermissions() {
        val alertInformation = AlertInformation(R.string.please_enable_permission,
            R.string.please_enable_permission_location,
            { startIntentToGivePermission() },
            true,
            { dialogInterface -> dialogInterface.cancel() })
        activity?.createAlertInformation(alertInformation)
    }


    private fun startIntentToGivePermission() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun setSelectionEditTextOfSerialNumber() {
        textInputValidateSSID.editText?.setSelection(textInputValidateSSID.text().length)
    }

    private fun starConnectionToHotspotCamera() {
        val serialNumberCamera = textInputValidateSSID.text()
        if (pairingPhoneWithCameraViewModel.isValidNumberCameraBWC(serialNumberCamera)) {
            verifyConnectionWithTheCamera()
            return
        }

        createConnectionWithWifi(serialNumberCamera)
    }

    private fun createConnectionWithWifi(serialNumberCamera: String) {
        createConnectionWithCamera { isConnected ->
            val isPossiblePairingCamera =
                pairingPhoneWithCameraViewModel.isValidNumberCameraBWC(serialNumberCamera) && isConnected
            if (isPossiblePairingCamera) {
                verifyConnectionWithTheCamera()

            } else startWithProgressPairing(false)
        }
    }

    private fun verifyConnectionWithTheCamera() {
        activity?.runOnUiThread {
            startWithProgressPairing(true)
            verifyProgressConnectionWithTheCamera()
        }
    }

    private fun verifyProgressConnectionWithTheCamera() {
        pairingPhoneWithCameraViewModel.getProgressConnectionWithTheCamera()
        pairingPhoneWithCameraViewModel.progressConnectionWithTheCamera.observe(
            viewLifecycleOwner,
            Observer {
                manageResponseProgressInConnectionCamera(it)
            }
        )
    }

    private fun manageResponseProgressInConnectionCamera(result: Result<Int>) {
        when (result) {
            is Result.Success -> setProgressInViewOfProgress(result.data)
            is Result.Error -> startLoginActivityAgain()
        }
    }

    private fun startLoginActivityAgain() {
        val intent = Intent(context, LoginActivity::class.java)
        context?.startActivity(intent)
        activity?.finish()
        Runtime.getRuntime().exit(0)
    }

    private fun createConnectionWithCamera(isConnectedSuccess: (connected: Boolean) -> Unit) {
        val serialNumberCamera = textInputValidateSSID.text()
        pairingPhoneWithCameraViewModel.connectCellPhoneToWifiCamera(
            serialNumberCamera,
            isConnectedSuccess
        )
    }

    private fun setProgressInViewOfProgress(progress: Int) {
        circularProgressbar.progress = progress
        val percent = "$progress%"
        textViewProgressConnection.text = percent
        if (progress == PERCENT_TOTAL_CONNECTION_CAMERA) {
            val serialNumberCamera = textInputValidateSSID.text()
            pairingPhoneWithCameraViewModel.saveSerialNumberOfCamera(serialNumberCamera)
            connectionSuccess.invoke(true)
        }
    }

    private fun startWithProgressPairing(pairingVisible: Boolean) {
        if (pairingVisible) {
            constrainProgressValidateSSID.visibility = View.VISIBLE
            constrainValidateSSID.visibility = View.GONE
            return
        }

        constrainProgressValidateSSID.visibility = View.GONE
        constrainValidateSSID.visibility = View.VISIBLE
    }

    companion object {
        val TAG = PairingPhoneWithCameraFragment::class.java.simpleName
        private const val PERCENT_TOTAL_CONNECTION_CAMERA = 100

        fun createInstance(connectionSuccess: (isSuccess: Boolean) -> Unit): PairingPhoneWithCameraFragment =
            PairingPhoneWithCameraFragment().apply { this.connectionSuccess = connectionSuccess }
    }
}