package com.flatcode.littlemovieadmin

import android.app.Application
import android.text.format.DateFormat
import io.selimdawa.multicolors.MultiColorManager
import java.util.Calendar
import java.util.Locale

import com.flatcode.littlemovieadmin.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiColorManager.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", calendar).toString()
        }
    }
}