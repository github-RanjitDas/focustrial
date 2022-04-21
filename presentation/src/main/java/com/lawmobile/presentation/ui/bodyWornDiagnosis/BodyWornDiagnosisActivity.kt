package com.lawmobile.presentation.ui.bodyWornDiagnosis

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityBodyWornDiagnosisBinding
import com.lawmobile.presentation.extensions.activityCollect
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.bodyWornDiagnosis.state.DiagnosisState
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornDiagnosisActivity : BaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var binding: ActivityBodyWornDiagnosisBinding
    private val viewModel: BodyWornDiagnosisViewModel by viewModels()

    var state: DiagnosisState
        get() = viewModel.getDiagnosisState()
        set(value) = viewModel.setDiagnosisState(value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyWornDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCustomAppBar()
        configureListeners()
        setObservers()
    }

    private fun setCustomAppBar() = with(binding.diagnosisAppBar) {
        textViewTitle.text = getString(R.string.live_view_menu_item_diagnose)
        imageButtonBackArrow.setImageResource(R.drawable.ic_cancel)
        imageButtonBackArrow.tag = R.drawable.ic_cancel
        imageButtonBackArrow.setOnClickListener { onBackPressed() }
    }

    private fun configureListeners() = with(binding) {
        buttonStartDiagnosis.setOnClickListenerCheckConnection { state = DiagnosisState.Progress }
        diagnosisAppBar.imageButtonBackArrow.setOnClickListenerCheckConnection { onBackPressed() }
        buttonOkFinishedDiagnosis.setOnClickListenerCheckConnection { finish() }
    }

    private fun setObservers() {
        activityCollect(viewModel.diagnosisState, ::handleDiagnosisState)
        activityCollect(viewModel.diagnosisResult, ::resultDiagnosisCamera)
    }

    private fun resultDiagnosisCamera(result: Result<Boolean>) = with(binding) {
        with(result) {
            doIfSuccess { state = DiagnosisState.Finished(it) }
            doIfError {
                state = DiagnosisState.Start
                sectionBodyWornStartDiagnosis.showErrorSnackBar(
                    getString(R.string.error_trying_to_diagnose)
                )
            }
        }
    }

    private fun startDiagnosis() = with(binding) {
        sectionBodyWornStartDiagnosis.isVisible = true
        sectionProgressDiagnosis.isVisible = false
        sectionResultDiagnosis.isVisible = false
    }

    private fun progressDiagnosis() = with(binding) {
        sectionBodyWornStartDiagnosis.isVisible = false
        sectionProgressDiagnosis.isVisible = true
        sectionResultDiagnosis.isVisible = false
    }

    private fun finishedDiagnosis(isSuccess: Boolean) = with(binding) {
        sectionBodyWornStartDiagnosis.isVisible = false
        sectionProgressDiagnosis.isVisible = false
        sectionResultDiagnosis.isVisible = true
        if (isSuccess) showSuccessDiagnosis()
        else showErrorDiagnosis()
    }

    private fun showErrorDiagnosis() = with(binding) {
        titleBackgroundSolid.setBackgroundResource(R.drawable.background_solid_body_worn_diagnosis_error)
        titleBackgroundStroke.setBackgroundResource(R.drawable.background_stroke_body_worn_diagnosis_error)
        imageIconResult.setImageResource(R.drawable.ic_error_diagnosis_icon)
        imageIconResult.tag = R.drawable.ic_error_diagnosis_icon
        textTitleDiagnosis.text = getString(R.string.body_worn_diagnosis_error_text)
        textTitleDiagnosis.setTextColor(getColor(R.color.white))
        textDescriptionDiagnosis.text = getString(R.string.diagnosis_failed_message)
    }

    private fun showSuccessDiagnosis() = with(binding) {
        titleBackgroundSolid.setBackgroundResource(R.drawable.background_solid_body_worn_diagnosis_success)
        titleBackgroundStroke.setBackgroundResource(R.drawable.background_stroke_body_worn_diagnosis_success)
        imageIconResult.setImageResource(R.drawable.ic_success_icon)
        imageIconResult.tag = R.drawable.ic_success_icon
        textTitleDiagnosis.text = getString(R.string.body_worn_diagnosis_success_text)
        textTitleDiagnosis.setTextColor(getColor(R.color.white))
        textDescriptionDiagnosis.text = getString(R.string.diagnosis_success_message)
    }

    private fun handleDiagnosisState(diagnosisState: DiagnosisState) {
        with(diagnosisState) {
            onStartDiagnosis(::startDiagnosis)
            onProgressDiagnosis {
                viewModel.getDiagnosis()
                progressDiagnosis()
            }
            onFinishedDiagnosis(::finishedDiagnosis)
        }
    }
}
