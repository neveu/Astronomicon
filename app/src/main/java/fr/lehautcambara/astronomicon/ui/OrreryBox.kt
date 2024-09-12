package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Earth
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Jupiter
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mars
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Mercury
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Moon
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Saturn
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Sun
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.Venus
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.DisplayMode
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryUIState.Companion.fromTo
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Composable
fun OrreryBox(uiState: StateFlow<OrreryUIState>, orreryBackground: Int) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier

        .paint(
            painterResource(id = orreryBackground),
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
        val modifier = Modifier.align(Alignment.Center)
        when(orreryUIState.displayMode) {
            DisplayMode.Heliocentric -> DrawAllHeliocentric(uiState = orreryUIState, size, modifier = modifier)
            DisplayMode.Geocentric -> DrawAllGeocentric(uiState = orreryUIState, size, modifier = modifier)
            DisplayMode.Ecliptic -> DrawAllEcliptic(uiState = orreryUIState, size, modifier = modifier)
            DisplayMode.NatalChart -> DrawNatalChart(uiState = orreryUIState, size, modifier = modifier)
        }
    }
}




@Composable
private fun DrawAllHeliocentric(
    uiState: OrreryUIState,
    size: Size,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    with(uiState) {
        DrawPlanetAndOrbit(Mercury, 1, size,   proportions, mercury,   modifier = modifier)
        DrawPlanetAndOrbit(Venus, 2,  size,  proportions, venus,  modifier = modifier)
        DrawPlanetAndOrbit(Earth, 3, size, proportions,  earth,   modifier = modifier)
        DrawPlanetAndOrbit(Mars, 4, size, proportions,   mars,   modifier = modifier)
        DrawPlanetAndOrbit(Jupiter, 5, size, proportions, jupiter,  modifier = modifier)
        val saturnScale = proportions.planetImageScale * 2
        DrawPlanetAndOrbit(Saturn, 6, size, proportions.copy(planetImageScale = saturnScale),  saturn, modifier = modifier)
        DrawPlanetAndOrbit(Sun, 0, size, proportions,  sun,  modifier = modifier)
    }
}
@Composable
private fun DrawAllGeocentric(
    uiState: OrreryUIState,
    size: Size,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    with(uiState) {
        DrawPlanetAndOrbit(Moon, 1, size, proportions, coords = fromTo(earth, moon), modifier = modifier)

        var orbitColor: Color = orbitColor(Earth, Mercury, zonedDateTime)
        DrawPlanetAndOrbit(Mercury, 2, size, proportions, coords = fromTo(earth, mercury),  orbitColor = orbitColor, modifier = modifier)

        orbitColor = orbitColor(Earth, Venus, zonedDateTime)
        DrawPlanetAndOrbit(Venus, 3, size, proportions, coords = fromTo(earth, venus),  orbitColor = orbitColor, modifier = modifier)
        DrawPlanetAndOrbit(Sun, 4, size, proportions, coords = fromTo(earth, sun),  modifier = modifier)

        orbitColor = orbitColor(Earth, Mars, zonedDateTime)
        DrawPlanetAndOrbit(Mars, 5, size, proportions, coords = fromTo(earth, mars),  orbitColor = orbitColor, modifier = modifier)

        orbitColor = orbitColor(Earth, Jupiter, zonedDateTime)
        DrawPlanetAndOrbit(Jupiter, 6, size, proportions, coords = fromTo(earth, jupiter),  orbitColor = orbitColor, modifier = modifier)

        orbitColor = orbitColor(Earth, Saturn, zonedDateTime)
        val saturnScale = proportions.planetImageScale * 2
        DrawPlanetAndOrbit(Saturn, 7, size, proportions.copy(planetImageScale = saturnScale), coords = fromTo(earth, saturn),  orbitColor = orbitColor, modifier = modifier)
        DrawPlanetAndOrbit(Earth, 0, size, proportions, coords = earth,  modifier = modifier)
    }
}

fun orbitColor(fromPlanet: Ephemeris, planet: Ephemeris, zdt: ZonedDateTime) : Color =
    if (fromPlanet.apparentAngularVelocity(planet, zdt) < 0.0)
        Color.Red
    else Color.Black


@Composable
private fun DrawAllEcliptic(
    uiState: OrreryUIState,
    size: Size,
    proportions: OrbitalProportions = OrbitalProportions(),
    modifier: Modifier
) {
    with(uiState) {
        DrawOrbit(radius = (size.width*proportions.eclipticRadiusScale).roundToInt(), color = Color.Red, stroke = 4F, modifier = modifier)

        DrawPlanetEcliptic(body = Mercury, size, proportions, coords = fromTo(earth, mercury),  modifier = modifier)
        DrawPlanetEcliptic(body = Venus, size, proportions, coords = fromTo(earth, venus), modifier = modifier)
        DrawPlanetEcliptic(body = Sun, size, proportions,  coords = fromTo(earth, sun), modifier = modifier)
        DrawPlanetEcliptic(body = Mars, size, proportions, coords = fromTo(earth, mars),   modifier = modifier)
        DrawPlanetEcliptic(body = Jupiter, size, proportions, coords = fromTo(earth, jupiter),  modifier = modifier)
        val saturnScale = proportions.planetImageScale * 2
        DrawPlanetEcliptic(body = Saturn, size, proportions.copy(planetImageScale = saturnScale), coords = fromTo(earth, saturn),  modifier = modifier)
        DrawPlanetEcliptic(body = Moon, size, proportions, coords = fromTo(earth, moon),   modifier = modifier)
        DrawPlanet(body = Earth, r = 0.0, a = 0.0, size, proportions,  modifier = modifier)
    }


}
@Preview
@Composable

fun PreviewOrreryBox() {
    OrreryBox(OrreryVM().uiState, orreryBackground = R.drawable.acsquare4)
}