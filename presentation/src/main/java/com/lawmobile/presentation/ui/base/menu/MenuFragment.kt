package com.lawmobile.presentation.ui.base.menu

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
import com.lawmobile.presentation.extensions.getIntentDependsCameraType
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnSwipeRightListener
import com.lawmobile.presentation.extensions.setOnTouchListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.bodyWornDiagnosis.BodyWornDiagnosisActivity
import com.lawmobile.presentation.ui.fileList.x1.FileListX1Activity
import com.lawmobile.presentation.ui.fileList.x2.FileListX2Activity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.live.x1.LiveX1Activity
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.ui.notificationList.NotificationListActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class MenuFragment : BaseFragment() {

    private var _binding: FragmentLiveMenuX2Binding? = null
    private val binding get() = _binding!!
    private val menuViewModel: MenuViewModel by viewModels()
    lateinit var onCloseMenuButton: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentLiveMenuX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOfficerInformation()
        setCurrentNotificationCount()
        setObservers()
        setTouchListeners()
        setListeners()
        binding.versionNumberTextMainMenu.text = getApplicationVersionText()
    }

    private fun setCurrentNotificationCount() {
        binding.textPendingNotification.isVisible = CameraInfo.currentNotificationCount > 0
        binding.textPendingNotification.text = CameraInfo.currentNotificationCount.toString()
    }

    private fun setOfficerInformation() {
        try {
            binding.textViewOfficerName.text = CameraInfo.officerName.split(" ")[0]
            binding.textViewOfficerLastName.text = CameraInfo.officerName.split(" ")[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setObservers() {
        menuViewModel.pendingNotificationsCountResult.observe(
            viewLifecycleOwner,
            ::reviewPendingNotifications
        )
    }

    private fun reviewPendingNotifications(result: Result<Int>) {
        result.doIfSuccess {
            CameraInfo.currentNotificationCount = it
            setCurrentNotificationCount()
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

        binding.textViewDashboard.setOnTouchListenerCheckConnection(
            {
                startMainScreen()
                onCloseMenuButton()
            },
            { onCloseMenuButton() }
        )

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

    private fun startMainScreen() {
        isInMainScreen = true
        if (activity is LiveX2Activity) return
        val intent =
            requireActivity().getIntentDependsCameraType(LiveX1Activity(), LiveX2Activity())
        startActivity(intent)
    }

    private fun startFileListActivity(fileType: String) {
        if (activity is FileListX2Activity && currentListView == fileType) return
        currentListView = fileType
        (activity as BaseActivity).updateLiveOrPlaybackActive(false)
        val fileListIntent =
            requireActivity().getIntentDependsCameraType(FileListX1Activity(), FileListX2Activity())
        fileListIntent.putExtra(Constants.FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
        if (!isInMainScreen) requireActivity().finish()
        isInMainScreen = false
    }

    private fun startNotificationListActivity() {
        if (activity is NotificationListActivity) return
        startActivity(Intent(requireContext(), NotificationListActivity::class.java))
        if (!isInMainScreen) requireActivity().finish()
        isInMainScreen = false
    }

    private fun startBodyWornDiagnosisActivity() {
        startActivity(Intent(requireContext(), BodyWornDiagnosisActivity::class.java))
    }

    private fun startHelpActivity() {
        startActivity(Intent(requireActivity(), HelpPageActivity::class.java))
    }

    private fun logoutApplication() {
        menuViewModel.disconnectCamera()
        isOfficerLogged = false
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }

    fun openMenu() {
        menuViewModel.getPendingNotificationsCount()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        var currentListView = ""
        var isInMainScreen = true
        val TAG = MenuFragment::class.java.simpleName
    }
}
