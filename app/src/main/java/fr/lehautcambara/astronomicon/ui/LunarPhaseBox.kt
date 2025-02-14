package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.FindNodeEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime

@Composable
fun LunarPhaseBox(uiState: StateFlow<OrreryUIState>, modifier: Modifier = Modifier) {

    with(uiState.collectAsState().value){
        val earthToMoon = earth.fromTo(moon)
        val scale: Double = (earthToMoon.mag()/384400F)*0.7
        LunarPhaseBox(lunarPhaseImage(), scale.toFloat(), zonedDateTime)
    }
}

@Composable
private fun LunarPhaseBox(image: Int, scale: Float = 0.5f, zdt: ZonedDateTime) {
    Image(
        painterResource(id = image),
        "Moon Phase",
        modifier = Modifier
            .scale(scale)
            .clickable {
                Kbus.post(FindNodeEvent("Moon", zdt))
            }
    )
}

@Preview
@Composable
private fun PreviewLunarPhaseBox() {
    LunarPhaseBox(OrreryVM().uiState)
}