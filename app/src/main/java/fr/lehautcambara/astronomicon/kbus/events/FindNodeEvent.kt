/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent
import java.time.ZonedDateTime

data class FindNodeEvent(val body: String, val zdt: ZonedDateTime): BusEvent() {

}
