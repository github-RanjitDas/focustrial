package com.lawmobile.presentation.ui.base

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.utils.EspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null

    fun showLoadingDialog() {
        EspressoIdlingResource.increment()
        loadingDialog = (activity as BaseActivity).createAlertProgress()
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
        EspressoIdlingResource.decrement()
    }

}