package com.lawmobile.presentation.ui.validatePasswordOfficer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.text
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.utils.EncodePassword
import kotlinx.android.synthetic.main.fragmentg_validate_password_officer.*
import javax.inject.Inject

class ValidatePasswordOfficerFragment : BaseFragment() {

    @Inject
    lateinit var validatePasswordOfficerViewModel: ValidatePasswordOfficerViewModel
    private var domainUser: DomainUser? = null
    private lateinit var validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit
    private lateinit var alertProgress: AlertDialog
    private lateinit var failedGetUser: () -> Unit
    private var currentAttemptToGetInformation = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragmentg_validate_password_officer, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validatePasswordOfficerViewModel.createSingletonCameraHelper()
        alertProgress = (activity as BaseActivity).createAlertProgress()
        alertProgress.show()
        getUserInformation()
        configureListeners()
    }

    private fun getUserInformation() {
        validatePasswordOfficerViewModel.getUserInformation()
        validatePasswordOfficerViewModel.domainUser.observe(
            viewLifecycleOwner,
            Observer { domainUser ->
                activity?.runOnUiThread {
                    this.domainUser = domainUser
                    textViewOfficerName.text = domainUser.name
                    CameraInfo.officerId = domainUser.id
                    alertProgress.hide()
                }
            })

        validatePasswordOfficerViewModel.errorDomainUser.observe(viewLifecycleOwner, Observer {
            if (currentAttemptToGetInformation < totalAttemptsToGetInformation) {
                currentAttemptToGetInformation++
                validatePasswordOfficerViewModel.getUserInformation()
            } else {
                alertProgress.hide()
                Toast.makeText(activity, "Error getting information of the user, trying again", Toast.LENGTH_LONG).show()
                failedGetUser.invoke()
            }
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
        fun createInstance(validateSuccessPasswordOfficer: (isSuccess: Boolean) -> Unit,
                           failedGetUser: () -> Unit) =
            ValidatePasswordOfficerFragment().apply {
                this.validateSuccessPasswordOfficer = validateSuccessPasswordOfficer
                this.failedGetUser = failedGetUser
            }

        val TAG = ValidatePasswordOfficerFragment::class.java.simpleName
        private const val totalAttemptsToGetInformation: Int = 5
    }

}