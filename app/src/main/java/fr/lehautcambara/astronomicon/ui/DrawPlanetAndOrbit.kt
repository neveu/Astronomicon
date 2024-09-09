package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.angled
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Earth
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.elevationd
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.sind
import kotlin.math.roundToInt

@Composable
private fun pixToDp(
    outerRadius: Double,
): Dp {
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = ((outerRadius * 2.0) / screenPixelDensity).dp
    return dpValue
}
@Composable
fun DrawPlanetAndOrbit(body: Ephemeris, orbitNumber: Int, outerSize: Size, proportions: OrbitalProportions, coords: Coords?, id: Int,  orbitColor: Color =  Color.Black, modifier: Modifier) {
    if (coords == null) return
    DrawPlanetAndOrbit(body, orbitNumber, outerSize, proportions, xecl = coords.x, yecl = coords.y, id = id, orbitColor,  modifier = modifier)
}
@Composable
fun DrawPlanetAndOrbit(body: Ephemeris, orbitNumber: Int, outerSize: Size, proportions: OrbitalProportions, xecl: Double, yecl: Double, id: Int,  orbitColor: Color =  Color.Black,   modifier: Modifier) {
    val ang = angled(xecl, yecl)
    val r = (outerSize.width * proportions.orbitScale * orbitNumber)

    DrawOrbit(radius = r.toInt(), color = orbitColor, modifier = modifier)
    DrawPlanet(body, r, ang, outerSize, proportions, id,  modifier)
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
fun DrawPlanet(body: Ephemeris, r: Double, a: Double, size: Size, proportions: OrbitalProportions, id: Int,    modifier: Modifier) {
    val pointerRadius = (size.width * proportions.pointerScale).toInt()
    if (r>0.0) DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r* cosd(a)).roundToInt()
    val y = (r* sind(a)).roundToInt()
    DrawPlanet(body, x, y, size, proportions, id, modifier)
}
@Composable
fun DrawPlanet(body: Ephemeris, x: Int, y: Int, size: Size, proportions: OrbitalProportions, id: Int, modifier: Modifier) {
    // shadow
    Image(
        painterResource(id = R.drawable.shadow30x30), "shadow",
        modifier= modifier
            .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
            .size(
                pixToDp(2 * size.width * proportions.planetShadowScale))
            .blur(3.dp)
    )
    // Planet
    Image(
        painterResource(id = id), body.toString(),
        modifier = modifier
            .absoluteOffset { IntOffset(x, -y) }
            .size(pixToDp(size.width * proportions.planetImageScale))
            .clickable {
                Kbus.post(PlanetClickEvent(body))
            }
    )
}

@Composable
fun DrawPlanetEcliptic(body: Ephemeris, size: Size, proportions: OrbitalProportions,  coords: Coords?,  id: Int,  modifier: Modifier) {
    coords?.apply {
        val a = angled(x,y)
        val elevation: Double = elevationd(x,y,z)
        val r = (size.width*proportions.eclipticRadiusScale) + (elevation * proportions.elevationScale)
        val pointerRadius = (size.width * proportions.pointerScale).toInt()
        DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
        DrawPlanet(body, r=r, a, size, proportions, id, modifier)
    }
}
@Preview
@Composable
private fun PreviewDrawPlanet() {
    val uiState = OrreryUIState()
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates ->
            size = coordinates.size.toSize()
        }

    ) {
        val modifier = Modifier.align(Alignment.Center)
        DrawPlanetEcliptic(Earth, size, proportions = OrbitalProportions() ,  uiState.earth, R.drawable.earthjpg40,  modifier)
        // DrawPlanetAndOrbit(uiState.Sun, r=100, uiState.sun, R.drawable.sun2, 600, modifier)
    }

}