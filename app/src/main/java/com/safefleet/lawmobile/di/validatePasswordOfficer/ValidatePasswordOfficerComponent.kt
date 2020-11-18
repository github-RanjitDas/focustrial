package com.safefleet.lawmobile.di.validatePasswordOfficer

import dagger.Component
import dagger.android.AndroidInjectionModule


@Component(modules = [ValidatePasswordOfficerModule::class, AndroidInjectionModule::class])
interface ValidatePasswordOfficerComponent