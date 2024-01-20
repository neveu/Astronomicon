package fr.lehautcambara.astronomicon.keplerianElements

import java.util.Calendar


data class Coords(val x: Double, val y: Double, val z: Double)

abstract class Ephemeris {
    abstract fun eclipticCoords(dateTime: Calendar): Coords

    abstract fun equatorialCoords(dateTime: Calendar): Coords
}