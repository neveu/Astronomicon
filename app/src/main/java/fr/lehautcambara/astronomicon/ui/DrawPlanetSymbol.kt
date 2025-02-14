/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.orrery.graphics.defaultPlanetSignDrawables
import fr.lehautcambara.astronomicon.sind
import kotlin.math.roundToInt

@Composable
fun DrawPlanetSymbol(body: Ephemeris, r: Double, a: Double, size: Size, proportions: OrbitalProportions, modifier: Modifier) {
    val pointerRadius = (size.width * proportions.pointerScale).toInt()
    if (r>0.0) DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r* cosd(a)).roundToInt()
    val y = (r* sind(a)).roundToInt()
    DrawPlanetSymbol(body, x, y, size, proportions, modifier)
}

@Composable
fun DrawPlanetSymbol(body: Ephemeris, x: Int, y: Int, size: Size, proportions: OrbitalProportions, modifier: Modifier) {
    // shadow
    defaultPlanetSignDrawables[body.toString()]?.let { id: Int ->
        // draw shadow
        Image (
            painter = rememberDrawablePainter(drawable =
            getDrawable(LocalContext.current, id)),
            "shadow",
            modifier = modifier
                .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
                .size(pixToDp(size.width * proportions.planetSymbolScale))
                .blur(2.dp)
                .alpha(0.5F)
        )
        Image (
            painter = rememberDrawablePainter(drawable =
                getDrawable(LocalContext.current, id)),
            body.toString(),
            modifier = modifier
                .absoluteOffset { IntOffset(x, -y) }
                .size(pixToDp(size.width * proportions.planetSymbolScale))
                .clickable {
                    Kbus.post(PlanetClickEvent(body))
                }
        )
    }
}
