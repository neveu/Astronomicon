/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ephemeris

import java.util.Locale
import kotlin.math.truncate

data class Hms(val hours: Int, val minutes: Int, val seconds: Int) {
    fun toDecimalDegrees(): Double = hours * 15.0 + (minutes + seconds/60.0) * 0.25

    fun toDms() : Dms = Dms.fromDecimalDegrees(toDecimalDegrees())

    override fun toString(): String = String.format(Locale.getDefault(), "%2dh %2d\' %2d\"", hours, minutes, seconds)

    companion object {
        fun fromDecimalDegrees(degrees: Double) : Hms {
            val h = degrees/15.0
            val fractionOfHour = h - truncate(h)
            val m = fractionOfHour * 60.0
            val fractionOfMinute = m - truncate(m)
            val s = fractionOfMinute * 60.0
            return Hms(truncate(h).toInt(), truncate(m).toInt(), s.toInt())
        }
    }
}