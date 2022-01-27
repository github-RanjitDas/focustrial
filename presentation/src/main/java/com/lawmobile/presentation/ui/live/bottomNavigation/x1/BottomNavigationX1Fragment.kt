package com.lawmobile.presentation.ui.live.bottomNavigation.x1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lawmobile.presentation.databinding.FragmentLiveNavigationX1Binding
import com.lawmobile.presentation.ui.live.bottomNavigation.BottomNavigationBaseFragment

class BottomNavigationX1Fragment : BottomNavigationBaseFragment() {

    private val binding: FragmentLiveNavigationX1Binding get() = _binding!!
    private var _binding: FragmentLiveNavigationX1Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveNavigationX1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setSharedListeners()
    }

    private fun setViews() {
        buttonSnapshotList = binding.buttonSnapshotList
        buttonVideoList = binding.buttonVideoList
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = BottomNavigationX1Fragment::class.java.simpleName
    }
}
