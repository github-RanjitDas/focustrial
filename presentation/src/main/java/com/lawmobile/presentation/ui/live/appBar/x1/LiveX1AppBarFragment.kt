package com.lawmobile.presentation.ui.live.appBar.x1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.databinding.FragmentLiveAppBarX1Binding
import com.lawmobile.presentation.extensions.createAlertConfirmAppExit
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity

class LiveX1AppBarFragment : BaseFragment() {

    private val viewModel: AppBarX1ViewModel by activityViewModels()

    private var _binding: FragmentLiveAppBarX1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveAppBarX1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOfficerName()
        setListeners()
    }

    private fun setOfficerName() {
        try {
            binding.textViewOfficerName.text = CameraInfo.officerName.split(" ")[0]
            binding.textViewOfficerLastName.text = CameraInfo.officerName.split(" ")[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        binding.buttonLogout.setOnClickListenerCheckConnection {
            context?.createAlertConfirmAppExit(::logout)
        }
    }

    private fun logout() {
        viewModel.disconnectCamera()
        startActivity(Intent(context, LoginX1Activity::class.java))
        activity?.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = LiveX1AppBarFragment::class.java.simpleName
    }
}
