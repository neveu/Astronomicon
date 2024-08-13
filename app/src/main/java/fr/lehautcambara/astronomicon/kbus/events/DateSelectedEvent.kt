/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.events

import fr.lehautcambara.astronomicon.kbus.BusEvent
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

data class DateSelectedEvent(val longdate: Long?) : BusEvent() {

    val date: Date?
        get() {
            return longdate?.let {
                Date(it)
            }
        }

    val zdt: ZonedDateTime?
        get() {
            return longdate?.let {millis ->
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("GMT"))
            }
        }

}
