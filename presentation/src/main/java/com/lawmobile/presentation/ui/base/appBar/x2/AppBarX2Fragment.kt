package com.lawmobile.presentation.ui.base.appBar.x2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.databinding.FragmentAppBarX2Binding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.notificationList.NotificationListActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppBarX2Fragment : BaseFragment() {

    private var _binding: FragmentAppBarX2Binding? = null
    private val binding get() = _binding!!

    lateinit var onTapMenuButton: () -> Unit

    private val viewModel: AppBarX2ViewModel by viewModels()

    private var isLogoActive: Boolean = false
    private var callPendingNotification: Boolean = false
    private var isBellIconActive: Boolean = true
    private var title: String = ""

    lateinit var onBackPressed: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        callPendingNotification = false
        _binding =
            FragmentAppBarX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isInPortraitMode()) {
            setListeners()
            setObservers()
            configureView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInPortraitMode()) setCurrentNotificationCount()
    }

    private fun setCurrentNotificationCount() {
        binding.textPendingNotification.isVisible = CameraInfo.currentNotificationCount > 0
        binding.textPendingNotification.text = CameraInfo.currentNotificationCount.toString()
    }

    private fun configureView() {
        binding.textViewTitle.text = title
        binding.imageViewLogo.isVisible = isLogoActive
        binding.constrainAppBarTitle.isVisible = !isLogoActive
        binding.buttonNotification.isVisible = isBellIconActive
    }

    private fun setListeners() {
        binding.buttonMenu.setOnClickListenerCheckConnection {
            onTapMenuButton()
        }

        binding.buttonNotification.setOnClickListenerCheckConnection {
            if (activity !is NotificationListActivity) {
                startActivity(Intent(requireContext(), NotificationListActivity::class.java))
            }
        }

        binding.imageButtonBackArrow.setOnClickListenerCheckConnection { onBackPressed() }

        CameraInfo.onReadyToGetNotifications = {
            if (!callPendingNotification && isInPortraitMode()) {
                callPendingNotification = true
                getPendingNotifications()
            } else {
                CameraInfo.onReadyToGetSettings?.invoke()
            }
        }
    }

    private fun setObservers() {
        viewModel.pendingNotificationCountResult.observe(
            viewLifecycleOwner,
            ::reviewPendingNotifications
        )
    }

    private fun getPendingNotifications() {
        lifecycleScope.launch {
            delay(500)
            viewModel.getPendingNotificationsCount()
        }
    }

    private fun reviewPendingNotifications(result: Result<Int>) {
        result.doIfSuccess {
            CameraInfo.currentNotificationCount = it
            setCurrentNotificationCount()
        }
        CameraInfo.onReadyToGetSettings?.invoke()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = AppBarX2Fragment::class.java.simpleName
        fun createInstance(
            isLogoActive: Boolean = false,
            title: String,
            isBellIconActive: Boolean = true
        ): AppBarX2Fragment {
            return AppBarX2Fragment().apply {
                this.isLogoActive = isLogoActive
                this.title = title
                this.isBellIconActive = isBellIconActive
            }
        }
    }
}
