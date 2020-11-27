package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import android.view.MenuItem
import com.lawmobile.presentation.databinding.ActivityHelpPageBinding
import com.lawmobile.presentation.extensions.checkIfSessionIsExpired
import com.lawmobile.presentation.extensions.createAlertErrorConnection
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.CameraHelper

class HelpPageActivity : BaseActivity() {

    private var startedFromLiveActivity: Boolean = false
    private lateinit var activityHelpPageBinding: ActivityHelpPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHelpPageBinding = ActivityHelpPageBinding.inflate(layoutInflater)
        setContentView(activityHelpPageBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        startedFromLiveActivity = intent.getBooleanExtra("LiveActivity", false)

        activityHelpPageBinding.pdfView.fromAsset("FMA_Interactive_Help.pdf").load()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (startedFromLiveActivity) {
            if (checkConnectionToCamera()) finish()
        } else {
            finish()
        }
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