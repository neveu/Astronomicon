package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.astrology.Aspects.Aspect
import fr.lehautcambara.astronomicon.kbus.BusEvent

data class AspectsEvent(val aspects: List<Aspect>) : BusEvent() {

}
