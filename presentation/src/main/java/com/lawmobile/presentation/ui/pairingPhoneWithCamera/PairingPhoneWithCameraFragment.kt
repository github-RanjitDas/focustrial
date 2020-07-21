package com.lawmobile.presentation.ui.pairingPhoneWithCamera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
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
    ): View? {
        return inflater.inflate(R.layout.fragment_pairing_phone_with_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureListeners()
    }

    private fun configureListeners() {
        imageButtonGo.setOnClickListener {
            if ((activity as BaseActivity).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                startConnectionToHotspotCamera()
            } else {
                showAlertToNavigateToPermissions()
            }
        }
        textInstructionsToLinkCamera.setOnClickListener {
            openHelpPage()
        }
    }

    private fun showAlertToNavigateToPermissions() {
        val alertInformation = AlertInformation(R.string.please_enable_permission,
            R.string.please_enable_permission_location,
            { startIntentToGivePermission() },
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

    private fun startConnectionToHotspotCamera() {
        if (!pairingPhoneWithCameraViewModel.isWifiEnable()) {
            createAlertToNavigateWifiSettings()
            return
        }

        val serialNumberCamera = pairingPhoneWithCameraViewModel.getNetworkName()

        if (pairingPhoneWithCameraViewModel.isValidNumberCameraBWC(serialNumberCamera)) {
            CameraInfo.serialNumber = serialNumberCamera
            startWithProgressPairing(true)
            verifyConnectionWithTheCamera()
        } else {
            activity?.showToast("Verify the connection to the camera wifi", Toast.LENGTH_SHORT)
        }
    }

    private fun createAlertToNavigateWifiSettings() {
        val alertInformation =
            AlertInformation(R.string.wifi_is_off, R.string.please_turn_wifi_on, {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }, {
                it.dismiss()
            })

        activity?.createAlertInformation(alertInformation)
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
            is Result.Error -> startWithProgressPairing(false)
        }
    }

    private fun setProgressInViewOfProgress(progress: Int) {
        startWithProgressPairing(true)
        circularProgressbar.progress = progress
        val percent = "$progress%"
        textViewProgressConnection.text = percent
        if (progress == PERCENT_TOTAL_CONNECTION_CAMERA) {
            connectionSuccess.invoke(true)
        }
    }

    private fun startWithProgressPairing(pairingVisible: Boolean) {
        activity?.runOnUiThread {
            if (pairingVisible) {
                constrainProgressValidateSSID.visibility = View.VISIBLE
                constrainValidateSSID.visibility = View.GONE
            } else {
                constrainProgressValidateSSID.visibility = View.GONE
                constrainValidateSSID.visibility = View.VISIBLE
            }
        }
    }

    private fun openHelpPage() {
        val intent = Intent(context, HelpPageActivity::class.java)
        startActivity(intent)
    }

    companion object {
        val TAG = PairingPhoneWithCameraFragment::class.java.simpleName
        private const val PERCENT_TOTAL_CONNECTION_CAMERA = 100

        fun createInstance(connectionSuccess: (isSuccess: Boolean) -> Unit): PairingPhoneWithCameraFragment =
            PairingPhoneWithCameraFragment().apply { this.connectionSuccess = connectionSuccess }
    }
}