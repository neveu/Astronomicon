/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.events.PlanetSignClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.sind
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Composable
fun pixToDp(
    pixels: Double,
): Dp {
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = ((pixels * 2.0) / screenPixelDensity).dp
    return dpValue
}

@Composable
fun DrawPlanet(polarCoords: PlanetSignPolarCoords?, sizeDp: Dp, planetSymbolDrawable: Int?, modifier: Modifier = Modifier) {
    planetSymbolDrawable?.let { id ->
        polarCoords?.let { coords ->
            DrawPlanet(id, coords.planet, sizeDp, coords.offset(), modifier = modifier) {
                Kbus.post(PlanetSignClickEvent(coords))
            }
        }
    }
}

@Composable
fun DrawPlanet(body: Ephemeris, uiState: StateFlow<OrreryUIState>, r: Double, a: Double, sizeInPx: Size, proportions: OrbitalProportions, modifier: Modifier) {
    val pointerRadius = (sizeInPx.width * proportions.pointerScale).toInt()
    if (r>0.0) DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r* cosd(a)).roundToInt()
    val y = (r* sind(a)).roundToInt()
    DrawPlanet(body, uiState, x, y, sizeInPx, proportions, modifier)
}

@Composable
fun DrawPlanet(body: Ephemeris, uiState: StateFlow<OrreryUIState>, x: Int, y: Int, sizeInPx: Size, proportions: OrbitalProportions, modifier: Modifier) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    val id: Int? = orreryUIState.getDrawableByEphemeris(body)
    val shadowSize = pixToDp(sizeInPx.width * proportions.planetShadowScale)
    val planetSize = pixToDp(sizeInPx.width * proportions.planetImageScale)

    id?.let {id ->
        DrawPlanet(id, "shadow", shadowSize, IntOffset(x + 20, -(y - 20)), blur = 2.dp, alpha = 0.3F, modifier)
        DrawPlanet(id, body.toString(), planetSize, IntOffset(x, -y), modifier = modifier ){
            Kbus.post(PlanetClickEvent(body))
        }
    }
}


@Composable
fun DrawPlanet(id: Int, planet: String, sizeDp: Dp, offset: IntOffset, blur: Dp = 0.dp, alpha: Float = 1.0f, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val painter = rememberDrawablePainter(drawable = getDrawable(LocalContext.current, id))
    Image(painter = painter, planet,
        modifier = modifier
            .absoluteOffset { offset }
            .size(sizeDp)
            .blur(blur)
            .alpha(alpha)
            .clickable(onClick = onClick)
    )
}

@Preview
@Composable
fun PreviewDrawPlanet() {
    var size: Size by remember { mutableStateOf(Size.Zero) }
    val ephemeris: Ephemeris = AstrologicalPoints.Saturn
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates ->
            size = coordinates.size.toSize()
        }

    ) {
    DrawPlanet(ephemeris, OrreryVM().uiState, 400.0, 315.0, size, OrbitalProportions(), Modifier )
    }
}
