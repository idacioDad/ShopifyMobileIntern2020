package com.doilio.shopifymemory

import android.app.Application
import timber.log.Timber

class AppLogger : Application() {

    /**
     * Timber init
     */
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}