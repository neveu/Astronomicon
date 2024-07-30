package fr.lehautcambara.astronomicon.astrology

import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import java.time.ZonedDateTime

data class Aspect(
    val aspectType: AspectType,
    val zdt: ZonedDateTime,
    val body1: Ephemeris,
    val body2: Ephemeris,
    val orb: Double // actual angle between bodies
) {
    override fun toString(): String {
        return "${aspectType.name}: ${body1.toString()}-${body2.toString()} $orb deg"
    }
}