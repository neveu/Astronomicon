package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OrreryBox(uiState: StateFlow<OrreryUIState>, orreryBackground: Int, orbitIncrement: Int = 55) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    var size by remember { mutableStateOf(Size.Zero) }
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
        DrawAll(orreryUIState,  orbitIncrement, modifier)
    }
}

@Composable
private fun DrawAll(
    uiState: OrreryUIState,
    orbitIncrement: Int,
    modifier: Modifier
) {
    val pointerRadius = orbitIncrement * 9
    with(uiState) {
        DrawPlanetAndOrbit(
            r = orbitIncrement,
            mercury,
            id = R.drawable.mercury,
            pointerRadius,
            modifier = modifier
        )
        DrawPlanetAndOrbit(r = 0, sun, id = R.drawable.sun2,0, modifier = modifier)
        DrawPlanetAndOrbit(
            r = orbitIncrement * 2,
            venus,
            id = R.drawable.venus40,
            pointerRadius,
            modifier = modifier
        )
        DrawPlanetAndOrbit(
            r = orbitIncrement * 3,
            earth,
            id = R.drawable.earthjpg40,
            pointerRadius,
            modifier = modifier
        )
        DrawPlanetAndOrbit(
            r = orbitIncrement * 4,
            mars,
            id = R.drawable.mars,
            pointerRadius,
            modifier = modifier
        )
        DrawPlanetAndOrbit(
            r = orbitIncrement * 5,
            jupiter,
            id = R.drawable.jupiter,
            pointerRadius,
            modifier = modifier
        )
        DrawPlanetAndOrbit(
            r = orbitIncrement * 6,
            saturn,
            id = R.drawable.saturn30,
            pointerRadius,
            modifier = modifier
        )
    }
}

@Preview
@Composable

fun PreviewOrreryBox() {
    OrreryBox(OrreryVM().uiState, orreryBackground = R.drawable.acsquare4)
}