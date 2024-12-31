/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow


@Composable
fun DrawAllHeliocentric(
    uiState: StateFlow<OrreryUIState>,
    backgroundID: Int = R.drawable.acsquare4,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
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
        val m = modifier.align(Alignment.Center)
        with(orreryUIState) {
            DrawPlanetAndOrbit(
                AstrologicalPoints.Mercury,
                1,
                size,
                proportions,
                mercury,
                modifier = m
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Venus,
                2,
                size,
                proportions,
                venus,
                modifier = m
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Earth,
                3,
                size,
                proportions,
                earth,
                modifier = m
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Mars,
                4,
                size,
                proportions,
                mars,
                modifier = m
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Jupiter,
                5,
                size,
                proportions,
                jupiter,
                modifier = m
            )
            val saturnScale = proportions.planetImageScale * 2
            DrawPlanetAndOrbit(
                AstrologicalPoints.Saturn,
                6,
                size,
                proportions.copy(planetImageScale = saturnScale),
                saturn,
                modifier = m
            )
            DrawPlanetAndOrbit(
                AstrologicalPoints.Sun,
                0,
                size,
                proportions,
                sun,
                modifier = m
            )
        }
    }
}

@Preview
@Composable
fun PreviewHeliocentric() {
    DrawAllHeliocentric(uiState = OrreryVM().uiState, modifier = Modifier)
}
