package com.mjfuring.atlas.di

import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mjfuring.atlas.setup.VmSetup
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

val appModule = module {

    single {
        PreferenceManager.getDefaultSharedPreferences(androidContext())
    }

    viewModel { VmSetup(get()) }

}


