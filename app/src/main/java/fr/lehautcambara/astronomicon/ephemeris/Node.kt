/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ephemeris

import java.time.ZonedDateTime
import java.util.Calendar

class Node(val body: Ephemeris): Ephemeris() {
    override fun eclipticCoords(dateTime: Calendar, name: String?): Coords {
        return body.eclipticCoords(dateTime, name)
    }

    override fun eclipticCoords(zdt: ZonedDateTime, name: String?): Coords {
        return body.eclipticCoords(zdt, name)
    }

    override fun eclipticCoords(julianCentury: Double, name: String?): Coords {
        return body.eclipticCoords(julianCentury, name)
    }

    override fun equatorialCoords(dateTime: Calendar, name: String?): Coords {
        return body.equatorialCoords(dateTime, name)
    }

    override fun equatorialCoords(julianCentury: Double, name: String?): Coords {
        return body.equatorialCoords(julianCentury, name)
    }

    override fun toString(): String {
        return "$body Node"
    }
}