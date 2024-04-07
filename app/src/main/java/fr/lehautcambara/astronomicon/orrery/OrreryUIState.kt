package fr.lehautcambara.astronomicon.orrery

import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.convertToJulianCentury
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class OrreryUIState (val zonedDateTime: ZonedDateTime = ZonedDateTime.now()) {
//    constructor( dateTime: GregorianCalendar) : this()

    override fun toString(): String {
       // return SimpleDateFormat("dd-MMM-yyyy").format()
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy GG"))
    }

    private val Mercury =  SolarEphemeris( KeplerianElements.Mercury())
    private val Venus = SolarEphemeris( KeplerianElements.Venus())
    private val Earth  = SolarEphemeris( KeplerianElements.EmBary())
    private val Mars = SolarEphemeris( KeplerianElements.Mars())
    private val Jupiter = SolarEphemeris( KeplerianElements.Jupiter())
    private val Saturn  = SolarEphemeris( KeplerianElements.Saturn())
    private val Sun = SolarEphemeris( KeplerianElements.Sun())
    private val Moon  = LunarEphemeris()

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

    private var _mars = Mars.eclipticCoords(julianCentury)
    val mars: Coords
        get() = _mars

    private var _jupiter = Jupiter.eclipticCoords(julianCentury)
    val jupiter: Coords
        get() = _jupiter

    private var _saturn = Saturn.eclipticCoords(julianCentury)
    val saturn: Coords
        get() = _saturn

}

