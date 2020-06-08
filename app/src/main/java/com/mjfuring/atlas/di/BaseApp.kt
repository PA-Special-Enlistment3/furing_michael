package com.mjfuring.atlas.di

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.mjfuring.atlas.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) { Timber.plant(Timber.DebugTree()) }
        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)

        startKoin {
            androidContext(this@BaseApp)
            modules(listOf(appModule))
            androidFileProperties()
        }

    }

}




