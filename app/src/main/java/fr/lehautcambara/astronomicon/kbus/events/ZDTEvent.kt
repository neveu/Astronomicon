package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent
import java.time.ZonedDateTime

data class ZDTEvent(val zdt: ZonedDateTime) : BusEvent() {

}
