package com.safefleet.lawmobile.di

import com.lawmobile.presentation.di.PresentationComponent
import com.safefleet.lawmobile.BaseApplication
import com.safefleet.lawmobile.di.cameraService.CameraServiceModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilderModules::class,
        FragmentBuilderModules::class,
        CameraServiceModule::class
    ],
    dependencies = [PresentationComponent::class]
)
interface AppComponent : AndroidInjector<BaseApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: BaseApplication): Builder

        fun presentationComponent(presentationComponent: PresentationComponent): Builder

        fun build(): AppComponent
    }
}