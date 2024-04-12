package fr.lehautcambara.astronomicon.kbus

import fr.lehautcambara.astronomicon.ephemeris.Ephemeris

data class PlanetClickEvent(val body: Ephemeris) : BusEvent() {

}
