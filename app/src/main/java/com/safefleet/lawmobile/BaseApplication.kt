package com.safefleet.lawmobile

import com.lawmobile.presentation.di.DaggerPresentationComponent
import com.lawmobile.presentation.di.PresentationComponent
import com.safefleet.lawmobile.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<BaseApplication>? =
        DaggerAppComponent.builder()
            .presentationComponent(buildPresentationComponent())
            .application(this).build()

    private fun buildPresentationComponent(): PresentationComponent {
        return DaggerPresentationComponent.create()
    }
}
