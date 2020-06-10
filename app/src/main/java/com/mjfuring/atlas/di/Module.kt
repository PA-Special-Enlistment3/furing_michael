package com.mjfuring.atlas.di

import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mjfuring.atlas.R
import com.mjfuring.atlas.VmMain
import com.mjfuring.atlas.db.AppDatabase
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

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            androidApplication().baseContext.getString(R.string.app_name)
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                /* Executors.newSingleThreadExecutor().execute {
                     db.execSQL(DB_DEFAULT_VALUE)
                 }*/
            }
        }).fallbackToDestructiveMigration().build()
    }

    single {
        get<AppDatabase>().contactDAO()
    }

    single {
        get<AppDatabase>().requestDAO()
    }

    single {
        get<AppDatabase>().respondentDAO()
    }

    viewModel { VmSetup(get(), get()) }
    viewModel { VmMain(get(), get(), get()) }
}


//{cmd:1,lat:12.9794,lon:124.0271}
//{cmd:1,lat:12.9803,lon:124.0106}

