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
import androidx.compose.ui.draw.alpha
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
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.angled
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mars
import fr.lehautcambara.astronomicon.astrology.planetDrawables
import fr.lehautcambara.astronomicon.astrology.planetSignDrawables
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.elevationd
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.PlanetGraphic
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
fun DrawPlanetAndOrbit(body: Ephemeris, orbitNumber: Int, outerSize: Size, proportions: OrbitalProportions, coords: Coords?,  orbitColor: Color =  Color.Black, modifier: Modifier) {
    if (coords == null) return

    DrawPlanetAndOrbit(body, orbitNumber, outerSize, proportions, xecl = coords.x, yecl = coords.y,  orbitColor,  modifier = modifier)
}
@Composable
fun DrawPlanetAndOrbit(body: Ephemeris, orbitNumber: Int, outerSize: Size, proportions: OrbitalProportions, xecl: Double, yecl: Double, orbitColor: Color =  Color.Black,   modifier: Modifier) {
    val ang = angled(xecl, yecl)
    val r = (outerSize.width * proportions.orbitScale * orbitNumber)

    DrawOrbit(radius = r.toInt(), color = orbitColor, modifier = modifier)
    DrawPlanet(body, r, ang, outerSize, proportions, modifier)
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
fun DrawPlanet(body: Ephemeris, r: Double, a: Double, size: Size, proportions: OrbitalProportions,   modifier: Modifier) {
    val pointerRadius = (size.width * proportions.pointerScale).toInt()
    if (r>0.0) DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r* cosd(a)).roundToInt()
    val y = (r* sind(a)).roundToInt()
    DrawPlanet(body, x, y, size, proportions, modifier)
}
@Composable
fun DrawPlanet(body: Ephemeris, x: Int, y: Int, size: Size, proportions: OrbitalProportions, modifier: Modifier) {
    // shadow
    Image(
        painterResource(id = R.drawable.shadow30x30), "shadow",
        modifier= modifier
            .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
            .size(
                pixToDp(2 * size.width * proportions.planetShadowScale)
            )
            .blur(3.dp)
            .alpha(0.7F)
    )
    // Planet
    planetDrawables[body.toString()]?.let { id: Int ->
        Image(
            // painterResource(id = id),
            painter = rememberDrawablePainter(drawable =
                getDrawable(
                    LocalContext.current,
                    id
                )
            ),
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
fun DrawPlanetSymbol(body: Ephemeris, r: Double, a: Double, size: Size, proportions: OrbitalProportions,   modifier: Modifier) {
    val pointerRadius = (size.width * proportions.pointerScale).toInt()
    if (r>0.0) DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
    val x = (r* cosd(a)).roundToInt()
    val y = (r* sind(a)).roundToInt()
    DrawPlanetSymbol(body, x, y, size, proportions, modifier)
}

@Composable
fun DrawPlanetSymbol(body: Ephemeris, x: Int, y: Int, size: Size, proportions: OrbitalProportions, modifier: Modifier) {
    // shadow
    planetSignDrawables[body.toString()]?.let { id: Int ->
        // draw shadow
        Image (
            painterResource(id = id), "shadow",
            modifier = modifier
                .absoluteOffset { IntOffset(x + 20, -(y - 20)) }
                .size(pixToDp(size.width * proportions.planetShadowScale))
                .blur(2.dp)
                .alpha(0.7F)
        )
        Image (
            // painterResource(id = id),
            painter = rememberDrawablePainter(drawable =
            getDrawable(LocalContext.current, id)),
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
fun DrawPlanetEcliptic(body: Ephemeris, size: Size, proportions: OrbitalProportions,  coords: Coords?, planetGraphic: PlanetGraphic, modifier: Modifier) {
    coords?.apply {
        val a = angled(x,y)
        val elevation: Double = elevationd(x,y,z)
        val r = (size.width*proportions.eclipticRadiusScale) + (elevation * proportions.elevationScale)
        val pointerRadius = (size.width * proportions.pointerScale).toInt()
        DrawZodiacPointer(radius = pointerRadius, a = a, width = 1F, modifier = modifier)
        DrawPlanetSymbol(body, r=r, a, size = size, proportions = proportions, modifier)
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
        DrawPlanetEcliptic(Mars, size, proportions = OrbitalProportions(), uiState.mars, uiState.planetGraphic,   modifier)
        // DrawPlanetAndOrbit(uiState.Sun, r=100, uiState.sun, R.drawable.sun2, 600, modifier)
    }

}