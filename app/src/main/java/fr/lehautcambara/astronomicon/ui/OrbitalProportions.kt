/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

data class OrbitalProportions(
    val orbitIncrement: Int = 40,
    val pointerRadius: Int = 300,
    val orbitScale: Double = 0.045,
    val pointerScale: Double = 0.5,
    val planetImageScale: Double = 0.02,
    val eclipticRadiusScale: Double = 0.18,
    val elevationScale: Double = 20.0,
    val planetShadowScale: Double = 0.02,
) {


}