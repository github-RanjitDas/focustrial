package com.lawmobile.presentation.ui.login.validateOfficerId

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.customEvents.InternetErrorEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentValidateOfficerIdBinding
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.onBoardingCards.OnBoardingCardsActivity
import com.lawmobile.presentation.ui.selectCamera.SelectCameraActivity
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidateOfficerIdFragment : BaseFragment() {

    private val viewModel: ValidateOfficerIdViewModel by viewModels()

    private var _binding: FragmentValidateOfficerIdBinding? = null
    private val binding get() = _binding!!

    private lateinit var onExistingOfficerId: (Boolean, String) -> Unit
    private lateinit var officerId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValidateOfficerIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifyInternetConnection()
        viewModel.setObservers()
        binding.setOfficerId()
        binding.setListeners()
    }

    private fun FragmentValidateOfficerIdBinding.setOfficerId() {
        editTextOfficerId.setText(officerId)
    }

    private fun verifyInternetConnection() {
        viewModel.verifyInternetConnection {
            if (!it) showNoInternetConnectionDialog()
        }
    }

    private fun showNoInternetConnectionDialog() {
        activity?.runOnUiThread {
            val cameraEvent = InternetErrorEvent.event
            context?.createNotificationDialog(cameraEvent)
                ?.setButtonText(resources.getString(R.string.OK))
        }
    }

    private fun ValidateOfficerIdViewModel.setObservers() {
        validateOfficerIdResult.observe(viewLifecycleOwner, ::manageValidateOfficerIdResult)
    }

    private fun manageValidateOfficerIdResult(result: Result<Boolean>) {
        val officerId = binding.editTextOfficerId.text.toString()
        with(result) {
            doIfSuccess { onExistingOfficerId(it, officerId) }
            doIfError { onExistingOfficerId(false, officerId) }
        }
    }

    private fun FragmentValidateOfficerIdBinding.setListeners() {
        editTextOfficerIdListener()
        buttonContinueListener()
        changeCameraListener()
        onBoardingCardsListener()
    }

    private fun FragmentValidateOfficerIdBinding.editTextOfficerIdListener() {
        editTextOfficerId.addTextChangedListener {
            buttonContinue.isEnabled = it.toString().isNotEmpty()
            buttonContinue.isActivated = it.toString().isNotEmpty()
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
        buttonChangeCamera.setOnClickListener { goToSelectCamera() }
    }

    private fun FragmentValidateOfficerIdBinding.buttonContinueListener() {
        buttonContinue.isEnabled = officerId.isNotEmpty()
        buttonContinue.isActivated = officerId.isNotEmpty()
        buttonContinue.setOnClickListener { validateOfficerId() }
    }

    private fun FragmentValidateOfficerIdBinding.validateOfficerId() {
        (activity as AppCompatActivity).hideKeyboard()
        val officerId = editTextOfficerId.text.toString()
        viewModel.verifyInternetConnection {
            if (it) viewModel.validateOfficerId(officerId)
            else onExistingOfficerId(false, officerId)
        }
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

    companion object {
        val TAG = ValidateOfficerIdFragment::class.java.simpleName

        fun createInstance(
            onExistingOfficerId: (Boolean, String) -> Unit,
            officerId: String
        ) = ValidateOfficerIdFragment().apply {
            this.officerId = officerId
            this.onExistingOfficerId = onExistingOfficerId
        }
    }
}
