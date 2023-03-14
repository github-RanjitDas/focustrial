package com.lawmobile.presentation.ui.base.settingsBar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentStatusBarBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseFragment
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SettingsBarFragment : BaseFragment() {

    private var _binding: FragmentStatusBarBinding? = null
    private val binding: FragmentStatusBarBinding get() = _binding!!
    private val viewModel: SettingsBarViewModel by viewModels()
    private var currentSettingChanged: TypesOfBodyWornSettings = TypesOfBodyWornSettings.CovertMode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListenersForOpenOrCloseStatusBar()
        setListenersForChangeTheStatus()
    }

    suspend fun getBodyCameraSettings() {
        viewModel.getBodyCameraSettings()
    }

    private fun initWithDefault() {
        setCurrentStatusBar(
            ParametersBodyWornSettings(
                CameraInfo.isCovertModeEnable,
                CameraInfo.isBluetoothEnable,
                false
            )
        )
    }

    private fun setObservers() {
        viewModel.bodyCameraSettings.observe(
            viewLifecycleOwner,
            ::manageBodyCameraSettingsResult
        )
        viewModel.changeStatusSettingLiveData.observe(
            viewLifecycleOwner,
            ::resultChangeStatusSetting
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBodyCameraSettingsInBg()
    }

    private fun manageBodyCameraSettingsResult(result: Result<ParametersBodyWornSettings>) {
        result.doIfSuccess(::setCurrentStatusBar)
        result.doIfError {
            initWithDefault()
        }
        activity?.runOnUiThread { hideLoadingDialog() }
    }

    private fun setCurrentStatusBar(parametersBodyWornSettings: ParametersBodyWornSettings) =
        with(binding) {
            imageButtonCovertMode.isActivated = parametersBodyWornSettings.isCovertModeEnable
            imageButtonGPS.isActivated = parametersBodyWornSettings.isGPSEnable
            imageButtonBluetooth.isActivated = parametersBodyWornSettings.isBluetoothEnable
        }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListenersForOpenOrCloseStatusBar() {
        binding.imageButtonArrowHide.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isShowCompleteStatusBar(true)
                    true
                }
                else -> return@setOnTouchListener false
            }
        }

        binding.imageButtonArrowShow.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_UP -> {
                    isShowCompleteStatusBar(false)
                    true
                }
                else -> return@setOnTouchListener false
            }
        }
    }

    private fun isShowCompleteStatusBar(isShow: Boolean) {
        binding.constraintShowStatusBar.isVisible = isShow
        binding.constraintHideStatusBar.isVisible = !isShow
        if (isShow) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom_anim)
            binding.constraintShowStatusBar.startAnimation(animation)
            return
        }
        val animationHide = AnimationUtils.loadAnimation(context, R.anim.bottom_to_top_anim)
        binding.constraintHideStatusBar.startAnimation(animationHide)
    }

    private fun resultChangeStatusSetting(result: Result<Unit>) {
        result.doIfError {
            returnStatusForErrorInChange()
            binding.root.showErrorSnackBar(getString(R.string.body_worn_settings_error_in_change_settings))
        }
    }

    private fun setListenersForChangeTheStatus() = with(binding) {
        imageButtonBluetooth.setOnClickListenerCheckConnection {
            imageButtonBluetooth.isActivated = !imageButtonBluetooth.isActivated
            changeBodyWornSettings(
                TypesOfBodyWornSettings.Bluetooth,
                imageButtonBluetooth.isActivated
            )
        }

        imageButtonCovertMode.setOnClickListenerCheckConnection {
            imageButtonCovertMode.isActivated = !imageButtonCovertMode.isActivated
            changeBodyWornSettings(
                TypesOfBodyWornSettings.CovertMode,
                imageButtonCovertMode.isActivated
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

    private fun returnStatusForErrorInChange() = with(binding) {
        when (currentSettingChanged) {
            TypesOfBodyWornSettings.CovertMode ->
                imageButtonCovertMode.isActivated = !imageButtonCovertMode.isActivated
            TypesOfBodyWornSettings.Bluetooth ->
                imageButtonBluetooth.isActivated = !imageButtonBluetooth.isActivated
        }
    }

    override val viewTag: String get() = TAG

    companion object {
        val TAG: String = SettingsBarFragment::class.java.simpleName
    }
}
