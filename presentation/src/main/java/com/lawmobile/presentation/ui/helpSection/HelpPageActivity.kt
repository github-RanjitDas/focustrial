package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityHelpPageBinding
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity

class HelpPageActivity : BaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var activityHelpPageBinding: ActivityHelpPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHelpPageBinding = ActivityHelpPageBinding.inflate(layoutInflater)
        setContentView(activityHelpPageBinding.root)

        setAppBar()
        setBottomSheetBehavior()
        loadPDFGuideDependsCameraType()
    }

    private fun setBottomSheetBehavior() {
        val bottomSheetBehavior =
            BottomSheetBehavior.from(activityHelpPageBinding.containerHelpPage)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
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

    private fun loadPDFGuideDependsCameraType() {
        when (CameraInfo.cameraType) {
            CameraType.X2 -> loadPDFGuideCameraX2()
        }
    }


    private fun loadPDFGuideCameraX2() {
        activityHelpPageBinding.pdfView.fromAsset(PDF_GUIDE_FILE_NAME_X2).load()
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
        private const val PDF_GUIDE_FILE_NAME_X2 = "FMA_Interactive_Help_X2.pdf"
    }
}
