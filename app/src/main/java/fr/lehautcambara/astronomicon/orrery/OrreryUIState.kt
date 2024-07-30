package fr.lehautcambara.astronomicon.orrery

import angled
import fr.lehautcambara.astronomicon.astrology.Aspect
import fr.lehautcambara.astronomicon.astrology.AspectType
import fr.lehautcambara.astronomicon.astrology.convertToJulianCentury
import fr.lehautcambara.astronomicon.astrology.ephemerides
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

    private var _aspectDescription: String = "Aspects"
    val aspectDescription: String
        get() = _aspectDescription

    val geocentricPlanets: ArrayList<Ephemeris> = arrayListOf<Ephemeris>(Sun, Mercury, Venus, Mars, Jupiter, Saturn, Moon)


    fun aspectEphemerisPairs(): MutableList<Pair<Ephemeris, Ephemeris>> {
        val pairs = mutableListOf<Pair<Ephemeris, Ephemeris>>()
        for(i in 0..geocentricPlanets.size -2)
            for(j in i+1..geocentricPlanets.size-1) {
                pairs.add(Pair(geocentricPlanets[i],geocentricPlanets[j]))
            }
        return pairs
    }

    fun aspects( zdt:ZonedDateTime): List<Aspect> {
        return aspectEphemerisPairs()
            .map{pair: Pair<Ephemeris, Ephemeris> ->
                ephemerisPairToAspect(pair.first, pair.second, zdt)
            }
            .filterNotNull()
    }

    fun aspectRange(center: Double, error: Double = 2.5): ClosedFloatingPointRange<Double> {
        return ((center - error)..(center + error))
    }

    fun ephemerisPairToAspect(b1: Ephemeris, b2: Ephemeris, zdt: ZonedDateTime) : Aspect? {
        val angle = aspectAngle(ephemerides["Earth"], b1,b2, zdt)
        return when(angle) {
            in aspectRange(0.0) -> Aspect(AspectType.Conjunction, zdt, b1,b2, angle)
            in aspectRange(30.0) -> Aspect(AspectType.SemiSextile, zdt, b1,b2, angle)
            in aspectRange(45.0) -> Aspect(AspectType.Octile, zdt, b1,b2, angle)
            in aspectRange(60.0) -> Aspect(AspectType.Sextile, zdt, b1,b2, angle)
            in aspectRange(90.0) -> Aspect(AspectType.Square, zdt, b1,b2, angle)
            in aspectRange(120.0) -> Aspect(AspectType.Trine, zdt, b1,b2, angle)
            in aspectRange(135.0) -> Aspect(AspectType.Trioctile, zdt, b1,b2, angle)
            in aspectRange(180.0) ->  Aspect(AspectType.Opposition, zdt, b1,b2, angle)
            else -> null
        }
    }

    fun aspectAngle(center: Ephemeris?, from: Ephemeris?, to: Ephemeris?, zdt:ZonedDateTime): Double {
            return aspectAngle(center?.eclipticCoords(zdt), from?.eclipticCoords(zdt), to?.eclipticCoords(zdt))
    }
    fun aspectAngle(center: Coords?, from: Coords?, to: Coords?): Double {
        val a1 = abs(angled(center, from) - angled(center, to))
        val a180 = abs( if (a1 > 180) a1 - 360.0 else a1)
        return a180
    }

    override fun toString(): String {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy GG hh:mm:ss a "))
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

