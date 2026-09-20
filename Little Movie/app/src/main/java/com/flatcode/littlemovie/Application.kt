package com.flatcode.littlemovie

import android.app.Application
import android.text.format.DateFormat
import com.cloudinary.android.MediaManager
import com.flatcode.littlemovie.utils.DATA
import dagger.hilt.android.HiltAndroidApp
import io.selimdawa.multicolors.MultiColorManager
import timber.log.Timber
import java.util.Calendar
import java.util.Locale

@HiltAndroidApp
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiColorManager.init(this)
        Timber.plant(Timber.DebugTree())

        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to DATA.CLOUDINARY_CLOUD_NAME,
            "secure" to true
        )
        try {
            MediaManager.init(this, config)
        } catch (e: Exception) {
            Timber.e(e, "Cloudinary initialization failed")
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