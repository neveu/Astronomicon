package fr.lehautcambara.astronomicon.orrery

import angled
import fr.lehautcambara.astronomicon.astrology.convertToJulianCentury
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

enum class DisplayMode {
    Heliocentric {
        override fun scale(radialScroll: Float) = radialScroll/20.0
                 },
    Geocentric {
        override fun scale(radialScroll: Float) = radialScroll/20.0
    },
    Ecliptic{
        override fun scale(radialScroll: Float) = radialScroll/20.0
    },
    NatalChart{
        override fun scale(radialScroll: Float) = radialScroll/200.0
    };
    abstract fun scale(radialScroll: Float): Double
}

data class OrreryUIState (
    val zonedDateTime: ZonedDateTime = ZonedDateTime.now(),
    val displayMode: DisplayMode = DisplayMode.Heliocentric,
) {

    val Mercury =  SolarEphemeris( KeplerianElements.Mercury())
    val Venus = SolarEphemeris( KeplerianElements.Venus())
    val Earth  = SolarEphemeris( KeplerianElements.EmBary())
    val Mars = SolarEphemeris( KeplerianElements.Mars())
    val Jupiter = SolarEphemeris( KeplerianElements.Jupiter())
    val Saturn  = SolarEphemeris( KeplerianElements.Saturn())
    val Sun = SolarEphemeris( KeplerianElements.Sun())
    val Moon  = LunarEphemeris()

    fun <T, U> cartesianProduct(c1: Collection<T>, c2: Collection<U>): List<Pair<T, U>> {
        return c1.flatMap { lhsElem -> c2.map { rhsElem -> lhsElem to rhsElem } }
    }
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

    val geocentricPlanets: ArrayList<Ephemeris> = arrayListOf<Ephemeris>(Sun, Mercury, Venus, Mars, Jupiter, Saturn, Moon)
    // val aspectAnglePairs = cartesianProduct(geocentricPlanets, geocentricPlanets).filter { it: Pair<Ephemeris, Ephemeris> -> it.first != it.second}
    fun aspectAnglePairs(): MutableList<Pair<Ephemeris, Ephemeris>> {
        val pairs = mutableListOf<Pair<Ephemeris, Ephemeris>>()
        for(i in 0..geocentricPlanets.size -2)
            for(j in i+1..geocentricPlanets.size-1) {
                pairs.add(Pair(geocentricPlanets[i],geocentricPlanets[j]))
            }
        return pairs
    }


    val significantAspectPairs = aspectAnglePairs().map { it: Pair<Ephemeris,Ephemeris> ->
        Pair(it, aspectAngle(earth, it.first.eclipticCoords(julianCentury), it.second.eclipticCoords(julianCentury)))
    }.filter {it -> isSignificantAspect(it) }

    fun isSignificantAspect(anglePair: Pair<Pair<Ephemeris, Ephemeris>, Double>) : Boolean {
        return when(abs(anglePair.second)) {
            in 0.0F..1.0F -> true
            in 29F .. 31F -> true
            in 44F..46F -> true
            in 59F..61F -> true
            in 89F..91F -> true
            in 119F..121F -> true
            in 134F..136F -> true
            in 179F..180F -> true
            else -> false
        }
    }


    fun aspectAngle(center: Coords?, from: Coords?, to: Coords?): Double {
        return abs(angled(center, from) - angled(center, to))
    }
    override fun toString(): String {
        // return SimpleDateFormat("dd-MMM-yyyy").format()
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mmaa GG"))
    }

    companion object {
        fun fromTo(from: Coords?, to: Coords?): Coords? {
            if (from == null) return to
            return if (to == null) -from
            else to - from
        }
        fun angled(from: Coords?, to: Coords?) : Double {
            fromTo(from, to)?.apply {
                return angled(x, y)
            }
            return 0.0
        }

    }


}

