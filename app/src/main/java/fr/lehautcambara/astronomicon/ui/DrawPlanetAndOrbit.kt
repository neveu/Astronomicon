package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
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
import angled
import cosd
import elevationd
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.PlanetClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import sind
import kotlin.math.roundToInt
import kotlin.math.sqrt


@Composable
fun DrawPlanetAndOrbit(body: Ephemeris, r: Int, coords: Coords?, id: Int, pointerRadius: Int, orbitColor: Color =  Color.Black,  modifier: Modifier) {
    if (coords == null) return
    DrawPlanetAndOrbit(body, r = r, xecl = coords.x, yecl = coords.y, id = id, pointerRadius, orbitColor,  modifier = modifier)
}
@Composable
fun DrawPlanetAndOrbit(body: Ephemeris, r: Int, xecl: Double, yecl: Double, id: Int, pointerRadius: Int, orbitColor: Color =  Color.Black,   modifier: Modifier) {
    val ang = angled(xecl, yecl)
    DrawOrbit(radius = r, color = orbitColor, modifier = modifier)
    DrawPlanet(body, r, ang, id, pointerRadius,   modifier)
}

@Composable
fun DrawOrbit(radius: Int, color: Color = Color.Black, stroke: Float = 2F, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(color = color, style = Stroke(width = stroke), radius = radius.toFloat())
    }
}
@Composable
fun DrawPlanet(body: Ephemeris, r: Int, xecl: Double, yecl: Double, id: Int, pointerRadius: Int,  modifier: Modifier) {
    val ang = angled(xecl, yecl)
    DrawPlanet(body, r, ang, id, pointerRadius,  modifier)
}




@Composable
fun DrawPlanet(body: Ephemeris, r: Double, a: Double,  id: Int, pointerRadius: Int, modifier: Modifier) {
    val x = (r*cosd(a)).roundToInt()
    val y = (r*sind(a)).roundToInt()
    DrawPlanet(body, x,y,id, modifier)

}

@Composable
fun DrawPlanet(body: Ephemeris, r: Int, a: Double, id: Int, pointerRadius: Int,    modifier: Modifier) {
    DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r*cosd(a)).roundToInt()
    val y = (r*sind(a)).roundToInt()
    DrawPlanet(body, x,y,id, modifier)
}
@Composable
fun DrawPlanet(body: Ephemeris, x: Int, y: Int, id: Int, modifier: Modifier) {
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
            .clickable {
                Kbus.post(PlanetClickEvent(body))
            }
    )
}

@Composable
fun DrawPlanetEcliptic(body: Ephemeris, coords: Coords?,  id: Int, pointerRadius: Int, modifier: Modifier) {
    coords?.apply {
        val ra = sqrt(x*x + y*y)
        val a = angled(x,y)
        val elevation: Double = elevationd(x,y,z)
        val r = 200.0 + (elevation * 10.0)
        DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
        DrawPlanet(body, r, a, id, pointerRadius, modifier)
    }
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
        DrawPlanetEcliptic(uiState.Earth,  uiState.earth, R.drawable.earthjpg40, 600, modifier)
        // DrawPlanetAndOrbit(uiState.Sun, r=100, uiState.sun, R.drawable.sun2, 600, modifier)
    }

}