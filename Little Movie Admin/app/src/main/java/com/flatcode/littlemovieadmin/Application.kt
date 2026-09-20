package com.flatcode.littlemovieadmin

import android.app.Application
import android.text.format.DateFormat
import com.cloudinary.android.MediaManager
import com.flatcode.littlemovieadmin.utils.DATA
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

        val config = HashMap<String, String>()
        config["cloud_name"] = DATA.CLOUDINARY_CLOUD_NAME
        MediaManager.init(this, config)
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", calendar).toString()
        }
    }
}
