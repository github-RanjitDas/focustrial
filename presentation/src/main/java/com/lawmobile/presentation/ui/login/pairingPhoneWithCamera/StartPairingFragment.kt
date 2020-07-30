package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_start_pairing.*
import javax.inject.Inject


class StartPairingFragment : BaseFragment() {

    @Inject
    lateinit var pairingViewModel: PairingViewModel
    lateinit var validateRequirements: (isSuccess: Boolean) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_pairing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureListeners()
    }

    private fun configureListeners() {
        buttonLogin.setOnClickListener {
            verifyPermissionsToStartPairing()
        }
        buttonInstructionsToLinkCamera.setOnClickListener {
            openHelpPage()
        }
    }

    private fun verifyPermissionsToStartPairing() {
        if (arePermissionsGranted()) {
            startPairingResultFragment()
        } else {
            showAlertToNavigateToPermissions()
        }
    }

    private fun arePermissionsGranted() =
        (activity as BaseActivity).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun startPairingResultFragment() {
        if (!pairingViewModel.isWifiEnable()) {
            createAlertToNavigateWifiSettings()
            return
        }
        val serialNumberCamera = pairingViewModel.getNetworkName()
        if (!pairingViewModel.isValidNumberCameraBWC(serialNumberCamera)) {
            activity?.fragmentContainer?.showErrorSnackBar(getString(R.string.verify_camera_wifi))
            return
        }
        validateRequirements(true)
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

    private fun createAlertToNavigateWifiSettings() {
        val alertInformation =
            AlertInformation(R.string.wifi_is_off, R.string.please_turn_wifi_on, {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }, {
                it.dismiss()
            })

        activity?.createAlertInformation(alertInformation)
    }

    private fun openHelpPage() {
        val intent = Intent(context, HelpPageActivity::class.java)
        startActivity(intent)
    }

    companion object {
        val TAG = StartPairingFragment::class.java.simpleName
        fun createInstance(connectionSuccess: (isSuccess: Boolean) -> Unit): StartPairingFragment =
            StartPairingFragment().apply { this.validateRequirements = connectionSuccess }
    }
}