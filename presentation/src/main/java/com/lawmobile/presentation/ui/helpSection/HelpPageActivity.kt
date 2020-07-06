package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import android.view.MenuItem
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.createAlertErrorConnection
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.CameraHelper
import kotlinx.android.synthetic.main.activity_help_page.*

class HelpPageActivity : BaseActivity() {

    private var startedFromLiveActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_page)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        startedFromLiveActivity = intent.getBooleanExtra("LiveActivity", false)

        pdfView.fromAsset("FMA_Interactive_Help.pdf").load()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (startedFromLiveActivity) {
            when (item.itemId) {
                android.R.id.home -> onBackPressed()
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (checkConnectionToCamera()) finish()
    }

    private fun checkConnectionToCamera(): Boolean {
        val isSessionExpired = checkIfSessionIsExpired()
        if (isSessionExpired) {
            this.createAlertSessionExpired()
            return false
        }
        return if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
            this.createAlertErrorConnection()
            false
        } else true
    }
}