package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.BusEvent

data class PlanetClickEvent(val body: Ephemeris? = null) : BusEvent() {

}
