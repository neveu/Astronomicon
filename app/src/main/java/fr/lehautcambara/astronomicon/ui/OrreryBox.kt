package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import fr.lehautcambara.astronomicon.astrology.convertToJulianCentury
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.DisplayMode
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryUIState.Companion.fromTo
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime

@Composable
fun OrreryBox(uiState: StateFlow<OrreryUIState>, orreryBackground: Int) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .fillMaxWidth()
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
        DrawBox(orreryUIState, modifier)
    }
}

@Composable
fun DrawBox(orreryUIState: OrreryUIState,  modifier: Modifier) {
    when(orreryUIState.displayMode) {
        DisplayMode.Heliocentric -> DrawAllHeliocentric(uiState = orreryUIState, orbitIncrement = 55, modifier = modifier)
        DisplayMode.Geocentric -> DrawAllGeocentric(uiState = orreryUIState, orbitIncrement = 45, modifier = modifier)
        DisplayMode.Ecliptic -> DrawAllEcliptic(uiState = orreryUIState,  modifier = modifier)
        DisplayMode.NatalChart -> DrawNatalChart(uiState = orreryUIState, modifier = modifier)
    }
}

@Composable
private fun DrawAllHeliocentric(
    uiState: OrreryUIState,
    orbitIncrement: Int,
    modifier: Modifier
) {
    val pointerRadius = 0
    with(uiState) {
        DrawPlanetAndOrbit(Mercury, r = orbitIncrement, mercury, id = R.drawable.mercury, pointerRadius, modifier = modifier)
        DrawPlanetAndOrbit(Venus, r = orbitIncrement * 2, venus, id = R.drawable.venus40, pointerRadius, modifier = modifier)
        DrawPlanetAndOrbit(Earth, r = orbitIncrement * 3, earth, id = R.drawable.earthjpg40, pointerRadius, modifier = modifier)
        DrawPlanetAndOrbit(Mars, r = orbitIncrement * 4, mars, id = R.drawable.mars, pointerRadius, modifier = modifier)
        DrawPlanetAndOrbit(Jupiter, r = orbitIncrement * 5, jupiter, id = R.drawable.jupiter, pointerRadius, modifier = modifier)
        DrawPlanetAndOrbit(Saturn, r = orbitIncrement * 6, saturn, id = R.drawable.saturn30, pointerRadius, modifier = modifier)
        DrawPlanetAndOrbit(Sun, r = 0, sun, id = R.drawable.sun2, 0, modifier = modifier)
    }
}
@Composable
private fun DrawAllGeocentric(
    uiState: OrreryUIState,
    orbitIncrement: Int,
    modifier: Modifier
) {
    val pointerRadius = orbitIncrement * 9
    with(uiState) {
        DrawPlanetAndOrbit(Moon, r = orbitIncrement, coords = fromTo(earth, moon),
            id = R.drawable.moon2 , pointerRadius = pointerRadius, modifier = modifier)

        var orbitColor: Color = orbitColor(Earth, Mercury, zonedDateTime)
        DrawPlanetAndOrbit(Mercury, r = orbitIncrement*2, coords = fromTo(earth, mercury),
            id = R.drawable.mercury , pointerRadius = pointerRadius, orbitColor = orbitColor,  modifier = modifier)

        orbitColor = orbitColor(Earth, Venus, zonedDateTime)
        DrawPlanetAndOrbit(Venus, r = orbitIncrement*3, coords = fromTo(earth, venus),
            id = R.drawable.venus40 , pointerRadius = pointerRadius, orbitColor = orbitColor,  modifier = modifier)

        DrawPlanetAndOrbit(Sun, r = orbitIncrement*4, coords = fromTo(earth, sun),
            id = R.drawable.sun2 , pointerRadius = pointerRadius,  modifier = modifier)

        orbitColor = orbitColor(Earth, Mars, zonedDateTime)
        DrawPlanetAndOrbit(Mars, r = orbitIncrement*5, coords = fromTo(earth, mars),
            id = R.drawable.mars , pointerRadius = pointerRadius, orbitColor = orbitColor,  modifier = modifier)

        orbitColor = orbitColor(Earth, Jupiter, zonedDateTime)
        DrawPlanetAndOrbit(Jupiter, r = orbitIncrement*6, coords = fromTo(earth, jupiter),
            id = R.drawable.jupiter , pointerRadius = pointerRadius, orbitColor = orbitColor,  modifier = modifier)

        orbitColor = orbitColor(Earth, Jupiter, zonedDateTime)
        DrawPlanetAndOrbit(Saturn, r = orbitIncrement*7, coords = fromTo(earth, saturn),
            id = R.drawable.saturn30 , pointerRadius = pointerRadius,  orbitColor = orbitColor, modifier = modifier)

        DrawPlanetAndOrbit(Earth, r = 0, coords = earth,  id = R.drawable.earthjpg40,
            0, modifier = modifier)
    }
}

fun orbitColor(fromPlanet: Ephemeris, planet: Ephemeris, zdt: ZonedDateTime) : Color =
    if (apparentAngularVelocity(fromPlanet, planet, zdt) < 0.0) Color.Red else Color.Black

fun apparentAngularVelocity(fromPlanet: Ephemeris, planet: Ephemeris, zdt: ZonedDateTime): Double {
        val planetCoords = planet.eclipticCoords(zdt.convertToJulianCentury())
        val fromCoords = fromPlanet.eclipticCoords(zdt.convertToJulianCentury())
        val deltaT = zdt.plusHours(1)
        val currentAngle = fromCoords.angleTo(planetCoords)
        val deltaJ = deltaT.convertToJulianCentury()
        val deltaEarth = fromPlanet.eclipticCoords(deltaJ)
        val deltaPlanet = planet.eclipticCoords(deltaJ)
        val deltaAngle = deltaEarth.angleTo(deltaPlanet)
        return deltaAngle - currentAngle
}

@Composable
private fun DrawAllEcliptic(
    uiState: OrreryUIState,
    modifier: Modifier
) {
    with(uiState) {
        DrawOrbit(radius = 200, color = Color.Red, stroke = 4F, modifier = modifier)

        DrawPlanetEcliptic(body = Mercury, coords = fromTo(earth, mercury), id = R.drawable.mercury, pointerRadius = 500, modifier = modifier)
        DrawPlanetEcliptic(body = Venus, coords = fromTo(earth, venus), id = R.drawable.venus40, pointerRadius = 500, modifier = modifier)
        DrawPlanetEcliptic(body = Sun, coords = fromTo(earth, sun), id = R.drawable.sun2, pointerRadius = 500, modifier = modifier)
        DrawPlanetEcliptic(body = Mars, coords = fromTo(earth, mars), id = R.drawable.mars, pointerRadius = 500, modifier = modifier)
        DrawPlanetEcliptic(body = Jupiter, coords = fromTo(earth, jupiter), id = R.drawable.jupiter, pointerRadius = 500, modifier = modifier)
        DrawPlanetEcliptic(body = Saturn, coords = fromTo(earth, saturn), id = R.drawable.saturn30, pointerRadius = 500, modifier = modifier)
        DrawPlanetEcliptic(body = Moon, coords = fromTo(earth, moon), id = R.drawable.moon2, pointerRadius = 500, modifier = modifier)
        DrawPlanet(body = Earth, r = 0.0, a = 0.0, id = R.drawable.earthjpg40, pointerRadius = 0, modifier = modifier)


    }


}
@Preview
@Composable

fun PreviewOrreryBox() {
    OrreryBox(OrreryVM().uiState, orreryBackground = R.drawable.acsquare4)
}