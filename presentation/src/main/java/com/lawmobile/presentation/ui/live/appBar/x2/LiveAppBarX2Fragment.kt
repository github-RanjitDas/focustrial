package com.lawmobile.presentation.ui.live.appBar.x2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.presentation.databinding.LiveViewAppBarMenuBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment

class LiveAppBarX2Fragment : BaseFragment() {

    private var _liveAppBaeMenuFragment: LiveViewAppBarMenuBinding? = null
    private val binding get() = _liveAppBaeMenuFragment!!

    lateinit var onTapMenuButton: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _liveAppBaeMenuFragment =
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
    }

    companion object {
        val TAG = LiveAppBarX2Fragment::class.java.simpleName
        fun createInstance(tapInMenuButton: () -> Unit): LiveAppBarX2Fragment {
            return LiveAppBarX2Fragment().apply {
                this.onTapMenuButton = tapInMenuButton
            }
        }
    }
}
