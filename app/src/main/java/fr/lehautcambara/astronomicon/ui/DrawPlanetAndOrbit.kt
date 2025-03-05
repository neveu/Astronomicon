package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.angled
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mars
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DrawPlanetAndOrbit(body: Ephemeris, uiState: StateFlow<OrreryUIState>, orbitNumber: Int, outerSize: Size, proportions: OrbitalProportions, coords: Coords?, orbitColor: Color =  Color.Black, modifier: Modifier) {
    if (coords == null) return
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    orreryUIState.getDrawableByEphemeris(body)?.let {id->
        DrawPlanetAndOrbit(body,
            uiState, orbitNumber, outerSize, proportions, xecl = coords.x, yecl = coords.y,  orbitColor,  modifier = modifier)
    }
}
@Composable
fun DrawPlanetAndOrbit(body: Ephemeris,  uiState: StateFlow<OrreryUIState>, orbitNumber: Int, outerSize: Size, proportions: OrbitalProportions, xecl: Double, yecl: Double, orbitColor: Color =  Color.Black,   modifier: Modifier) {
    val ang = angled(xecl, yecl)
    val r = (outerSize.width * proportions.orbitScale * orbitNumber)

    DrawOrbit(radius = r.toInt(), color = orbitColor, modifier = modifier)
    DrawPlanet(body, uiState, r, ang, outerSize, proportions, modifier)
}

@Composable
fun DrawOrbit(radius: Int, color: Color = Color.Black, stroke: Float = 2F, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(color = color, style = Stroke(width = stroke), radius = radius.toFloat())
    }
}

@Preview
@Composable
private fun PreviewDrawPlanetEcliptic() {
    val orreryUIState: OrreryUIState by OrreryVM().uiState.collectAsState()
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates ->
            size = coordinates.size.toSize()
        }

    ) {
        val modifier = Modifier.align(Alignment.Center)
        DrawPlanetEcliptic(Mars,  size, proportions = OrbitalProportions(), coords = OrreryUIState.fromTo(orreryUIState.earth, orreryUIState.mars),
            modifier)
        // DrawPlanetAndOrbit(uiState.Sun, r=100, uiState.sun, R.drawable.sun2, 600, modifier)
    }

}