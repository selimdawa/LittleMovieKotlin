package com.flatcode.littlemovie.Unit

import android.app.Application
import android.content.Context

object GetTimeAgo : Application() {
    private const val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS

    fun getTimeAgo(time: Long, ctx: Context?): String? {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        // TODO: localize
        val diff = now - time
        if (diff < MINUTE_MILLIS) {
            return "just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            return (diff / MINUTE_MILLIS).toString() + " minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            return (diff / HOUR_MILLIS).toString() + " hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday"
        } else {
            return (diff / DAY_MILLIS).toString() + " days ago"
        }
    }

    fun getMessageAgo(time: Long, context: Context?): String? {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        // TODO: localize
        val diff = now - time
        if (diff < MINUTE_MILLIS) {
            return "1 s"
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 m"
        } else if (diff < 50 * MINUTE_MILLIS) {
            return (diff / MINUTE_MILLIS).toString() + " m"
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 h"
        } else if (diff < 24 * HOUR_MILLIS) {
            return (diff / HOUR_MILLIS).toString() + " h"
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 d"
        } else {
            return (diff / DAY_MILLIS).toString() + " d"
        }
    }
}