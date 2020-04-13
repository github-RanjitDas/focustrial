package com.safefleet.lawmobile.di

import android.app.Application
import com.safefleet.lawmobile.BaseApplication
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideApplication(application: BaseApplication): Application = application
    }
}