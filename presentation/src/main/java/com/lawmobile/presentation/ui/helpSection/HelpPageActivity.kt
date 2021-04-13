package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
        setBottomSheetBehavior()
        loadPDFGuide()
    }

    private fun setBottomSheetBehavior() {
        val bottomSheetBehavior = BottomSheetBehavior.from(activityHelpPageBinding.containerHelpPage)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Does not need any special behaviour
            }
        })
    }

    private fun loadPDFGuide() {
        activityHelpPageBinding.pdfView.fromAsset(PDF_GUIDE_FILE_NAME).load()
    }

    private fun setAppBar() {
        with(activityHelpPageBinding) {
            textViewTitle.text = getString(R.string.user_guide)
            imageButtonBackArrow.setImageResource(R.drawable.ic_cancel)
            imageButtonBackArrow.setOnClickListener {
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
