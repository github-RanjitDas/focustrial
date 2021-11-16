package com.lawmobile.presentation.ui.login.validateOfficerPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.presentation.databinding.FragmentValidateOfficerPasswordBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.EncodePassword
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.android_commons.extensions.hideKeyboard

class ValidateOfficerPasswordFragment : BaseFragment() {

    private var _binding: FragmentValidateOfficerPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragmentListener: ValidateOfficerPasswordFragmentListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentValidateOfficerPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setListeners()
    }

    private fun FragmentValidateOfficerPasswordBinding.setListeners() {
        buttonLogin.setOnClickListenerCheckConnection {
            (activity as BaseActivity).hideKeyboard()
            EspressoIdlingResource.increment()
            validateOfficerPassword()
        }
    }

    private fun validateOfficerPassword() {
        if (fragmentListener.user?.password == null) fragmentListener.onEmptyUserInformation()
        else fragmentListener.onPasswordValidationResult(isCorrectPassword())
    }

    private fun isCorrectPassword(): Boolean {
        val inputPassword = binding.textInputOfficerPassword.text()
        val sha256Password = EncodePassword.encodePasswordOfficer(inputPassword)
        return sha256Password.isNotEmpty() && sha256Password == fragmentListener.user?.password
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun createInstance(
            fragmentListener: ValidateOfficerPasswordFragmentListener
        ) = ValidateOfficerPasswordFragment().apply {
            this.fragmentListener = fragmentListener
        }

        val TAG = ValidateOfficerPasswordFragment::class.java.simpleName
    }
}
