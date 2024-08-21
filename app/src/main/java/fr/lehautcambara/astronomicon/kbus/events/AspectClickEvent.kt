/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved. 
 */

package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.astrology.aspects.Aspect
import fr.lehautcambara.astronomicon.kbus.BusEvent

data class AspectClickEvent(val aspect: Aspect) : BusEvent() {

}
