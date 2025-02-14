/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.astrology.aspects

import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris.Companion.ephemerides
import java.time.ZonedDateTime
import kotlin.math.abs

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

    val name: String = aspectType.name
    val glyph: Int?
        get() = aspectType.glyph

}
fun aspects(zdt: ZonedDateTime): List<Aspect> {
    return aspectEphemerisPairs(AstrologicalPoints.geocentricPlanets)
        .map{pair: Pair<Ephemeris, Ephemeris> ->
            ephemerisPairToAspect(pair.first, pair.second, zdt)
        }
        .filterNotNull()
}
fun aspectEphemerisPairs(geocentricPlanets: ArrayList<Ephemeris>): MutableList<Pair<Ephemeris, Ephemeris>> {
    val pairs = mutableListOf<Pair<Ephemeris, Ephemeris>>()
    for(i in 0..geocentricPlanets.size -2)
        for(j in i+1..geocentricPlanets.size-1) {
            pairs.add(Pair(geocentricPlanets[i], geocentricPlanets[j]))
        }
    return pairs
}
fun aspectRange(center: Double, error: Double = 2.5): ClosedFloatingPointRange<Double> {
    return ((center - error)..(center + error))
}
fun aspectAngle(center: Ephemeris?, from: Ephemeris?, to: Ephemeris?, zdt:ZonedDateTime): Double {
    return aspectAngle(center?.eclipticCoords(zdt), from?.eclipticCoords(zdt), to?.eclipticCoords(zdt))
}
fun aspectAngle(center: Coords?, from: Coords?, to: Coords?): Double {
    center?.let {center ->
        val a1 = abs(center.angleTo( from) - center.angleTo(to))
        val a180 = abs( if (a1 > 180) a1 - 360.0 else a1)
        return a180
    }
    return 0.0
}
fun ephemerisPairToAspect(b1: Ephemeris, b2: Ephemeris, zdt: ZonedDateTime) : Aspect? {
    val angle = aspectAngle(ephemerides()["Earth"], b1,b2, zdt)
    return when(angle) {
        in aspectRange(0.0) -> Aspect(AspectType.Conjunction, zdt, b1,b2, angle)
        in aspectRange(30.0) -> Aspect(AspectType.SemiSextile, zdt, b1,b2, angle)
        in aspectRange(45.0) -> Aspect(AspectType.Octile, zdt, b1,b2, angle)
        in aspectRange(60.0) -> Aspect(AspectType.Sextile, zdt, b1,b2, angle)
        in aspectRange(90.0) -> Aspect(AspectType.Square, zdt, b1,b2, angle)
        in aspectRange(120.0) -> Aspect(AspectType.Trine, zdt, b1,b2, angle)
        in aspectRange(135.0) -> Aspect(AspectType.Trioctile, zdt, b1,b2, angle)
        in aspectRange(180.0) ->  Aspect(AspectType.Opposition, zdt, b1,b2, angle)
        else -> null
    }
}