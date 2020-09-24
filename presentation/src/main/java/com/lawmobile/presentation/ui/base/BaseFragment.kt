package com.lawmobile.presentation.ui.base

import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.utils.EspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null

    fun showFailedFoldersInLog(errors: ArrayList<String>) {
        CameraInfo.areNewChanges = true
        Log.d(getString(R.string.getting_files_error), errors.toString())
    }

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