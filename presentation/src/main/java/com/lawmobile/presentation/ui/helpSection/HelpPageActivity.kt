package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_help_page.*
import kotlinx.android.synthetic.main.custom_app_bar.*

class HelpPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_page)

        setAppBar()
        loadPDFGuide()
    }

    private fun loadPDFGuide() {
        pdfView.fromAsset(PDF_GUIDE_FILE_NAME).load()
    }

    private fun setAppBar() {
        with(layoutFileListAppBar) {
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