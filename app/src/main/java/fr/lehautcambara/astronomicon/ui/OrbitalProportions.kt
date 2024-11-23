/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

data class OrbitalProportions(
    val orbitScale: Double = 0.045,
    val pointerScale: Double = 0.5,
    val planetImageScale: Double = 0.02,
    val planetSymbolScale: Double = 0.03,
    val planetSymbolShadowScale: Double = 0.03,
    val eclipticRadiusScale: Double = 0.18,
    val elevationScale: Double = 20.0,
    val planetShadowScale: Double = 0.02,
    val lunarNodesOrbitRadiusScale: Double = 0.26,
    val lunarNodesElevationScale: Double = 25.0,

    )