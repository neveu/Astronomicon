package fr.lehautcambara.astronomicon.ephemeris

import fr.lehautcambara.astronomicon.astrology.convertToJ2000Century
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import java.time.ZonedDateTime
import java.util.Calendar
import kotlin.math.sign


abstract class Ephemeris {
    abstract fun eclipticCoords(dateTime: Calendar, name: String? = null): Coords
    abstract fun eclipticCoords(zdt: ZonedDateTime, name: String? = null): Coords



    abstract fun eclipticCoords(julianCentury: Double, name: String? = null): Coords
    abstract fun equatorialCoords(julianCentury: Double, name: String? = null): Coords
    abstract fun equatorialCoords(dateTime: Calendar, name: String? = null): Coords


    abstract override fun toString(): String

    fun apparentAngularVelocity(toPlanet: Ephemeris, zdt: ZonedDateTime): Double {
        val planetCoords = toPlanet.eclipticCoords(zdt.convertToJ2000Century())
        val fromCoords = this.eclipticCoords(zdt.convertToJ2000Century())
        val deltaT = zdt.plusHours(1)
        val currentAngle = fromCoords.angleTo(planetCoords)
        val deltaJ = deltaT.convertToJ2000Century()
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

    companion object {
        fun ephemerides(): HashMap<String, Ephemeris> = hashMapOf<String, Ephemeris>(
            "Sun" to SolarEphemeris( KeplerianElements.Sun()),
            "Earth" to SolarEphemeris( KeplerianElements.EmBary()),
            "Mercury" to SolarEphemeris( KeplerianElements.Mercury()),
            "Moon" to  LunarEphemeris(),
            "Venus" to SolarEphemeris( KeplerianElements.Venus()),
            "Mars" to SolarEphemeris( KeplerianElements.Mars()),
            "Jupiter" to SolarEphemeris( KeplerianElements.Jupiter()),
            "Saturn" to SolarEphemeris( KeplerianElements.Saturn()),
            "Uranus" to SolarEphemeris( KeplerianElements.Uranus()),
            "Neptune" to SolarEphemeris( KeplerianElements.Neptune()),
            "Pluto" to SolarEphemeris( KeplerianElements.Pluto()),
            )
        val obliquityJ2000 = 23.43928
    }
}