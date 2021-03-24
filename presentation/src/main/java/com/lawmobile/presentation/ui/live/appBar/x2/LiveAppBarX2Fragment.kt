package com.lawmobile.presentation.ui.live.appBar.x2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.presentation.databinding.LiveViewAppBarMenuBinding
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment

class LiveAppBarX2Fragment : BaseFragment() {

    private var _liveAppBarMenuFragment: LiveViewAppBarMenuBinding? = null
    private val binding get() = _liveAppBarMenuFragment!!

    lateinit var onTapMenuButton: () -> Unit

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
    }

    private fun setListeners() {
        binding.buttonMenu.setOnClickListenerCheckConnection {
            onTapMenuButton()
        }
        binding.buttonNotification.setOnClickListenerCheckConnection {
            requireContext().createNotificationDialog(
                CameraEvent(
                    "StartedVideo",
                    EventType.NOTIFICATION,
                    EventTag.INFORMATION,
                    "Started video recording",
                    date = "20/10/2021"
                )
            ) {}
        }
    }

    companion object {
        val TAG = LiveAppBarX2Fragment::class.java.simpleName
    }
}
