/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent

data class DelayEvent(val delayedEvent: BusEvent, val delayTimeInMillis: Long = 1000): BusEvent()
