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
import com.lawmobile.presentation.ui.fileList.shared.ListTypeButtons

class AppBarX1Fragment : BaseFragment(), ListTypeButtons {

    private val binding: FragmentAppBarX1Binding get() = _binding!!
    private var _binding: FragmentAppBarX1Binding? = null

    lateinit var onBackPressed: () -> Unit
    override lateinit var onThumbnailsClick: () -> Unit
    override lateinit var onSimpleClick: () -> Unit

    private var showNavigationList: Boolean = false

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
    }

    private fun configureView() {
        binding.textViewTitle.text = title
        binding.buttonSimpleList.isVisible = showNavigationList
        binding.buttonThumbnailList.isVisible = showNavigationList
    }

    private fun setListeners() {
        binding.buttonSimpleListListener()
        binding.buttonThumbnailListListener()
        binding.imageButtonBackArrow.setOnClickListenerCheckConnection { onBackPressed() }
    }

    private fun FragmentAppBarX1Binding.buttonThumbnailListListener() {
        buttonThumbnailList.setClickListenerCheckConnection {
            it.isActivated = true
            buttonSimpleList.isActivated = false
            onThumbnailsClick()
        }
    }

    private fun FragmentAppBarX1Binding.buttonSimpleListListener() {
        buttonSimpleList.setClickListenerCheckConnection {
            it.isActivated = true
            buttonThumbnailList.isActivated = false
            onSimpleClick()
        }
    }

    override fun toggleListType(isSimple: Boolean) {
        binding.buttonSimpleList.isActivated = isSimple
        binding.buttonThumbnailList.isActivated = !isSimple
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        private lateinit var title: String
        val TAG: String = AppBarX1Fragment::class.java.simpleName
        fun createInstance(title: String, showNavigationList: Boolean = false): AppBarX1Fragment {
            this.title = title
            return AppBarX1Fragment().apply {
                this.showNavigationList = showNavigationList
            }
        }
    }
}
