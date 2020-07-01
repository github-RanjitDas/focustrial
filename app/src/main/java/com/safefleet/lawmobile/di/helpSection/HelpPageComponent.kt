package com.safefleet.lawmobile.di.helpSection

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [HelpPageModule::class, AndroidInjectionModule::class])
interface HelpPageComponent