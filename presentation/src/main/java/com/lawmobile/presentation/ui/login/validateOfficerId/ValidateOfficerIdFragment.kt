package com.lawmobile.presentation.ui.login.validateOfficerId

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.NoInternetEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentValidateOfficerIdBinding
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.selectCamera.SelectCameraActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidateOfficerIdFragment : BaseFragment() {

    private val viewModel: ValidateOfficerIdViewModel by viewModels()

    private var _binding: FragmentValidateOfficerIdBinding? = null
    private val binding get() = _binding!!

    private lateinit var onExistingOfficerId: (Boolean) -> Unit

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
        setObservers()
        binding.setListeners()
    }

    private fun verifyInternetConnection() {
        viewModel.verifyInternetConnection {
            if (!it) showNoInternetConnectionDialog()
        }
    }

    private fun showNoInternetConnectionDialog() {
        activity?.runOnUiThread {
            val cameraEvent = NoInternetEvent.event
            context?.createNotificationDialog(cameraEvent)
                ?.setButtonText(resources.getString(R.string.OK))
        }
    }

    private fun setObservers() {
        viewModel.validateOfficerIdResult.observe(
            viewLifecycleOwner,
            ::manageValidateOfficerIdResult
        )
    }

    private fun manageValidateOfficerIdResult(result: Result<Boolean>) {
        with(result) {
            doIfSuccess { onExistingOfficerId(it) }
            doIfError { onExistingOfficerId(false) }
        }
    }

    private fun FragmentValidateOfficerIdBinding.setListeners() {
        buttonContinueListener()
        changeCameraListener()
        onBoardingCardsListener()
    }

    private fun FragmentValidateOfficerIdBinding.onBoardingCardsListener() {
        textViewOnBoardingCards.setOnClickListener {
            goToOnBoardingCards()
        }
    }

    private fun goToOnBoardingCards() {
        // pending to implement
    }

    private fun FragmentValidateOfficerIdBinding.changeCameraListener() {
        textViewChangeCamera.setOnClickListener {
            goToSelectCamera()
        }
    }

    private fun FragmentValidateOfficerIdBinding.buttonContinueListener() {
        buttonContinue.setOnClickListener {
            val officerId = editTextOfficerId.text.toString()
            viewModel.validateOfficerId(officerId)
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

        fun createInstance(onExistingOfficerId: (Boolean) -> Unit) =
            ValidateOfficerIdFragment().apply {
                this.onExistingOfficerId = onExistingOfficerId
            }
    }
}
