package com.lawmobile.presentation.ui.live.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveMenuX2Binding
import com.lawmobile.presentation.extensions.createAlertConfirmAppExit
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnSwipeRightListener
import com.lawmobile.presentation.extensions.setOnTouchListenerCheckConnection
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.ui.notificationList.NotificationListActivity
import com.lawmobile.presentation.utils.Constants

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
        setListeners()
        setTouchListeners()
        setInformationOfOfficer()
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

        binding.textViewSettings.setOnTouchListenerCheckConnection(
            {
                showToastNotSupportedYet()
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
                showToastNotSupportedYet()
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

    private fun startNotificationListActivity() {
        startActivity(Intent(requireContext(), NotificationListActivity::class.java))
    }

    private fun startFileListActivity(fileType: String) {
        (activity as BaseActivity).updateLiveOrPlaybackActive(false)
        val fileListIntent = Intent(activity, FileListActivity::class.java)
        fileListIntent.putExtra(Constants.FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
    }

    private fun startHelpActivity() {
        val intent = Intent(activity, HelpPageActivity::class.java)
        startActivity(intent)
    }

    private fun logoutApplication() {
        liveMenuViewModel.disconnectCamera()
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    private fun showToastNotSupportedYet() {
        activity?.showToast(
            getString(R.string.live_view_menu_feature_not_supported),
            Toast.LENGTH_LONG
        )
    }

    companion object {
        fun createInstance(closeFragment: () -> Unit): LiveMenuFragment {
            return LiveMenuFragment().apply {
                this.onCloseMenuButton = closeFragment
            }
        }

        val TAG = LiveMenuFragment::class.java.simpleName
    }
}
