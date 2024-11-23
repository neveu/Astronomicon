/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import java.time.ZonedDateTime

@Composable
fun DrawAllGeocentric(
    uiState: OrreryUIState,
    backgroundID: Int = R.drawable.acsquare4,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = modifier
        .paint(
            painterResource(id = backgroundID),
            contentScale = ContentScale.FillWidth
        )
        .onGloballyPositioned { coordinates ->
            size = coordinates.size.toSize()
        }
        .pointerInput(Unit) {
            detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                Kbus.post(RadialScrollEvent(size, change.position, dragAmount))
            }
        }
    ) {
        with(uiState) {
            DrawPlanetAndOrbit(
                AstrologicalPoints.Moon,
                1,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, moon),
                modifier = modifier.align(Alignment.Center)
            )

            var orbitColor: Color = orbitColor(
                AstrologicalPoints.Earth,
                AstrologicalPoints.Mercury, zonedDateTime
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Mercury,
                2,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, mercury),
                orbitColor = orbitColor,
                modifier = modifier.align(Alignment.Center)
            )

            orbitColor =
                orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Venus, zonedDateTime)
            DrawPlanetAndOrbit(
                AstrologicalPoints.Venus,
                3,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, venus),
                orbitColor = orbitColor,
                modifier = modifier.align(Alignment.Center)
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Sun,
                4,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, sun),
                modifier = modifier.align(Alignment.Center)
            )

            orbitColor =
                orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Mars, zonedDateTime)
            DrawPlanetAndOrbit(
                AstrologicalPoints.Mars,
                5,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, mars),
                orbitColor = orbitColor,
                modifier = modifier.align(Alignment.Center)
            )

            orbitColor =
                orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Jupiter, zonedDateTime)
            DrawPlanetAndOrbit(
                AstrologicalPoints.Jupiter,
                6,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, jupiter),
                orbitColor = orbitColor,
                modifier = modifier.align(Alignment.Center)
            )

            orbitColor =
                orbitColor(AstrologicalPoints.Earth, AstrologicalPoints.Saturn, zonedDateTime)
            val saturnScale = proportions.planetImageScale * 2
            DrawPlanetAndOrbit(
                AstrologicalPoints.Saturn,
                7,
                size,
                proportions.copy(planetImageScale = saturnScale),
                coords = OrreryUIState.fromTo(earth, saturn),
                orbitColor = orbitColor,
                modifier = modifier.align(Alignment.Center)
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Earth,
                0,
                size,
                proportions,
                coords = earth,
                modifier = modifier.align(Alignment.Center)
            )
        }
    }
}

fun orbitColor(fromPlanet: Ephemeris, planet: Ephemeris, zdt: ZonedDateTime) : Color =
    if (fromPlanet.apparentAngularVelocity(planet, zdt) < 0.0)
        Color.Red
    else Color.Black

@Preview
@Composable
fun PreviewGeocentric() {
    DrawAllGeocentric(uiState = OrreryVM().uiState.value, modifier = Modifier)
}
