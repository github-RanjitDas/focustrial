package com.lawmobile.presentation.ui.pairingPhoneWithCamera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
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
import java.lang.Exception
import javax.inject.Inject


class PairingPhoneWithCameraFragment : BaseFragment() {

    @Inject
    lateinit var pairingPhoneWithCameraViewModel: PairingPhoneWithCameraViewModel
    lateinit var connectionSuccess: (isSuccess: Boolean) -> Unit
    private var startByDefaultPairing: Boolean = false

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
        configureObservers()
        if (startByDefaultPairing) {
            startWithProgressPairing(pairingVisible = true)
            setProgressInViewOfProgress(0)
            Thread.sleep(1500)
            startTheConnection()
        }
    }

    private fun configureObservers() {
        pairingPhoneWithCameraViewModel.progressConnectionWithTheCamera.observe(
            viewLifecycleOwner,
            Observer {
                manageResponseProgressInConnectionCamera(it)
            }
        )
    }

    private fun configureListeners() {
        imageButtonGo.setOnClickListener {
            startPairingInTapButtonGo()
        }
        textInstructionsToLinkCamera.setOnClickListener {
            openHelpPage()
        }
    }

    private fun startPairingInTapButtonGo() {
        if ((activity as BaseActivity).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (isGpSActive()) {
                startConnectionToHotspotCamera()
            } else {
                val alertInformation = AlertInformation(
                    R.string.gps_necessary_title,
                    R.string.gps_necessary_description,
                    {
                        it.dismiss()
                    })
                context?.createAlertInformation(alertInformation)
            }
        } else {
            showAlertToNavigateToPermissions()
        }
    }

    private fun isGpSActive(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnable = false
        try {
            gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }

        return gpsEnable
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
            CameraInfo.serialNumber = serialNumberCamera.replace("X57", "57")
            startTheConnection()
            return
        }

        startWithProgressPairing(pairingVisible = true)
        setProgressInViewOfProgress(0)
        Thread.sleep(1500)
        startTheConnection()
    }

    private fun startTheConnection() {
        startWithProgressPairing(true)
        verifyConnectionWithTheCamera()
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
    }

    private fun manageResponseProgressInConnectionCamera(result: Result<Int>) {
        when (result) {
            is Result.Success -> setProgressInViewOfProgress(result.data)
            is Result.Error -> {
                activity?.showToast(getString(R.string.verify_wifi_connection), Toast.LENGTH_SHORT)
                startWithProgressPairing(false)
            }
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

        fun createInstance(
            connectionSuccess: (isSuccess: Boolean) -> Unit,
            startByDefaultPairing: Boolean
        ): PairingPhoneWithCameraFragment =
            PairingPhoneWithCameraFragment().apply {
                this.connectionSuccess = connectionSuccess
                this.startByDefaultPairing = startByDefaultPairing
            }
    }
}