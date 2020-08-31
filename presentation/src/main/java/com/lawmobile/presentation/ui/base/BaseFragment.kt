package com.lawmobile.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.utils.EspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    @Inject
    lateinit var baseViewModel: BaseViewModel

    private var loadingDialog: AlertDialog? = null
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseViewModel.isWaitFinishedLiveData.observe(viewLifecycleOwner, Observer(::handleTimeout))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun showLoadingDialog() {
        if (!isLoading) {
            EspressoIdlingResource.increment()
            isLoading = true
            baseViewModel.waitToFinish(BaseActivity.LOADING_TIMEOUT)
            loadingDialog = (activity as BaseActivity).createAlertProgress()
            loadingDialog?.show()
        }

    }

    fun hideLoadingDialog() {
        if (isLoading){
            isLoading = false
            baseViewModel.cancelWait()
            loadingDialog?.dismiss()
            loadingDialog = null
            EspressoIdlingResource.decrement()
        }
    }

    private fun handleTimeout(timedOut: Boolean) {
        if (timedOut && isLoading) {
            hideLoadingDialog()
            activity?.finish()
            activity?.showToast(getString(R.string.loading_files_error), Toast.LENGTH_SHORT)
        }
    }
}