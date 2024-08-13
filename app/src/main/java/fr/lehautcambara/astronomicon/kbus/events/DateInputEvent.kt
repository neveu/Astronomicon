/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved. 
 */

package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent

data class DateInputEvent(val show: Boolean) : BusEvent() {

}
