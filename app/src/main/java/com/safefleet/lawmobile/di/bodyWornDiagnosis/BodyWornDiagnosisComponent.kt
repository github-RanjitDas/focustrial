package com.safefleet.lawmobile.di.bodyWornDiagnosis

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [BodyWornDiagnosisModule::class])
interface BodyWornDiagnosisComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): BodyWornDiagnosisComponent
    }
}
