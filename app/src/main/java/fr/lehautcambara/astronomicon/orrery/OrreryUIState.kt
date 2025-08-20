package fr.lehautcambara.astronomicon.orrery

import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Earth
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Jupiter
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mars
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mercury
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Moon
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Neptune
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Pluto
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Saturn
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Sun
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Uranus
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Venus
import fr.lehautcambara.astronomicon.astrology.aspects.Aspect
import fr.lehautcambara.astronomicon.astrology.aspects.aspects
import fr.lehautcambara.astronomicon.astrology.convertToJ2000Century
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.orrery.graphics.defaultLunarPhaseArray
import fr.lehautcambara.astronomicon.orrery.graphics.defaultPlanetDrawables
import fr.lehautcambara.astronomicon.orrery.graphics.defaultPlanetSignDrawables
import fr.lehautcambara.astronomicon.orrery.graphics.defaultPlanetSignRetroSymbols
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
    SymbolRetro,
}

typealias PlanetMap = Map<String, Int>

data class OrreryUIState (
    val zonedDateTime: ZonedDateTime = ZonedDateTime.now(),
    val displayMode: DisplayMode = DisplayMode.Geocentric,
    val aspects: List<Aspect> = aspects(zonedDateTime),
    val showDateInput: Boolean = false,
    val proportions: NatalChartProportions = NatalChartProportions(),
    val updateTime: Boolean = true,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val background: Int = R.drawable.milkyway,
    val drawableMaps: Map<PlanetGraphic, PlanetMap> = hashMapOf(
        PlanetGraphic.Planet to defaultPlanetDrawables,
        PlanetGraphic.Symbol to defaultPlanetSignDrawables,
        PlanetGraphic.SymbolRetro to defaultPlanetSignRetroSymbols,
    ),
    val lunarPhaseArray: List<Int> = defaultLunarPhaseArray,
    val currentDrawable: Map<String, Int> = HashMap(defaultPlanetSignDrawables)
) {

    private var _julianCentury: Double = zonedDateTime.convertToJ2000Century()
    val julianCentury : Double
        get() = _julianCentury


    private var _sun = Sun.eclipticCoords(julianCentury, "Sun")
    val sun: Coords
        get() = _sun

    private var _mercury = Mercury.eclipticCoords(julianCentury, "Mercury")
    val mercury: Coords
        get() = _mercury

    private var _venus = Venus.eclipticCoords(julianCentury, "Venus")
     val venus: Coords
        get() = _venus

    private var _earth = Earth.eclipticCoords(julianCentury, "Earth")
     val earth: Coords
        get() = _earth

    private var _moon = Moon.eclipticCoords(julianCentury, "Moon")
     val moon: Coords
        get() = _moon

    fun moonPhase(numImages:Int) : Int {
        return Moon.phaseImageIndex(zonedDateTime, numImages)
    }

    private var _mars = Mars.eclipticCoords(julianCentury, "Mars")
    val mars: Coords
        get() = _mars

    private var _jupiter = Jupiter.eclipticCoords(julianCentury, "Jupiter")
    val jupiter: Coords
        get() = _jupiter

    private var _saturn = Saturn.eclipticCoords(julianCentury, "Saturn")
    val saturn: Coords
        get() = _saturn

    private var _uranus = Uranus.eclipticCoords(julianCentury, "Uranus")
    val uranus: Coords
        get() = _uranus

    private var _neptune = Neptune.eclipticCoords(julianCentury, "Neptune")
    val neptune: Coords
        get() = _neptune

    private var _pluto = Pluto.eclipticCoords(julianCentury, "Pluto")
    val pluto: Coords
        get() = _pluto


    private var _aspectDescription: String = "Aspects"
    val aspectDescription: String
        get() = _aspectDescription

    override fun toString(): String {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy GG hh:mm a "))
    }

    fun getDrawableByEphemeris(body: Ephemeris): Int? {
        val name = body.toString()
        val retrograde: Boolean = body.inRetrograde(Earth, zonedDateTime)
        return when(displayMode) {
            DisplayMode.NatalChart -> if (retrograde) defaultPlanetSignRetroSymbols[name] else defaultPlanetSignDrawables[name]
            DisplayMode.Geocentric -> getPlanetDrawables("Moon")[name]
            DisplayMode.Ecliptic -> if (retrograde) defaultPlanetSignRetroSymbols[name] else defaultPlanetSignDrawables[name]
            DisplayMode.LunarNodes -> if (retrograde) defaultPlanetSignRetroSymbols[name] else defaultPlanetSignDrawables[name]
            DisplayMode.Heliocentric -> getPlanetDrawables()[name]
        }
    }

    fun getPlanetDrawables(key: String? = null ) : Map<String, Int> {
        return  when(key) {
            null -> defaultPlanetDrawables
            "Moon" -> useMoonPhase()
            else -> defaultPlanetDrawables
        }
    }

    fun useMoonPhase(): Map<String, Int> {
        val planetmap = HashMap<String, Int>(defaultPlanetDrawables)
        planetmap["Moon"] = lunarPhaseImage()
        return planetmap
    }

    fun lunarPhaseImage() : Int {
        return defaultLunarPhaseArray[moonPhase((defaultLunarPhaseArray.size))]
    }


    companion object {
        fun fromTo(from: Coords?, to: Coords?): Coords? {
            if (from == null) return to
            return if (to == null) -from
            else to - from
        }
    }


}

