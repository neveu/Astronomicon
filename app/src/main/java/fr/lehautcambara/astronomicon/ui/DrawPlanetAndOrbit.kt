package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import angle
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.ephemeris.Coords
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DrawPlanetAndOrbit(r: Int, coords: Coords, id: Int, modifier: Modifier) {
    DrawPlanetAndOrbit(r = r, xecl = coords.x, yecl = coords.y, id = id, modifier = modifier)
}
@Composable
fun DrawPlanetAndOrbit(r: Int, xecl: Double, yecl: Double, id: Int, modifier: Modifier) {
    DrawOrbit(radius = r, modifier = modifier)
    DrawPlanet(r, xecl, yecl, id, modifier)
}
@Composable
fun DrawPlanet(r: Int, xecl: Double, yecl: Double, id: Int, modifier: Modifier) {
    val ang = angle(xecl, yecl)
    DrawPlanet(r, ang, id, modifier)
}

@Composable
fun DrawPlanet(r: Int, a: Double, id: Int, modifier: Modifier){
    val x = (r* cos(a)).roundToInt()
    val y = (r* sin(a)).roundToInt()
    DrawPlanet(x,y,id, modifier)
}
@Composable
fun DrawPlanet(x: Int, y: Int, id: Int, modifier: Modifier) {
    // shadow
    Image(
        painterResource(id = R.drawable.shadow), "shadow",
        modifier=modifier.absoluteOffset { IntOffset(x+20, -(y - 20) ) }
            .scale(1.2f)
            .blur(3.dp)
    )
    // Planet
    Image(
        painterResource(id = id), "Planet",
        modifier = modifier
            .absoluteOffset { IntOffset(x, -y) }
    )
}

@Composable
private fun DrawOrbit(radius: Int, color: Color = Color.Black, stroke: Float = 2F, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(color = color, style = Stroke(width = stroke), radius = radius.toFloat())
    }
}