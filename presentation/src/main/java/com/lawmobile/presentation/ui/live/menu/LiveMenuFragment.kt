package com.lawmobile.presentation.ui.live.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraInfo.isOfficerLogged
import com.lawmobile.presentation.databinding.FragmentLiveMenuX2Binding
import com.lawmobile.presentation.extensions.createAlertConfirmAppExit
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnSwipeRightListener
import com.lawmobile.presentation.extensions.setOnTouchListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.bodyWornDiagnosis.BodyWornDiagnosisActivity
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.ui.notificationList.NotificationListActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class LiveMenuFragment : BaseFragment() {

    private var _fragmentPairingMenuLive: FragmentLiveMenuX2Binding? = null
    private val binding get() = _fragmentPairingMenuLive!!

    private val liveMenuViewModel: LiveMenuViewModel by viewModels()

    lateinit var onCloseMenuButton: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentPairingMenuLive =
            FragmentLiveMenuX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInformationOfOfficer()
        setObservers()
        setTouchListeners()
        setListeners()
    }

    private fun setInformationOfOfficer() {
        try {
            binding.textViewOfficerName.text =
                CameraInfo.officerName.split(" ")[0]
            binding.textViewOfficerLastName.text =
                CameraInfo.officerName.split(" ")[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setObservers() {
        liveMenuViewModel.pendingNotificationsCountResult.observe(viewLifecycleOwner, ::reviewPendingNotifications)
    }

    private fun reviewPendingNotifications(result: Result<Int>) {
        result.doIfSuccess {
            binding.textPendingNotification.isVisible = it > 0
            binding.textPendingNotification.text = it.toString()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListeners() {
        with(binding) {
            layoutMenu.setOnSwipeRightListener { onCloseMenuButton() }
            scrollViewMenu.setOnSwipeRightListener { onCloseMenuButton() }
            constrainInformationUser.setOnSwipeRightListener { onCloseMenuButton() }
            constrainLogout.setOnSwipeRightListener { onCloseMenuButton() }
        }
    }

    private fun setListeners() {

        binding.textViewSnapshots.setOnTouchListenerCheckConnection(
            {
                startFileListActivity(Constants.SNAPSHOT_LIST)
                onCloseMenuButton()
            },
            { onCloseMenuButton() }
        )

        binding.textViewVideos.setOnTouchListenerCheckConnection(
            {
                startFileListActivity(Constants.VIDEO_LIST)
                onCloseMenuButton()
            },
            { onCloseMenuButton() }
        )

        binding.textViewNotification.setOnTouchListenerCheckConnection(
            {
                startNotificationListActivity()
                onCloseMenuButton()
            },
            { onCloseMenuButton() }
        )

        binding.textViewDiagnose.setOnTouchListenerCheckConnection(
            {
                startBodyWornDiagnosisActivity()
                onCloseMenuButton()
            },
            { onCloseMenuButton() }
        )

        binding.textViewHelp.setOnTouchListenerCheckConnection(
            {
                startHelpActivity()
                onCloseMenuButton()
            },
            { onCloseMenuButton() }
        )

        binding.viewLogout.setOnClickListenerCheckConnection {
            requireActivity().createAlertConfirmAppExit(::logoutApplication)
            onCloseMenuButton()
        }

        binding.closeMenu.setOnClickListenerCheckConnection {
            this.onCloseMenuButton()
        }
    }

    private fun startFileListActivity(fileType: String) {
        (activity as BaseActivity).updateLiveOrPlaybackActive(false)
        val fileListIntent = Intent(requireActivity(), FileListActivity::class.java)
        fileListIntent.putExtra(Constants.FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
    }

    private fun startNotificationListActivity() {
        startActivity(Intent(requireContext(), NotificationListActivity::class.java))
    }

    private fun startBodyWornDiagnosisActivity() {
        startActivity(Intent(requireContext(), BodyWornDiagnosisActivity::class.java))
    }

    private fun startHelpActivity() {
        startActivity(Intent(requireActivity(), HelpPageActivity::class.java))
    }

    private fun logoutApplication() {
        liveMenuViewModel.disconnectCamera()
        isOfficerLogged = false
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }

    fun openMenu() {
        liveMenuViewModel.getPendingNotificationsCount()
    }

    companion object {
        val TAG = LiveMenuFragment::class.java.simpleName
    }
}
