package com.lawmobile.presentation.ui.base.appBar.x2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.databinding.FragmentAppBarX2Binding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.notificationList.NotificationListActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AppBarX2Fragment : BaseFragment() {

    private var _binding: FragmentAppBarX2Binding? = null
    private val binding get() = _binding!!

    lateinit var onTapMenuButton: () -> Unit

    private val viewModel: AppBarX2ViewModel by viewModels()

    private var isLogoActive: Boolean = false
    private var isBellIconActive: Boolean = true
    private var title: String = ""

    lateinit var onBackPressed: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppBarX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        configureView()
    }

    override fun onResume() {
        super.onResume()
        setCurrentNotificationCount()
    }

    private fun setCurrentNotificationCount() = with(binding) {
        textPendingNotification.isVisible = CameraInfo.currentNotificationCount > 0
        textPendingNotification.text = CameraInfo.currentNotificationCount.toString()
    }

    private fun configureView() = with(binding) {
        textViewTitle.text = title
        imageViewLogo.isVisible = isLogoActive
        constrainAppBarTitle.isVisible = !isLogoActive
        buttonNotification.isVisible = isBellIconActive
    }

    private fun setListeners() {
        menuButtonListener()
        notificationIconListener()
        backButtonListener()
    }

    private fun menuButtonListener() {
        binding.buttonMenu.setOnClickListenerCheckConnection { onTapMenuButton() }
    }

    private fun notificationIconListener() {
        binding.buttonNotification.setOnClickListenerCheckConnection {
            if (activity !is NotificationListActivity) {
                val intent = Intent(context, NotificationListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun backButtonListener() {
        binding.imageButtonBackArrow.setOnClickListenerCheckConnection { onBackPressed() }
    }

    private fun setObservers() {
        viewModel.pendingNotificationCountResult.observe(
            viewLifecycleOwner,
            ::reviewPendingNotifications
        )
    }

    suspend fun getUnreadNotificationCount() {
        viewModel.getUnreadNotificationCount()
    }

    private fun reviewPendingNotifications(result: Result<Int>) {
        result.doIfSuccess {
            CameraInfo.currentNotificationCount = it
            setCurrentNotificationCount()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = AppBarX2Fragment::class.java.simpleName
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
