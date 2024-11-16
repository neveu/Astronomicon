/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ephemeris

import java.time.ZonedDateTime
import java.util.Calendar

class Node(val body: Ephemeris): Ephemeris() {
    override fun eclipticCoords(dateTime: Calendar): Coords {
        return body.eclipticCoords(dateTime)
    }

    override fun eclipticCoords(zdt: ZonedDateTime): Coords {
        return body.eclipticCoords(zdt)
    }

    override fun eclipticCoords(julianCentury: Double): Coords {
        return body.eclipticCoords(julianCentury)
    }

    override fun equatorialCoords(dateTime: Calendar): Coords {
        return body.equatorialCoords(dateTime)
    }

    override fun equatorialCoords(julianCentury: Double): Coords {
        return body.equatorialCoords(julianCentury)
    }

    override fun toString(): String {
        return "$body Node"
    }
}