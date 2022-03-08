package com.lawmobile.presentation.ui.base

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.NewRelicLogger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null
    abstract val viewTag: String?

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

    fun getApplicationVersionText(): String {
        return (activity as BaseActivity).getApplicationVersionText()
    }

    fun isInPortraitMode() =
        resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun onResume() {
        super.onResume()
        viewTag?.let {
            NewRelicLogger.updateActiveView(it)
        }
    }
}
