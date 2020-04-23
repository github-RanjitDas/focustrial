package com.lawmobile.presentation.ui.base

import dagger.android.support.DaggerAppCompatActivity

open class BaseActivity : DaggerAppCompatActivity() {

    companion object {
        const val PERMISSION_FOR_LOCATION = 100
    }
}