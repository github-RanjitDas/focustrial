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
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SettingsBarFragment : BaseFragment() {

    private var _binding: FragmentStatusBarBinding? = null
    private val binding: FragmentStatusBarBinding get() = _binding!!
    private val settingsBarViewModel: SettingsBarViewModel by viewModels()
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
        setFeatures()
        setListenersForOpenOrCloseStatusBar()
        setListenersForChangeTheStatus()
        CameraInfo.onReadyToGetSettings = {
            getSettingsInformation()
        }
    }

    private fun setFeatures() {
        binding.constraintHideStatusBar.isVisible = FeatureSupportHelper.supportBodyWornSettings
    }

    private fun getSettingsInformation() {
        settingsBarViewModel.getBodyWornSettings()
    }

    private fun setObservers() {
        settingsBarViewModel.bodyWornSettingsLiveData.observe(
            requireActivity(),
            ::manageResultBodyWornSettings
        )
        settingsBarViewModel.changeStatusSettingLiveData.observe(
            requireActivity(),
            ::resultChangeStatusSetting
        )
    }

    private fun manageResultBodyWornSettings(result: Result<ParametersBodyWornSettings>) {
        with(result) {
            doIfSuccess {
                setCurrentStatusBar(it)
            }
        }
        activity?.runOnUiThread {
            hideLoadingDialog()
        }
    }

    private fun setCurrentStatusBar(parametersBodyWornSettings: ParametersBodyWornSettings) {
        binding.imageButtonCovertMode.isActivated = parametersBodyWornSettings.isCovertModeEnable
        binding.imageButtonGPS.isActivated = parametersBodyWornSettings.isGPSEnable
        binding.imageButtonBluetooth.isActivated = parametersBodyWornSettings.isBluetoothEnable
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

    private fun setListenersForChangeTheStatus() {

        binding.imageButtonGPS.setOnClickListenerCheckConnection {
            binding.imageButtonGPS.isActivated = !binding.imageButtonGPS.isActivated
            changeBodyWornSettings(
                TypesOfBodyWornSettings.GPS,
                binding.imageButtonGPS.isActivated
            )
        }

        binding.imageButtonBluetooth.setOnClickListenerCheckConnection {
            binding.imageButtonBluetooth.isActivated = !binding.imageButtonBluetooth.isActivated
            changeBodyWornSettings(
                TypesOfBodyWornSettings.Bluetooth,
                binding.imageButtonBluetooth.isActivated
            )
        }

        binding.imageButtonCovertMode.setOnClickListenerCheckConnection {
            binding.imageButtonCovertMode.isActivated = !binding.imageButtonCovertMode.isActivated
            changeBodyWornSettings(
                TypesOfBodyWornSettings.CovertMode,
                binding.imageButtonCovertMode.isActivated
            )
        }
    }

    private fun changeBodyWornSettings(
        typesOfBodyWornSettings: TypesOfBodyWornSettings,
        isEnable: Boolean
    ) {
        currentSettingChanged = typesOfBodyWornSettings
        settingsBarViewModel.changeBodyWornSetting(typesOfBodyWornSettings, isEnable)
    }

    private fun returnStatusForErrorInChange() {
        when (currentSettingChanged) {
            TypesOfBodyWornSettings.CovertMode -> {
                binding.imageButtonCovertMode.isActivated =
                    !binding.imageButtonCovertMode.isActivated
            }
            TypesOfBodyWornSettings.Bluetooth -> {
                binding.imageButtonBluetooth.isActivated = !binding.imageButtonBluetooth.isActivated
            }
            TypesOfBodyWornSettings.GPS -> {
                binding.imageButtonGPS.isActivated = !binding.imageButtonGPS.isActivated
            }
        }
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = SettingsBarFragment::class.java.simpleName
        fun createInstance(): SettingsBarFragment {
            return SettingsBarFragment()
        }
    }
}
