package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import angle
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DrawPlanetAndOrbit(r: Int, coords: Coords, id: Int, pointerRadius: Int,  modifier: Modifier) {
    DrawPlanetAndOrbit(r = r, xecl = coords.x, yecl = coords.y, id = id, pointerRadius, modifier = modifier)
}
@Composable
fun DrawPlanetAndOrbit(r: Int, xecl: Double, yecl: Double, id: Int, pointerRadius: Int,   modifier: Modifier) {
    DrawOrbit(radius = r, modifier = modifier)
    DrawPlanet(r, xecl, yecl, id, pointerRadius, modifier)
}

@Composable
private fun DrawOrbit(radius: Int, color: Color = Color.Black, stroke: Float = 2F, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(color = color, style = Stroke(width = stroke), radius = radius.toFloat())
    }
}
@Composable
fun DrawPlanet(r: Int, xecl: Double, yecl: Double, id: Int, pointerRadius: Int,  modifier: Modifier) {
    val ang = angle(xecl, yecl)
    DrawPlanet(r, ang, id, pointerRadius, modifier)
}

@Composable
fun DrawPlanet(r: Int, a: Double, id: Int, pointerRadius: Int,   modifier: Modifier) {
    DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r*cos(a)).roundToInt()
    val y = (r*sin(a)).roundToInt()
    DrawPlanet(x,y,id, modifier)
}
@Composable
fun DrawPlanet(x: Int, y: Int, id: Int,  modifier: Modifier) {
    // shadow
    Image(
        painterResource(id = R.drawable.shadow30x30), "shadow",
        modifier= modifier
            .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
            .scale(2f)
            .blur(3.dp)
    )
    // Planet
    Image(
        painterResource(id = id), "Planet",
        modifier = modifier
            .absoluteOffset { IntOffset(x, -y) }
    )
}

@Preview
@Composable
private fun PreviewDrawPlanet() {
    val uiState = OrreryUIState()
    var size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates ->
            size = coordinates.size.toSize()
        }

    ) {
        val modifier = Modifier.align(Alignment.Center)

        DrawPlanetAndOrbit(100, uiState.mercury, R.drawable.mercury, 600, modifier)
    }

}