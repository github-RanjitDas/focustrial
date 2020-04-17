package com.lawmobile.presentation.extensions

import androidx.fragment.app.FragmentManager
import com.lawmobile.presentation.ui.base.BaseFragment

fun FragmentManager.attachFragment(containerId: Int, fragment: BaseFragment, tag: String, isInStack: Boolean = false) {
    val transaction = this.beginTransaction()
        .replace(containerId, fragment, tag)
    if (isInStack) transaction.addToBackStack(tag)
    transaction.commit()
}