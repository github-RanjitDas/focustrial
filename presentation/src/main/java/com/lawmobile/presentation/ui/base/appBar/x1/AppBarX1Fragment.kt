package com.lawmobile.presentation.ui.base.appBar.x1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.presentation.databinding.FragmentAppBarX1Binding
import com.lawmobile.presentation.extensions.setClickListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment

class AppBarX1Fragment : BaseFragment() {

    private val binding: FragmentAppBarX1Binding get() = _binding!!
    private var _binding: FragmentAppBarX1Binding? = null

    lateinit var onBackPressed: () -> Unit
    lateinit var onTapThumbnail: () -> Unit
    lateinit var onTapSimpleList: () -> Unit
    private lateinit var title: String
    private var showNavigationList: Boolean = false
    private var isViewCreated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppBarX1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        setListeners()
        isViewCreated = true
    }

    private fun configureView() {
        binding.textViewTitle.text = title
        binding.buttonSimpleList.isVisible = showNavigationList
        binding.buttonThumbnailList.isVisible = showNavigationList
    }

    private fun setListeners() {
        binding.buttonSimpleList.setClickListenerCheckConnection { onTapSimpleList() }
        binding.buttonThumbnailList.setClickListenerCheckConnection { onTapThumbnail() }
        binding.imageButtonBackArrow.setOnClickListenerCheckConnection { onBackPressed() }
    }

    fun isSimpleListActivity(isActive: Boolean) {
        if (!isViewCreated) return
        binding.buttonSimpleList.isActivated = isActive
        binding.buttonThumbnailList.isActivated = !isActive
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = AppBarX1Fragment::class.java.simpleName
        fun createInstance(title: String, showNavigationList: Boolean = false): AppBarX1Fragment {
            return AppBarX1Fragment().apply {
                this.title = title
                this.showNavigationList = showNavigationList
            }
        }
    }
}
