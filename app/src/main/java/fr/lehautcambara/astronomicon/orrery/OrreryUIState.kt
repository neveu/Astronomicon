package fr.lehautcambara.astronomicon.orrery

import angled
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.convertToJulianCentury
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sign
import kotlin.math.sqrt

enum class DisplayMode {
    Heliocentric,
    Geocentric,
    Ecliptic,
}
data class OrreryUIState (
    val zonedDateTime: ZonedDateTime = ZonedDateTime.now(),
    val displayMode: DisplayMode = DisplayMode.Heliocentric,

) {

    override fun toString(): String {
       // return SimpleDateFormat("dd-MMM-yyyy").format()
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy GG"))
    }

    val Mercury =  SolarEphemeris( KeplerianElements.Mercury())
    val Venus = SolarEphemeris( KeplerianElements.Venus())
    val Earth  = SolarEphemeris( KeplerianElements.EmBary())
    val Mars = SolarEphemeris( KeplerianElements.Mars())
    val Jupiter = SolarEphemeris( KeplerianElements.Jupiter())
    val Saturn  = SolarEphemeris( KeplerianElements.Saturn())
    val Sun = SolarEphemeris( KeplerianElements.Sun())
    val Moon  = LunarEphemeris()

    private var _julianCentury: Double = zonedDateTime.convertToJulianCentury()
    val julianCentury : Double
        get() = _julianCentury


    private var _sun = Sun.eclipticCoords(julianCentury)
    val sun: Coords
        get() = _sun

    private var _mercury = Mercury.eclipticCoords(julianCentury)
    val mercury: Coords
        get() = _mercury

    private var _venus = Venus.eclipticCoords(julianCentury)
     val venus: Coords
        get() = _venus

    private var _earth = Earth.eclipticCoords(julianCentury)
     val earth: Coords
        get() = _earth

    private var _moon = Moon.eclipticCoords(julianCentury)
     val moon: Coords
        get() = _moon

    fun moonPhase(numImages:Int) : Int {
        return Moon.phaseImageIndex(zonedDateTime, numImages)
    }

    private var _mars = Mars.eclipticCoords(julianCentury)
    val mars: Coords
        get() = _mars

    private var _jupiter = Jupiter.eclipticCoords(julianCentury)
    val jupiter: Coords
        get() = _jupiter

    private var _saturn = Saturn.eclipticCoords(julianCentury)
    val saturn: Coords
        get() = _saturn


    fun fromTo(from: Coords?, to: Coords?): Coords? {
        if (from == null) return to
        return if (to == null) -from
        else to - from
    }
    private fun angle(from: Coords?, to: Coords?) : Double {
        fromTo(from, to)?.apply {
            return angled(x, y)
        }
        return 0.0
    }

    fun aspectAngle(center: Coords?, from: Coords?, to: Coords?): Double {
        return abs(angle(center, from) - angle(center, to))
    }
    fun aspect(angleFromTo: Double, aspectAngle: Double, error: Double): Boolean {
        return abs(aspectAngle - angleFromTo) < error
    }
    fun elevation(x: Double, y: Double, z: Double): Double {
        // [x y z] . [x y 0]/|[x y 0]||[x y z]|
        return sign(z) * (180.0 / Math.PI) * acos(
            (x * x + y * y) / (sqrt(
                x * x + y * y
            ) * sqrt(x * x + y * y + z * z))
        )
    }

}

