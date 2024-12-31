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
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.DisplayMode
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow

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
            DisplayMode.Heliocentric -> DrawAllHeliocentric(uiState = uiState, R.drawable.acsquare4, modifier = modifier)
            DisplayMode.Geocentric -> DrawAllGeocentric(uiState = uiState, R.drawable.acsquare4, modifier = modifier)
            DisplayMode.Ecliptic -> DrawAllEcliptic(uiState = uiState, R.drawable.acsquare4,  modifier = modifier)
            DisplayMode.LunarNodes -> DrawAllLunarNodes(uiState = uiState, size, modifier = modifier)
            DisplayMode.NatalChart -> DrawNatalChart(uiState = uiState, size, modifier = modifier)
        }
    }
}



@Preview
@Composable

fun PreviewOrreryBox() {
    OrreryBox(OrreryVM().uiState, orreryBackground = R.drawable.acsquare4)
}