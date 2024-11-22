/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.orrery.OrreryUIState

@Composable
private fun DrawAllHeliocentric(
    uiState: OrreryUIState,
    size: Size,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    with(uiState) {
        DrawPlanetAndOrbit(AstrologicalPoints.Mercury, 1, size,   proportions, mercury,   modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Venus, 2,  size,  proportions, venus,  modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Earth, 3, size, proportions,  earth,   modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Mars, 4, size, proportions,   mars,   modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Jupiter, 5, size, proportions, jupiter,  modifier = modifier)
        val saturnScale = proportions.planetImageScale * 2
        DrawPlanetAndOrbit(AstrologicalPoints.Saturn, 6, size, proportions.copy(planetImageScale = saturnScale),  saturn, modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Sun, 0, size, proportions,  sun,  modifier = modifier)
    }
}
