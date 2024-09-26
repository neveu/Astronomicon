/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved. 
 */

package fr.lehautcambara.astronomicon.kbus.events

import android.location.Location
import fr.lehautcambara.astronomicon.kbus.BusEvent

data class LocationEvent(val location: Location) : BusEvent() {

}
