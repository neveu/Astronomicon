/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */


package fr.lehautcambara.astronomicon.astrology

import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements

class AstrologicalPoints {
    companion object {
        val Mercury =  SolarEphemeris( KeplerianElements.Mercury())
        val Venus = SolarEphemeris( KeplerianElements.Venus())
        val Earth  = SolarEphemeris( KeplerianElements.EmBary())
        val Mars = SolarEphemeris( KeplerianElements.Mars())
        val Jupiter = SolarEphemeris( KeplerianElements.Jupiter())
        val Saturn  = SolarEphemeris( KeplerianElements.Saturn())
        val Uranus  = SolarEphemeris( KeplerianElements.Uranus())
        val Neptune  = SolarEphemeris( KeplerianElements.Neptune())
        val Pluto  = SolarEphemeris( KeplerianElements.Pluto())

        val Sun = SolarEphemeris( KeplerianElements.Sun())
        val Moon  = LunarEphemeris()
        val geocentricPlanets: ArrayList<Ephemeris> = arrayListOf(Sun, Mercury, Venus, Mars, Jupiter, Saturn, Uranus, Neptune, Pluto,  Moon)


    }
}