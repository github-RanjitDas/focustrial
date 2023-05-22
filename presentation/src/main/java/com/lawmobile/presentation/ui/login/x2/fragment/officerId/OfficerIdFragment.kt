package com.lawmobile.presentation.ui.login.x2.fragment.officerId

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.customEvents.BluetoothErrorEvent
import com.lawmobile.domain.entities.customEvents.InternetErrorEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentValidateOfficerIdBinding
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.keystore.KeystoreHandler
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.onBoardingCards.OnBoardingCardsActivity
import com.lawmobile.presentation.ui.selectCamera.SelectCameraActivity
import com.lawmobile.presentation.utils.SFConsoleLogs
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import kotlin.reflect.KFunction1

class OfficerIdFragment : BaseFragment() {

    val handler = Handler(Looper.myLooper()!!)

    private val viewModel: OfficerIdViewModel by activityViewModels()

    private var _binding: FragmentValidateOfficerIdBinding? = null
    private val binding get() = _binding

    private lateinit var onContinueClick: (String) -> Unit

    private var wereConnectivityRequirementsChecked: Boolean
        get() = viewModel.wereConnectivityRequirementsChecked
        set(value) {
            viewModel.wereConnectivityRequirementsChecked = value
        }

    var officerId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValidateOfficerIdBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifyConnectivityRequirements()
        binding?.setListeners()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.officerId.isEmpty()) viewModel.officerId = officerId
        binding?.setOfficerId()
    }

    private fun verifyConnectivityRequirements() {
        val delay: Long = 1000
        if (wereConnectivityRequirementsChecked.not()) {
            handler.postDelayed({
                verifyInternetConnection()
                wereConnectivityRequirementsChecked = true
            }, delay)
            verifyBluetoothEnabled()
        }
    }

    private fun verifyBluetoothEnabled() {
        viewModel.verifyBluetoothEnabled {
            if (!it) showBluetoothOffDialog()
        }
    }

    private fun showBluetoothOffDialog() {
        activity?.runOnUiThread {
            val event = BluetoothErrorEvent.event
            context?.createNotificationDialog(event, true) {
                setButtonText(resources.getString(R.string.OK))
                onOkButtonClick = {
                    viewModel.enableBluetooth()
                }
            }
        }
    }

    private fun verifyInternetConnection() {
        viewModel.verifyInternetConnection {
            if (!it) showNoInternetConnectionDialog()
        }
    }

    private fun showNoInternetConnectionDialog() {
        SFConsoleLogs.log(
            SFConsoleLogs.Level.ERROR,
            SFConsoleLogs.Tags.TAG_INTERNET_CONNECTION_ERRORS,
            message = InternetErrorEvent.title
        )

        activity?.runOnUiThread {
            val cameraEvent = InternetErrorEvent.event
            context?.createNotificationDialog(cameraEvent)
                ?.setButtonText(resources.getString(R.string.OK))
        }
    }

    private fun FragmentValidateOfficerIdBinding.setOfficerId() {
        editTextOfficerId.setText(viewModel.officerId)
    }

    private fun FragmentValidateOfficerIdBinding.setListeners() {
        editTextOfficerIdListener()
        buttonContinueListener()
        changeCameraListener()
        onBoardingCardsListener()
    }

    private fun FragmentValidateOfficerIdBinding.editTextOfficerIdListener() {
        editTextOfficerId.addTextChangedListener {
            viewModel.officerId = it.toString()
            CameraInfo.officerId = it.toString()
            setButtonContinueEnable(it.toString().isNotEmpty())
        }
    }

    fun setButtonContinueEnable(enable: Boolean) {
        if (binding != null) {
            binding!!.buttonContinue.apply {
                this.isEnabled = enable
                isActivated = enable
            }
        }
    }

    private fun FragmentValidateOfficerIdBinding.onBoardingCardsListener() {
        textViewOnBoardingCards.setOnClickListener { goToOnBoardingCards() }
    }

    private fun goToOnBoardingCards() {
        val onBoardingCardsIntent = Intent(context, OnBoardingCardsActivity::class.java)
        activity?.startActivity(onBoardingCardsIntent)
        activity?.finish()
    }

    private fun FragmentValidateOfficerIdBinding.changeCameraListener() {
        buttonChangeCamera.setOnClickListener {
            KeystoreHandler.deleteKeystoreEntry()
            goToSelectCamera()
        }
    }

    private fun FragmentValidateOfficerIdBinding.buttonContinueListener() {
        setButtonContinueEnable(viewModel.officerId.isNotEmpty())
        buttonContinue.setOnClickListener {
            setButtonContinueEnable(false)
            validateOfficerId()
        }
    }

    private fun validateOfficerId() {
        (activity as AppCompatActivity).hideKeyboard()
        onContinueClick(viewModel.officerId)
    }

    private fun goToSelectCamera() {
        val selectCameraIntent = Intent(context, SelectCameraActivity::class.java)
        activity?.startActivity(selectCameraIntent)
        activity?.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = OfficerIdFragment::class.java.simpleName

        fun createInstance(
            onContinueClick: KFunction1<String, Unit>
        ) = OfficerIdFragment().apply {
            this.onContinueClick = onContinueClick
        }
    }
}
