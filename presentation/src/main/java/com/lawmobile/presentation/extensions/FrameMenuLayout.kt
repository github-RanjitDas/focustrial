package com.lawmobile.presentation.extensions

import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.MenuInformation

fun FrameLayout.openMenuButton(menuInformation: MenuInformation) {
    this.isVisible = true
    menuInformation.shadowOpenMenuView?.isVisible = true
    val animation = AnimationUtils.loadAnimation(menuInformation.context, R.anim.slide_in_right)
    this.startAnimation(animation)
    menuInformation.menuFragment.openMenu()
}

fun FrameLayout.closeMenuButton(menuInformation: MenuInformation) {
    val animation = AnimationUtils.loadAnimation(menuInformation.context, R.anim.slide_out_right)
    val animationShadow = AnimationUtils.loadAnimation(menuInformation.context, R.anim.fade_out)
    this.startAnimation(animation)
    menuInformation.shadowOpenMenuView?.startAnimation(animationShadow)
    this.isVisible = false
    menuInformation.shadowOpenMenuView?.isVisible = false
}
