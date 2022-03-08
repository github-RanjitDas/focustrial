package com.lawmobile.presentation.ui.login.x1.fragment.officerPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentOfficerPasswordBinding
import com.lawmobile.presentation.extensions.getIntentForCameraType
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.live.x1.LiveX1Activity
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.utils.EncodePassword
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.android_commons.extensions.hideKeyboard

class OfficerPasswordFragment : BaseFragment() {

    private var _binding: FragmentOfficerPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OfficerPasswordViewModel by activityViewModels()

    private val officerPassword: String get() = viewModel.officerPassword

    var passwordFromCamera: String = ""
    var onEmptyPassword: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentOfficerPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setViews()
        binding.setListeners()
    }

    private fun FragmentOfficerPasswordBinding.setViews() {
        editTextOfficerPassword.setText(officerPassword)
    }

    private fun FragmentOfficerPasswordBinding.setListeners() {
        buttonLoginListener()
        editTextOfficerPasswordListener()
    }

    private fun FragmentOfficerPasswordBinding.editTextOfficerPasswordListener() {
        editTextOfficerPassword.addTextChangedListener {
            viewModel.officerPassword = it.toString()
        }
    }

    private fun FragmentOfficerPasswordBinding.buttonLoginListener() {
        buttonLogin.setOnClickListenerCheckConnection {
            (activity as BaseActivity).hideKeyboard()
            EspressoIdlingResource.increment()
            validateOfficerPassword()
        }
    }

    private fun validateOfficerPassword() {
        if (passwordFromCamera.isEmpty()) showUserInformationError()
        else {
            if (isCorrectPassword()) startLiveViewActivity()
            else {
                binding.root.showErrorSnackBar(getString(R.string.incorrect_password))
                EspressoIdlingResource.decrement()
            }
        }
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = context?.getIntentForCameraType(
            LiveX1Activity::class.java,
            LiveX2Activity::class.java
        )
        startActivity(intent)
        activity?.finish()
    }

    private fun showUserInformationError() {
        (activity as AppCompatActivity).hideKeyboard()
        binding.root.showErrorSnackBar(
            getString(R.string.error_getting_officer_information),
            Snackbar.LENGTH_INDEFINITE
        ) {
            context?.verifySessionBeforeAction {
                onEmptyPassword?.invoke()
            }
        }
    }

    private fun isCorrectPassword(): Boolean {
        val inputPassword = binding.textInputOfficerPassword.text()
        val sha256Password = EncodePassword.encodePasswordOfficer(inputPassword)
        return sha256Password.isNotEmpty() && sha256Password == passwordFromCamera
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = OfficerPasswordFragment::class.java.simpleName
    }
}
