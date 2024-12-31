/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.angled
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.elevationd
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.orrery.PlanetGraphic
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime
import kotlin.math.roundToInt
@Composable
fun DrawPlanetEcliptic(body: Ephemeris, size: Size, proportions: OrbitalProportions, coords: Coords?, planetGraphic: PlanetGraphic, modifier: Modifier) {
    coords?.apply {
        val a = angled(x,y)
        val elevation: Double = elevationd(x,y,z)
        val r = (size.width*proportions.eclipticRadiusScale) + (elevation * proportions.elevationScale)
        val pointerRadius = (size.width * proportions.pointerScale).toInt()
        DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
        DrawPlanetSymbol(body, r=r, a, size = size, proportions = proportions, modifier)
    }
}
@Composable
fun DrawAllEcliptic(
    uiState: StateFlow<OrreryUIState>,
    background: Int = R.drawable.acsquare4,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    var size: Size by remember { mutableStateOf(Size.Zero) }

    Box(modifier = Modifier
        .paint(
            painterResource(id = background),
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

        with(orreryUIState) {
            val dpValue = pixToDp(360.0 * 0.95)
            val lunarNodeZdt: ZonedDateTime =
                AstrologicalPoints.Moon.nextNode(orreryUIState.zonedDateTime) {}
            val earthCoords = AstrologicalPoints.Earth.eclipticCoords(lunarNodeZdt)
            val angle: Double =
                earthCoords.angleTo(AstrologicalPoints.Moon.eclipticCoords(lunarNodeZdt))
            Log.d("DrawAllEcliptic Earth - Moon Angle: ", angle.toString())
            Image(
                painterResource(id = R.drawable.draconis_1k_sat_center),
                contentDescription = "",
                modifier = modifier
                    .size(dpValue)
                    .align(Alignment.Center)
                    .rotate(-angle.toFloat())
                    .alpha(0.5F)
                    .clickable { Kbus.post(PlanetClickEvent()) })

            DrawOrbit(
                radius = (size.width * proportions.eclipticRadiusScale).roundToInt(),
                color = Color.Red,
                stroke = 5F,
                modifier = modifier.align(Alignment.Center)
            )

            DrawPlanetEcliptic(
                body = AstrologicalPoints.Mercury,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, mercury),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)

            )
            DrawPlanetEcliptic(
                body = AstrologicalPoints.Venus,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, venus),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)
            )
            DrawPlanetEcliptic(
                body = AstrologicalPoints.Sun,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, sun),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)
            )
            DrawPlanetEcliptic(
                body = AstrologicalPoints.Mars,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, mars),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)
            )
            DrawPlanetEcliptic(
                body = AstrologicalPoints.Jupiter,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, jupiter),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)
            )
            val saturnScale =
                if (orreryUIState.planetGraphic == PlanetGraphic.Planet) proportions.planetImageScale * 2 else proportions.planetImageScale
            DrawPlanetEcliptic(
                body = AstrologicalPoints.Saturn,
                size,
                proportions.copy(planetImageScale = saturnScale),
                coords = OrreryUIState.fromTo(earth, saturn),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)

            )
            DrawPlanetEcliptic(
                body = AstrologicalPoints.Moon,
                size,
                proportions,
                coords = OrreryUIState.fromTo(earth, moon),
                planetGraphic,
                modifier = modifier.align(Alignment.Center)
            )
            DrawPlanet(
                body = AstrologicalPoints.Earth,
                r = 0.0,
                a = 0.0,
                size,
                proportions,
                modifier = modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun pixToDp(
    pixels: Double,
): Dp {
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = ((pixels * 2.0) / screenPixelDensity).dp
    return dpValue
}

@Preview
@Composable
fun PreviewDrawEcliptic() {
    DrawAllEcliptic(uiState = OrreryVM().uiState,  modifier = Modifier)

}
