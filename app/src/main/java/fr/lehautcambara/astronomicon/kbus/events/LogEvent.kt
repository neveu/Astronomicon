package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent

data class LogEvent(val msg: String) : BusEvent() {

}
