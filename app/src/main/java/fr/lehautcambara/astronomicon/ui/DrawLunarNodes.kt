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
@Composable
fun DrawPlanetLunarNode(body: Ephemeris, size: Size, proportions: OrbitalProportions, coords: Coords?, planetGraphic: PlanetGraphic, modifier: Modifier) {
    coords?.apply {
        val a = angled(x,y)
        val elevation: Double = elevationd(x,y,z)
        val r = (size.width*proportions.lunarNodesOrbitRadiusScale) + (elevation * proportions.elevationScale)
        val pointerRadius = (size.width * proportions.pointerScale).toInt()
        DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
        DrawPlanetSymbol(body, r=r, a, size = size, proportions = proportions, modifier)
    }
}
@Composable
fun DrawAllLunarNodes(
    uiState: StateFlow<OrreryUIState>,
    size: Size,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    with(orreryUIState) {
        val dpValue = pixToDp(280.0 )
        val lunarNodeZdt: ZonedDateTime = AstrologicalPoints.Moon.nextNode(zonedDateTime) {}
        val earthCoords = AstrologicalPoints.Earth.eclipticCoords(lunarNodeZdt)
        val angle: Double = earthCoords.angleTo(AstrologicalPoints.Moon.eclipticCoords(lunarNodeZdt))
        Log.d("DrawAllEcliptic Earth - Moon Angle: ", angle.toString())
        Image(
            painterResource(id = R.drawable.draconis_1k_sat_center),
            contentDescription = "",
            modifier = modifier
                .size(dpValue)
                //.offset(x = -1.dp, y = -2.dp)
                .rotate(-angle.toFloat())
                .alpha(0.75F)
                .clickable { Kbus.post(PlanetClickEvent()) })


        DrawPlanetLunarNode(body = AstrologicalPoints.Sun, size, proportions,  coords = OrreryUIState.fromTo(earth, sun), planetGraphic,  modifier = modifier)
        DrawPlanetLunarNode(body = AstrologicalPoints.Moon, size, proportions, coords = OrreryUIState.fromTo(earth, moon),  planetGraphic,   modifier = modifier)
        DrawPlanet(body = AstrologicalPoints.Earth, r = 0.0, a = 0.0, size, proportions,  modifier = modifier)
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
fun PreviewDrawAllLunarNodes( backgroundID: Int = R.drawable.draconis_1k_annulus, modifier: Modifier = Modifier) {
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
        DrawAllLunarNodes(uiState = OrreryVM().uiState, size = size, modifier = Modifier.align(Alignment.Center))
    }
}
