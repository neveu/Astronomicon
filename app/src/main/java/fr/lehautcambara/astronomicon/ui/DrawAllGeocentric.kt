/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import java.time.ZonedDateTime

@Composable
fun DrawAllGeocentric(
    uiState: OrreryUIState,
    size: Size,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    with(uiState) {
        DrawPlanetAndOrbit(AstrologicalPoints.Moon, 1, size, proportions, coords = OrreryUIState.fromTo(earth, moon), modifier = modifier)

        var orbitColor: Color = orbitColor(
            AstrologicalPoints.Earth,
            AstrologicalPoints.Mercury, zonedDateTime)
        DrawPlanetAndOrbit(AstrologicalPoints.Mercury, 2, size, proportions, coords = OrreryUIState.fromTo(earth, mercury),  orbitColor = orbitColor, modifier = modifier)

        orbitColor = orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Venus, zonedDateTime)
        DrawPlanetAndOrbit(AstrologicalPoints.Venus, 3, size, proportions, coords = OrreryUIState.fromTo(earth, venus),  orbitColor = orbitColor, modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Sun, 4, size, proportions, coords = OrreryUIState.fromTo(earth, sun),  modifier = modifier)

        orbitColor = orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Mars, zonedDateTime)
        DrawPlanetAndOrbit(AstrologicalPoints.Mars, 5, size, proportions, coords = OrreryUIState.fromTo(earth, mars),  orbitColor = orbitColor, modifier = modifier)

        orbitColor = orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Jupiter, zonedDateTime)
        DrawPlanetAndOrbit(AstrologicalPoints.Jupiter, 6, size, proportions, coords = OrreryUIState.fromTo(earth, jupiter),  orbitColor = orbitColor, modifier = modifier)

        orbitColor = orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Saturn, zonedDateTime)
        val saturnScale = proportions.planetImageScale * 2
        DrawPlanetAndOrbit(AstrologicalPoints.Saturn, 7, size, proportions.copy(planetImageScale = saturnScale), coords = OrreryUIState.fromTo(earth, saturn),  orbitColor = orbitColor, modifier = modifier)
        DrawPlanetAndOrbit(AstrologicalPoints.Earth, 0, size, proportions, coords = earth,  modifier = modifier)
    }
}

fun orbitColor(fromPlanet: Ephemeris, planet: Ephemeris, zdt: ZonedDateTime) : Color =
    if (fromPlanet.apparentAngularVelocity(planet, zdt) < 0.0)
        Color.Red
    else Color.Black
