package com.lawmobile.presentation.extensions

import androidx.annotation.AnimRes
import androidx.fragment.app.FragmentManager
import com.lawmobile.presentation.ui.base.BaseFragment

fun FragmentManager.attachFragment(
    containerId: Int,
    fragment: BaseFragment,
    tag: String,
    isInStack: Boolean = false
) {
    val transaction = this.beginTransaction()
        .replace(containerId, fragment, tag)
    if (isInStack) transaction.addToBackStack(tag)
    transaction.commit()
}

fun FragmentManager.attachFragmentWithAnimation(
    containerId: Int,
    fragment: BaseFragment,
    tag: String,
    isInStack: Boolean = false,
    @AnimRes animationIn: Int,
    @AnimRes animationOut: Int
) {
    val transaction = this.beginTransaction()
        .setCustomAnimations(animationIn, animationOut)
        .replace(containerId, fragment, tag)
    if (isInStack) transaction.addToBackStack(tag)
    transaction.commit()
}