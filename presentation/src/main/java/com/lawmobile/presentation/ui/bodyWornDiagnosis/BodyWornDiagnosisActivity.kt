package com.lawmobile.presentation.ui.bodyWornDiagnosis

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityBodyWornDiagnosisBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornDiagnosisActivity : BaseActivity() {

    private lateinit var binding: ActivityBodyWornDiagnosisBinding
    private val viewModel: BodyWornDiagnosisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyWornDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureViewDependsStepDiagnosis(StepDiagnosis.START)
        setCustomAppBar()
        configureListeners()
        setObservers()
    }

    private fun setCustomAppBar() {
        binding.layoutCustomAppBar.textViewTitle.text =
            getString(R.string.live_view_menu_item_diagnose)
        binding.layoutCustomAppBar.buttonThumbnailList.isVisible = false
        binding.layoutCustomAppBar.buttonSimpleList.isVisible = false
    }

    private fun configureListeners() {
        binding.buttonStartDiagnosis.setOnClickListenerCheckConnection {
            configureViewDependsStepDiagnosis(StepDiagnosis.PROGRESS)
            viewModel.getDiagnosis()
        }

        binding.layoutCustomAppBar.imageButtonBackArrow.setOnClickListenerCheckConnection {
            onBackPressed()
        }

        binding.buttonOkFinishedDiagnosis.setOnClickListenerCheckConnection {
            finish()
        }
    }

    private fun setObservers() {
        viewModel.diagnosisCameraLiveData.observe(this, ::resultDiagnosisCamera)
    }

    private fun resultDiagnosisCamera(result: Result<Boolean>) {
        with(result) {
            doIfSuccess { isDiagnosisSuccess ->
                configureViewDependsStepDiagnosis(StepDiagnosis.FINISHED)
                if (isDiagnosisSuccess) {
                    binding.titleBackgroundSolid.setBackgroundResource(R.drawable.background_solid_body_worn_diagnosis_success)
                    binding.titleBackgroundStroke.setBackgroundResource(R.drawable.background_stroke_body_worn_diagnosis_success)
                    binding.imageIconResult.setImageResource(R.drawable.ic_success_icon)
                    binding.textTitleDiagnosis.text =
                        getString(R.string.body_worn_diagnosis_success_text)
                    binding.textTitleDiagnosis.setTextColor(getColor(R.color.white))
                    binding.textDescriptionDiagnosis.text =
                        getString(R.string.body_worn_result_success_description)
                } else {
                    binding.titleBackgroundSolid.setBackgroundResource(R.drawable.background_solid_body_worn_diagnosis_error)
                    binding.titleBackgroundStroke.setBackgroundResource(R.drawable.background_stroke_body_worn_diagnosis_error)
                    binding.imageIconResult.setImageResource(R.drawable.ic_error_diagnosis_icon)
                    binding.textTitleDiagnosis.text = getString(R.string.body_worn_diagnosis_error_text)
                    binding.textTitleDiagnosis.setTextColor(getColor(R.color.white))
                    binding.textDescriptionDiagnosis.text =
                        getString(R.string.body_worn_result_failed_description)
                }
            }
            doIfError {
                configureViewDependsStepDiagnosis(StepDiagnosis.START)
                binding.sectionBodyWornStartDiagnosis.showErrorSnackBar(getString(R.string.body_worn_icon_error_is_not_possible_get_diagnosis))
            }
        }
    }

    private fun configureViewDependsStepDiagnosis(step: StepDiagnosis) {
        when (step) {
            StepDiagnosis.START -> {
                binding.sectionBodyWornStartDiagnosis.isVisible = true
                binding.sectionProgressDiagnosis.isVisible = false
                binding.sectionResultDiagnosis.isVisible = false
            }

            StepDiagnosis.PROGRESS -> {
                binding.sectionBodyWornStartDiagnosis.isVisible = false
                binding.sectionProgressDiagnosis.isVisible = true
                binding.sectionResultDiagnosis.isVisible = false
            }

            StepDiagnosis.FINISHED -> {
                binding.sectionBodyWornStartDiagnosis.isVisible = false
                binding.sectionProgressDiagnosis.isVisible = false
                binding.sectionResultDiagnosis.isVisible = true
            }
        }
    }

    enum class StepDiagnosis {
        START, PROGRESS, FINISHED
    }
}
