/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ephemeris

import fr.lehautcambara.astronomicon.angle360
import java.util.Locale
import kotlin.math.truncate

data class Dms(val degrees: Int, val minutes: Int, val seconds: Int) {

    override fun toString() = String.format(Locale.getDefault(), "%3d\u00B0 %2d\' %2d\"", degrees, minutes, seconds)


    fun toDecimalDegrees(): Double = degrees.toDouble() + minutes/60.0 + seconds/3600.0

    fun toHms() : Hms = Hms.fromDecimalDegrees(toDecimalDegrees())

    companion object {
        fun fromDecimalDegrees(degrees: Double) : Dms {
            val deg = truncate(angle360(degrees))
            val min = (angle360(degrees) - deg) * 60.0
            val secs = (min - truncate(min)) * 60.0
            return Dms(deg.toInt(), min.toInt(), secs.toInt())
        }
    }
}