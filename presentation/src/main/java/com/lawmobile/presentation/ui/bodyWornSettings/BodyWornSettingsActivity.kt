package com.lawmobile.presentation.ui.bodyWornSettings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityBodyWornSettingsBinding
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornSettingsActivity : BaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var binding: ActivityBodyWornSettingsBinding
    private val viewModel: BodyWornSettingsViewModel by viewModels()
    private var currentSettingChanged: TypesOfBodyWornSettings = TypesOfBodyWornSettings.CovertMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyWornSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCustomAppBar()
        setListener()
        setObservers()
        getBodyWornSettings()
    }

    private fun initWithDefault() {
        setBodyWornSettings(
            ParametersBodyWornSettings(
                CameraInfo.isCovertModeEnable,
                CameraInfo.isBluetoothEnable,
                false
            )
        )
    }

    private fun setCustomAppBar() {
        binding.layoutCustomAppBar.textViewTitle.text =
            getString(R.string.body_worn_settings_title)
        binding.layoutCustomAppBar.imageButtonBackArrow.setImageResource(R.drawable.ic_cancel)
        binding.layoutCustomAppBar.imageButtonBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getBodyWornSettings() {
        viewModel.getBodyWornSettings()
    }

    private fun setObservers() {
        viewModel.bodyCameraSettings.observe(this, ::resultBodyCameraSettings)
        viewModel.changeStatusSettingLiveData.observe(this, ::resultChangeStatusSetting)
    }

    private fun setListener() {
        binding.switchCovertMode.setClickListenerCheckConnection {
            changeBodyWornSettings(
                TypesOfBodyWornSettings.CovertMode,
                binding.switchCovertMode.isActivated
            )
        }

        binding.switchBluetooth.setClickListenerCheckConnection {
            changeBodyWornSettings(
                TypesOfBodyWornSettings.Bluetooth,
                binding.switchBluetooth.isActivated
            )
        }
    }

    private fun changeBodyWornSettings(
        typesOfBodyWornSettings: TypesOfBodyWornSettings,
        isEnable: Boolean
    ) {
        currentSettingChanged = typesOfBodyWornSettings
        viewModel.changeBodyWornSetting(typesOfBodyWornSettings, isEnable)
    }

    private fun resultChangeStatusSetting(result: Result<Unit>) {
        result.doIfError {
            SFConsoleLogs.log(
                SFConsoleLogs.Level.ERROR,
                SFConsoleLogs.Tags.TAG_CAMERA_ERRORS,
                it,
                getString(R.string.body_worn_settings_error_in_change_settings)
            )
            returnStatusForErrorInChange()
            binding.rootContent.showErrorSnackBar(getString(R.string.body_worn_settings_error_in_change_settings))
        }
    }

    private fun returnStatusForErrorInChange() {
        when (currentSettingChanged) {
            TypesOfBodyWornSettings.CovertMode -> {
                binding.switchCovertMode.isActivated = !binding.switchCovertMode.isActivated
            }
            TypesOfBodyWornSettings.Bluetooth -> {
                binding.switchBluetooth.isActivated = !binding.switchBluetooth.isActivated
            }
        }
    }

    private fun resultBodyCameraSettings(result: Result<ParametersBodyWornSettings>) {
        with(result) {
            doIfSuccess {
                setBodyWornSettings(it)
            }
            doIfError {
                SFConsoleLogs.log(
                    SFConsoleLogs.Level.ERROR,
                    SFConsoleLogs.Tags.TAG_CAMERA_ERRORS,
                    it,
                    getString(R.string.body_worn_settings_error_in_get_settings)
                )
                initWithDefault()
                binding.rootContent.showErrorSnackBar(
                    getString(R.string.body_worn_settings_error_in_get_settings),
                    onRetryClick = {
                        getBodyWornSettings()
                    }
                )
            }
        }
    }

    private fun setBodyWornSettings(parametersBodyWornSettings: ParametersBodyWornSettings) {
        binding.switchCovertMode.isActivated = parametersBodyWornSettings.isCovertModeEnable
        binding.switchBluetooth.isActivated = parametersBodyWornSettings.isBluetoothEnable
        binding.switchGPS.isActivated = parametersBodyWornSettings.isGPSEnable
        // Hide Bluetooth
        binding.switchBluetooth.isVisible = false
        binding.textViewBluetooth.isVisible = false
        binding.imageViewBluetooth.isVisible = false
    }
}
