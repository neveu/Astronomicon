package fr.lehautcambara.astronomicon.orrery

import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import java.util.GregorianCalendar
import kotlin.math.abs
import kotlin.math.atan2

class Orrery(var dateTime: GregorianCalendar = GregorianCalendar()) {

    private val mercury =  SolarEphemeris( KeplerianElements.Mercury())
    private val venus = SolarEphemeris( KeplerianElements.Venus())
    private val earth  = SolarEphemeris( KeplerianElements.EmBary())
    private val mars = SolarEphemeris( KeplerianElements.Mars())
    private val jupiter = SolarEphemeris( KeplerianElements.Jupiter())
    private val saturn  = SolarEphemeris( KeplerianElements.Saturn())
    private val sun= SolarEphemeris( KeplerianElements.Sun())
    private val moon  = LunarEphemeris()

    private val geocentricList = listOf<Ephemeris>(mercury, venus, moon, sun, mars, jupiter, saturn)
    private val heliocentricList = listOf<Ephemeris>(mercury, venus, earth, sun, mars, jupiter, saturn)


    fun fromTo(from: Ephemeris?, to: Ephemeris?): Coords? {
        if (from == null) return to?.eclipticCoords(dateTime)
        return if (to == null) -from.eclipticCoords(dateTime)
        else to.eclipticCoords(dateTime) - from.eclipticCoords(dateTime)
    }
    fun angle(x: Double, y: Double): Double {
        val tan = atan2(y, x)
        return tan * 180.0 / Math.PI
    }

    fun angle(from: Ephemeris?, to: Ephemeris?): Double {
        fromTo(from, to)?.apply {
            return angle(x, y)
        }
        return 0.0
    }

    fun aspectAngle(center: Ephemeris?, from: Ephemeris?, to: Ephemeris?): Double {
        return abs(angle(center, from) - angle(center, to))
    }
    fun aspect(angleFromTo: Double, aspectAngle: Double, error: Double): Boolean {
        return abs(aspectAngle - angleFromTo) < error
    }

}

private operator fun Coords.unaryMinus(): Coords? {
    return Coords(-x, -y, -z)
}

private operator fun Coords.minus(eclipticCoords: Coords): Coords {
    return Coords(
        x - eclipticCoords.x,
        y - eclipticCoords.y,
        z - eclipticCoords.z
    )
}
