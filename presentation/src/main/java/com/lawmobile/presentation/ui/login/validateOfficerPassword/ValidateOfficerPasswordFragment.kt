package com.lawmobile.presentation.ui.login.validateOfficerPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentValidateOfficerPasswordBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.EncodePassword
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.android_commons.extensions.hideKeyboard
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ValidateOfficerPasswordFragment : BaseFragment() {

    private var _fragmentValidateOfficerPasswordBinding: FragmentValidateOfficerPasswordBinding? =
        null
    private val fragmentValidateOfficerPasswordBinding get() = _fragmentValidateOfficerPasswordBinding!!

    private val validateOfficerPasswordViewModel: ValidateOfficerPasswordViewModel by viewModels()
    private var domainUser: DomainUser? = null
    private lateinit var validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentValidateOfficerPasswordBinding =
            FragmentValidateOfficerPasswordBinding.inflate(inflater, container, false)
        return fragmentValidateOfficerPasswordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validateOfficerPasswordViewModel.createSingletonCameraHelper()
        getUserInformation()
        configureListeners()
    }

    private fun getUserInformation() {
        validateOfficerPasswordViewModel.getUserInformation()
        validateOfficerPasswordViewModel.domainUserLiveData.observe(
            viewLifecycleOwner,
            Observer(::handleUserInformationResult)
        )
    }

    private fun handleUserInformationResult(result: Result<DomainUser>) {
        when (result) {
            is Result.Success -> {
                with(result.data) {
                    domainUser = this
                    CameraInfo.officerName = name
                    CameraInfo.officerId = id
                }
            }
            is Result.Error -> {
                showErrorInGetInformationOfUser()
            }
        }
    }

    private fun configureListeners() {
        fragmentValidateOfficerPasswordBinding.buttonLogin.setOnClickListenerCheckConnection {
            (activity as BaseActivity).hideKeyboard()
            EspressoIdlingResource.increment()
            verifyPasswordOfficer()
        }
    }

    private fun verifyPasswordOfficer() {
        if (domainUser?.password == null) {
            showErrorInGetInformationOfUser()
            return
        }
        val sha256Password =
            EncodePassword.encodePasswordOfficer(fragmentValidateOfficerPasswordBinding.textInputOfficerPassword.text())
        if (sha256Password.isNotEmpty() && sha256Password == domainUser?.password) {
            validateSuccessPasswordOfficer(true)
        } else {
            validateSuccessPasswordOfficer(false)
        }
    }

    private fun showErrorInGetInformationOfUser() {
        (activity as BaseActivity).hideKeyboard()
        fragmentValidateOfficerPasswordBinding.rootLayoutValidateOfficer.showErrorSnackBar(
            getString(R.string.error_getting_officer_information),
            Snackbar.LENGTH_INDEFINITE
        ) {
            context?.verifySessionBeforeAction { validateOfficerPasswordViewModel.getUserInformation() }
        }
    }

    companion object {
        fun createInstance(validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit) =
            ValidateOfficerPasswordFragment().apply {
                this.validateSuccessPasswordOfficer = validateSuccessPasswordOfficer
            }

        val TAG = ValidateOfficerPasswordFragment::class.java.simpleName
    }
}
