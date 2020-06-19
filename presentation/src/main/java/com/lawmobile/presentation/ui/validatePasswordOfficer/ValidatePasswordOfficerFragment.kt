package com.lawmobile.presentation.ui.validatePasswordOfficer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.EncodePassword
import kotlinx.android.synthetic.main.fragmentg_validate_password_officer.*
import javax.inject.Inject

class ValidatePasswordOfficerFragment : BaseFragment() {

    @Inject
    lateinit var validatePasswordOfficerViewModel: ValidatePasswordOfficerViewModel
    private var domainUser: DomainUser? = null
    private lateinit var validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragmentg_validate_password_officer, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validatePasswordOfficerViewModel.createSingletonCameraHelper()
        getUserInformation()
        configureListeners()
    }

    private fun getUserInformation() {
        validatePasswordOfficerViewModel.getUserInformation()
        validatePasswordOfficerViewModel.domainUser.observe(
            viewLifecycleOwner,
            Observer { domainUser ->
                this.domainUser = domainUser
                textViewOfficerName.text = domainUser.name
                CameraInfo.officerId = domainUser.id
            })

        validatePasswordOfficerViewModel.errorDomainUser.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
            validateSuccessPasswordOfficer.invoke(false)
        })
    }

    private fun configureListeners() {
        imageButtonGo.setOnClickListenerCheckConnection {
            verifyPasswordOfficer()
        }
    }

    private fun verifyPasswordOfficer() {
        val sha256Password =
            EncodePassword.encodePasswordOfficer(textInputValidatePasswordOfficer.text())
        if (sha256Password.isNotEmpty() && sha256Password == domainUser?.password) {
            validateSuccessPasswordOfficer.invoke(true)
            return
        }

        validateSuccessPasswordOfficer.invoke(false)
    }

    companion object {
        fun createInstance(validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit) =
            ValidatePasswordOfficerFragment().apply {
                this.validateSuccessPasswordOfficer = validateSuccessPasswordOfficer
            }

        val TAG = ValidatePasswordOfficerFragment::class.java.simpleName
    }

}