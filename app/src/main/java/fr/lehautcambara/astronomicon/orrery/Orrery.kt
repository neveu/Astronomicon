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

     val mercury =  SolarEphemeris( KeplerianElements.Mercury())
     val venus = SolarEphemeris( KeplerianElements.Venus())
     val earth  = SolarEphemeris( KeplerianElements.EmBary())
     val mars = SolarEphemeris( KeplerianElements.Mars())
     val jupiter = SolarEphemeris( KeplerianElements.Jupiter())
     val saturn  = SolarEphemeris( KeplerianElements.Saturn())
     val sun= SolarEphemeris( KeplerianElements.Sun())
     val moon  = LunarEphemeris()

    val heliocentricList = listOf<Ephemeris>( sun,mercury, venus, earth, mars, jupiter, saturn)
    val geocentricList = listOf<Ephemeris>(earth, moon, mercury, venus, sun, mars, jupiter, saturn)


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
