/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.events.PlanetSignClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.sind
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Composable
fun DrawPlanet(body: Ephemeris, uiState: StateFlow<OrreryUIState>, r: Double, a: Double, size: Size, proportions: OrbitalProportions, modifier: Modifier) {
    val pointerRadius = (size.width * proportions.pointerScale).toInt()
    if (r>0.0) DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r* cosd(a)).roundToInt()
    val y = (r* sind(a)).roundToInt()
    DrawPlanet(body, uiState, x, y, size, proportions, modifier)
}

@Composable
fun DrawPlanet(body: Ephemeris, uiState: StateFlow<OrreryUIState>, x: Int, y: Int, size: Size, proportions: OrbitalProportions, modifier: Modifier) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    val id: Int? = orreryUIState.getDrawableByEphemeris(body)
    // shadow
//    Image(
//        painterResource(id = R.drawable.shadow30x30), "shadow",
//        modifier= modifier
//            .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
//            .size(
//                pixToDp(2 * size.width * proportions.planetShadowScale)
//            )
//            .blur(3.dp)
//            .alpha(0.7F)
//    )
    // Planet

    id?.let {
        Image (
            painter = rememberDrawablePainter(drawable = getDrawable(LocalContext.current, it)),
//            painterResource(id = getDrawable(LocalContext.current, it).),
            "shadow",
            modifier = modifier
                .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
                .size(pixToDp(size.width * proportions.planetShadowScale))
                .blur(2.dp)
                .alpha(0.3F)
        )

        Image(
            // painterResource(id = id),
            painter = rememberDrawablePainter(drawable = getDrawable(LocalContext.current, it)),
            body.toString(),
            modifier = modifier
                .absoluteOffset { IntOffset(x, -y) }
                .size(pixToDp(size.width * proportions.planetImageScale))
                .clickable {
                    Kbus.post(PlanetClickEvent(body))
                }
        )
    }

}

@Composable
fun DrawPlanet(planet: PlanetSignPolarCoords?, sizeDp: Dp, planetSymbolDrawable: Int?, modifier: Modifier = Modifier) {
    planetSymbolDrawable?.let { id ->
        planet?.let { planet ->
            Image(painterResource(id = id), planet.planet,
                modifier = modifier
                    .absoluteOffset {planet.offset() }
                    .size(sizeDp)
                    .clickable {
                        Kbus.post(PlanetSignClickEvent(planet))
                    }
            )
        }
    }
}
@Composable
fun pixToDp(
    pixels: Double,
): Dp {
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = ((pixels * 2.0) / screenPixelDensity).dp
    return dpValue
}

