package com.lawmobile.presentation.ui.base.appBar.x2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lawmobile.presentation.databinding.LiveViewAppBarMenuBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.notificationList.NotificationListActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AppBarX2Fragment : BaseFragment() {

    private var _liveAppBarMenuFragment: LiveViewAppBarMenuBinding? = null
    private val binding get() = _liveAppBarMenuFragment!!

    lateinit var onTapMenuButton: () -> Unit

    private val viewModel: AppBarX2ViewModel by viewModels()

    private var isLogoActive: Boolean = false
    private var isBellIconActive: Boolean = true
    private lateinit var title: String

    lateinit var onBackPressed: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _liveAppBarMenuFragment =
            LiveViewAppBarMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        configureView()
    }

    private fun configureView() {
        binding.textViewTitle.text = title
        binding.imageView.isVisible = isLogoActive
        binding.constrainAppBarTitle.isVisible = !isLogoActive
        binding.buttonNotification.isVisible = isBellIconActive
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPendingNotificationsCount()
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
    }

    private fun setObservers() {
        viewModel.pendingNotificationCountResult.observe(
            viewLifecycleOwner,
            ::reviewPendingNotifications
        )
    }

    private fun reviewPendingNotifications(result: Result<Int>) {
        result.doIfSuccess {
            binding.textPendingNotification.isVisible = it > 0
            binding.textPendingNotification.text = it.toString()
        }
    }

    companion object {
        val TAG = AppBarX2Fragment::class.java.simpleName
        fun createInstance(isLogoActive: Boolean = false, title: String, isBellIconActive: Boolean = true): AppBarX2Fragment {
            return AppBarX2Fragment().apply {
                this.isLogoActive = isLogoActive
                this.title = title
                this.isBellIconActive = isBellIconActive
            }
        }
    }
}