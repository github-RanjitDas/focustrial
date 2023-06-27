package net.safefleet.focus.di

import net.safefleet.focus.BaseApplication
import net.safefleet.focus.di.cameraService.CameraServiceModule
import net.safefleet.focus.di.events.EventsModule
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
