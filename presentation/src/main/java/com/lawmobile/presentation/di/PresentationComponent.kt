package com.lawmobile.presentation.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication

@Component(
    modules = [PresentationModule::class,
        AndroidSupportInjectionModule::class]
)
interface PresentationComponent : AndroidInjector<DaggerApplication>