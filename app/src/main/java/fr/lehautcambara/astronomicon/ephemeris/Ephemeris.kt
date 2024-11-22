package fr.lehautcambara.astronomicon.ephemeris

import fr.lehautcambara.astronomicon.astrology.convertToJulianCentury
import java.time.ZonedDateTime
import java.util.Calendar
import kotlin.math.sign


abstract class Ephemeris {
    abstract fun eclipticCoords(dateTime: Calendar): Coords
    abstract fun eclipticCoords(zdt: ZonedDateTime): Coords

    abstract fun equatorialCoords(dateTime: Calendar): Coords


    abstract fun eclipticCoords(julianCentury: Double): Coords
    abstract fun equatorialCoords(julianCentury: Double): Coords


    abstract override fun toString(): String

    fun apparentAngularVelocity(toPlanet: Ephemeris, zdt: ZonedDateTime): Double {
        val planetCoords = toPlanet.eclipticCoords(zdt.convertToJulianCentury())
        val fromCoords = this.eclipticCoords(zdt.convertToJulianCentury())
        val deltaT = zdt.plusHours(1)
        val currentAngle = fromCoords.angleTo(planetCoords)
        val deltaJ = deltaT.convertToJulianCentury()
        val deltaEarth = this.eclipticCoords(deltaJ)
        val deltaPlanet = toPlanet.eclipticCoords(deltaJ)
        val deltaAngle = deltaEarth.angleTo(deltaPlanet)
        val av = deltaAngle - currentAngle
        return av
    }

    fun inRetrograde(toEphemeris: Ephemeris, zdt: ZonedDateTime) : Boolean = apparentAngularVelocity(toEphemeris, zdt) < 0.0

    fun nextNode(startTime: ZonedDateTime, onChange: (zdt: ZonedDateTime) -> Unit) : ZonedDateTime {
        var oldTime = startTime
        var newTime = oldTime.plusHours(1L)
        while (sign(eclipticCoords(oldTime).z) == sign(eclipticCoords(newTime).z)
            || sign(eclipticCoords(newTime).z) < 0
        ) {
            oldTime = newTime
            newTime = oldTime.plusHours(1L)
            onChange(newTime)
        }
        return oldTime
    }
}