package fr.lehautcambara.astronomicon.orrery

import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Earth
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Jupiter
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mars
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mercury
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Moon
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Saturn
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Sun
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Venus
import fr.lehautcambara.astronomicon.astrology.aspects.Aspect
import fr.lehautcambara.astronomicon.astrology.aspects.aspects
import fr.lehautcambara.astronomicon.astrology.convertToJulianCentury
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ui.NatalChartProportions
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class DisplayMode {
    NatalChart{
        override fun scale(radialScroll: Float) = radialScroll/200.0
    },
    Geocentric {
        override fun scale(radialScroll: Float) = radialScroll/20.0
    },
    Ecliptic {
        override fun scale(radialScroll: Float) = radialScroll/20.0
    },
    LunarNodes {
        override fun scale(radialScroll: Float): Double = radialScroll/20.0
    },
    Heliocentric {
        override fun scale(radialScroll: Float) = radialScroll/20.0
    },
    ;
    abstract fun scale(radialScroll: Float): Double
}

enum class PlanetGraphic {
    Symbol,
    Planet,
}

data class OrreryUIState (
    val zonedDateTime: ZonedDateTime = ZonedDateTime.now(),
    val displayMode: DisplayMode = DisplayMode.NatalChart,
    val aspects: List<Aspect> = aspects(zonedDateTime),
    val showDateInput: Boolean = false,
    val proportions: NatalChartProportions = NatalChartProportions(),
    val updateTime: Boolean = true,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val planetGraphic: PlanetGraphic = PlanetGraphic.Symbol
) {

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

    override fun toString(): String {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy GG hh:mm a "))
    }

    companion object {
        fun fromTo(from: Coords?, to: Coords?): Coords? {
            if (from == null) return to
            return if (to == null) -from
            else to - from
        }
    }


}

