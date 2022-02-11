package com.lawmobile.presentation.extensions

import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.MenuInformation

fun FrameLayout.openMenuButton(menuInformation: MenuInformation) {
    isVisible = true
    menuInformation.shadowOpenMenuView?.isVisible = true
    val animation = AnimationUtils.loadAnimation(menuInformation.context, R.anim.slide_in_right)
    startAnimation(animation)
    menuInformation.menuFragment.updateNotificationCount()
}

fun FrameLayout.closeMenuButton(menuInformation: MenuInformation) {
    val animation = AnimationUtils.loadAnimation(menuInformation.context, R.anim.slide_out_right)
    val animationShadow = AnimationUtils.loadAnimation(menuInformation.context, R.anim.fade_out)
    startAnimation(animation)
    menuInformation.shadowOpenMenuView?.startAnimation(animationShadow)
    isVisible = false
    menuInformation.shadowOpenMenuView?.isVisible = false
}
