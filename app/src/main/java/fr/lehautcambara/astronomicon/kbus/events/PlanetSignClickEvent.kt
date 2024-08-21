/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent
import fr.lehautcambara.astronomicon.ui.PlanetSignPolarCoords

data class PlanetSignClickEvent(val planet: PlanetSignPolarCoords) : BusEvent() {

}
