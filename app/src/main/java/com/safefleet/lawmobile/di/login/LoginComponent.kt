package com.safefleet.lawmobile.di.login

import dagger.Component
import dagger.android.AndroidInjectionModule


@Component(modules = [LoginModule::class, AndroidInjectionModule::class])
interface LoginComponent