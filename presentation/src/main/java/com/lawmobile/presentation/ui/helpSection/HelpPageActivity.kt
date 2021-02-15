package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityHelpPageBinding
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity

class HelpPageActivity : BaseActivity() {

    private lateinit var activityHelpPageBinding: ActivityHelpPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHelpPageBinding = ActivityHelpPageBinding.inflate(layoutInflater)
        setContentView(activityHelpPageBinding.root)

        setAppBar()
        loadPDFGuide()
    }

    private fun loadPDFGuide() {
        activityHelpPageBinding.pdfView.fromAsset(PDF_GUIDE_FILE_NAME).load()
    }

    private fun setAppBar() {
        with(activityHelpPageBinding.layoutFileListAppBar) {
            fileListAppBarTitle.text = getString(R.string.user_guide)
            buttonSimpleList.isVisible = false
            buttonThumbnailList.isVisible = false
            backArrowFileListAppBar.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        this.verifySessionBeforeAction {
            finish()
        }
    }

    companion object {
        const val PDF_GUIDE_FILE_NAME = "FMA_Interactive_Help.pdf"
    }
}
