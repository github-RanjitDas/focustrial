package com.lawmobile.presentation.ui.base

import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlin.system.exitProcess

open class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var baseViewModel: BaseViewModel

    fun killApp() {
        baseViewModel.deactivateCameraHotspot()
        finishAffinity()
        exitProcess(0)
    }

    companion object {
        const val PERMISSION_FOR_LOCATION = 100
    }
}