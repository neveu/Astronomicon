/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.orrery.graphics

import fr.lehautcambara.astronomicon.R

val defaultLunarPhaseArray: List<Int> = listOf(
    R.drawable.moon00,
    R.drawable.moon01,
    R.drawable.moon02,
    R.drawable.moon03,
    R.drawable.moon04,
    R.drawable.moon05,
    R.drawable.moon06,
    R.drawable.moon07,
    R.drawable.moon08,
    R.drawable.moon09,
    R.drawable.moon10,
    R.drawable.moon11,
    R.drawable.moon12,
    R.drawable.moon13,
    R.drawable.moon14,
    R.drawable.moon15,
    R.drawable.moon16,
    R.drawable.moon17,
    R.drawable.moon18,
    R.drawable.moon19,
    R.drawable.moon20,
    R.drawable.moon21,
    R.drawable.moon22,
    R.drawable.moon23,
    R.drawable.moon24,
    R.drawable.moon25,
    R.drawable.moon26,
    R.drawable.moon27,
    R.drawable.moon28,
    R.drawable.moon29,
)

val zodiacSignDrawables = listOf(
    R.drawable.aries,
    R.drawable.taurus,
    R.drawable.gemini,
    R.drawable.cancer,
    R.drawable.leo,
    R.drawable.virgo,
    R.drawable.libra,
    R.drawable.scorpio,
    R.drawable.sagitarius,
    R.drawable.capricorn,
    R.drawable.aquarius,
    R.drawable.pisces,
)

val defaultPlanetDrawables = hashMapOf<String, Int>(
    "Sun" to R.drawable.sun_anim40,
    "Earth" to R.drawable.earthcloudanimation,
    "Mercury" to R.drawable.mercuryanim141x141,
    "Moon" to  R.drawable.moon2,
    "Venus" to R.drawable.venusanim183x183,
    "Mars" to R.drawable.marsanim,
    "Jupiter" to R.drawable.jupiteranim225x225,
    "Saturn" to R.drawable.saturnanimprecess,

    )

val defaultPlanetSignDrawables = hashMapOf<String, Int> (
    "Sun" to R.drawable.sun_symbol2,
    "Earth" to R.drawable.earth_symbol,
    "Mercury" to R.drawable.mercury_symbol,
    "Moon" to  R.drawable.moon_symbol,
    "Venus" to R.drawable.venus_symbol,
    "Mars" to R.drawable.mars_symbol_shadow,
    "Jupiter" to R.drawable.jupiter_symbol,
    "Saturn" to R.drawable.saturn_symbol,
    "Uranus" to R.drawable.uranus_symbol,
    "Neptune" to R.drawable.neptune_symbol,
    "Pluto" to R.drawable.pluto_symbol
)

val defaultPlanetSignRetroSymbols = hashMapOf<String, Int> (
    "Sun" to R.drawable.sun_symbol2,
    "Earth" to R.drawable.earth_symbol,
    "Mercury" to R.drawable.mercury_retro_symbol,
    "Moon" to  R.drawable.moon_symbol,
    "Venus" to R.drawable.venus_retro_symbol,
    "Mars" to R.drawable.mars_retro_symbol,
    "Jupiter" to R.drawable.jupiter_retrograde_symbol,
    "Saturn" to R.drawable.saturn_retro_symbol,
    "Uranus" to R.drawable.uranus_retro_symbol,
    "Neptune" to R.drawable.neptune_retro_symbol,
)
