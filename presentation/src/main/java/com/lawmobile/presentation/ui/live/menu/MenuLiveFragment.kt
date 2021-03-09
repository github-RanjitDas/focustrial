package com.lawmobile.presentation.ui.live.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.LiveViewMenuNavigationBinding
import com.lawmobile.presentation.extensions.createAlertConfirmAppExit
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.helpSection.HelpPageActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.Constants

class MenuLiveFragment : BaseFragment() {

    private var _fragmentPairingMenuLive: LiveViewMenuNavigationBinding? = null
    private val fragmentLiveMenuResultBinding get() = _fragmentPairingMenuLive!!

    private val menuLiveViewModel: MenuLiveViewModel by viewModels()

    lateinit var closeFragment: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentPairingMenuLive =
            LiveViewMenuNavigationBinding.inflate(inflater, container, false)
        return fragmentLiveMenuResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setInformationOfOfficer()
    }

    private fun setInformationOfOfficer() {
        try {
            fragmentLiveMenuResultBinding.textViewOfficerName.text =
                CameraInfo.officerName.split(" ")[0]
            fragmentLiveMenuResultBinding.textViewOfficerLastName.text =
                CameraInfo.officerName.split(" ")[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        fragmentLiveMenuResultBinding.textViewSnapshots.setOnClickListenerCheckConnection {
            startFileListIntent(Constants.SNAPSHOT_LIST)
        }

        fragmentLiveMenuResultBinding.textViewVideos.setOnClickListenerCheckConnection {
            startFileListIntent(Constants.VIDEO_LIST)
        }

        fragmentLiveMenuResultBinding.textViewSettings.setOnClickListenerCheckConnection {
            this.showToastNotSupportedYet()
        }

        fragmentLiveMenuResultBinding.textViewNotification.setOnClickListenerCheckConnection {
            this.showToastNotSupportedYet()
        }

        fragmentLiveMenuResultBinding.textViewDiagnose.setOnClickListenerCheckConnection {
            this.showToastNotSupportedYet()
        }

        fragmentLiveMenuResultBinding.textViewHelp.setOnClickListenerCheckConnection {
            this.showHelpView()
        }

        fragmentLiveMenuResultBinding.constrainLogout.setOnClickListenerCheckConnection {
            activity?.createAlertConfirmAppExit(::logoutApplication)
        }

        fragmentLiveMenuResultBinding.closeMenu.setOnClickListenerCheckConnection {
            this.closeFragment()
        }
    }

    private fun startFileListIntent(fileType: String) {
        (activity as BaseActivity).updateLiveOrPlaybackActive(false)
        val fileListIntent = Intent(activity, FileListActivity::class.java)
        fileListIntent.putExtra(Constants.FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
    }

    private fun showHelpView() {
        val intent = Intent(activity, HelpPageActivity::class.java)
        startActivity(intent)
    }

    private fun logoutApplication() {
        menuLiveViewModel.disconnectCamera()
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
        fun createInstance(closeFragment: () -> Unit): MenuLiveFragment {
            return MenuLiveFragment().apply {
                this.closeFragment = closeFragment
            }
        }
        val TAG = MenuLiveFragment::class.java.simpleName
    }
}
