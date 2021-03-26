package com.safefleet.lawmobile.di

import com.safefleet.lawmobile.BaseApplication
import com.safefleet.lawmobile.di.cameraService.CameraServiceModule
import com.safefleet.lawmobile.di.events.EventsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        CameraServiceModule::class,
        EventsModule::class,
        AppActivitySubComponent::class,
        AppFragmentSubComponent::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: BaseApplication): AppComponent
    }
}
