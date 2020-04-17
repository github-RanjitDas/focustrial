package com.lawmobile.presentation.ui.login

import android.content.pm.PackageInfo
import android.os.Bundle
import android.widget.Toast
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.pairingPhoneWithCamera.PairingPhoneWithCameraFragment
import com.lawmobile.presentation.ui.validatePasswordOfficer.ValidatePasswordOfficerFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private lateinit var connectionSuccess: (isSuccess: Boolean) -> Unit
    private lateinit var validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        manageConnectionCamera()
        manageValidatePasswordOfficer()
        setTextVersion()
        showFragmentPairingCamera()
    }

    private fun manageConnectionCamera() {

        connectionSuccess = { isSuccess ->
            if (isSuccess) {
                showFragmentValidatePasswordOfficer()
            } else {
                showFragmentPairingCamera()
            }
        }
    }

    private fun manageValidatePasswordOfficer() {
        validateSuccessPasswordOfficer = { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "password success", Toast.LENGTH_LONG).show()
            } else {
                showFragmentPairingCamera()
            }
        }
    }

    private fun showFragmentPairingCamera() {
        supportFragmentManager.attachFragment(
            containerId = R.id.constrainContainer,
            fragment = PairingPhoneWithCameraFragment.createInstance(connectionSuccess),
            tag = PairingPhoneWithCameraFragment.TAG
        )
    }

    private fun showFragmentValidatePasswordOfficer() {
        supportFragmentManager.attachFragment(
            containerId = R.id.constrainContainer,
            fragment = ValidatePasswordOfficerFragment.createInstance(validateSuccessPasswordOfficer),
            tag = ValidatePasswordOfficerFragment.TAG
        )
    }

    private fun setTextVersion() {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(this.packageName, 0)
        textViewVersion.text = String.format("Version %s ", packageInfo.versionName)
    }

}
