/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ephemeris

import fr.lehautcambara.astronomicon.angled
import kotlin.math.sqrt

data class Coords( val x: Double, val y: Double, val z: Double, val name: String? = null) {
    override fun toString(): String {
        return "${name?:""} : ($x, $y, $z)"
    }

    operator fun unaryMinus(): Coords {
        return Coords(-x, -y, -z)
    }

    operator fun minus(eclipticCoords: Coords): Coords {
        return Coords(
            x - eclipticCoords.x,
            y - eclipticCoords.y,
            z - eclipticCoords.z
        )
    }




    fun fromTo( to: Coords?): Coords {
        return if (to == null) -this
        else to - this
    }
    fun angleTo( to: Coords?) : Double {
        fromTo(to).apply {
            return angled(x, y)
        }
    }

    fun toPolar(): PolarCoords {
        return PolarCoords(sqrt(x*x + y*y), angled(x,y), name)
    }
}

data class PolarCoords (val r: Double, val a: Double, val name: String? = null)
