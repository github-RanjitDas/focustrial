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
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.EncodePassword
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.hideKeyboard
import kotlinx.android.synthetic.main.fragment_validate_officer_password.*

class ValidateOfficerPasswordFragment : BaseFragment() {

    private val validateOfficerPasswordViewModel: ValidateOfficerPasswordViewModel by viewModels()
    private var domainUser: DomainUser? = null
    private lateinit var validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_validate_officer_password, container, false)

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
        buttonLogin.setOnClickListenerCheckConnection {
            (activity as LoginActivity).hideKeyboard()
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
            EncodePassword.encodePasswordOfficer(textInputOfficerPassword.text())
        if (sha256Password.isNotEmpty() && sha256Password == domainUser?.password) {
            validateSuccessPasswordOfficer(true)
            return
        }

        validateSuccessPasswordOfficer(false)
    }

    private fun showErrorInGetInformationOfUser() {
        rootLayoutValidateOfficer.showErrorSnackBar(
            getString(R.string.error_getting_officer_information),
            Snackbar.LENGTH_INDEFINITE
        ) {
            validateOfficerPasswordViewModel.getUserInformation()
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