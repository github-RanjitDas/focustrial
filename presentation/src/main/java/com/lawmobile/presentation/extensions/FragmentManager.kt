package com.lawmobile.presentation.extensions

import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.attachFragment(
    containerId: Int,
    fragment: Fragment,
    tag: String,
    isInStack: Boolean = false
) {
    val transaction = this.beginTransaction()
        .replace(containerId, fragment, tag)
    if (isInStack) transaction.addToBackStack(tag)
    transaction.commit()
}

fun FragmentManager.detachFragment(
    containerId: Int
) {
    findFragmentById(containerId)?.let {
        beginTransaction().remove(it).commit()
    }
}

fun FragmentManager.attachFragmentWithAnimation(
    containerId: Int,
    fragment: Fragment,
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
