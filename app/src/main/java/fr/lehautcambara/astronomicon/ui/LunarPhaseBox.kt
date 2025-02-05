package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.FindNodeEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime

@Composable
fun LunarPhaseBox(uiState: StateFlow<OrreryUIState>, modifier: Modifier = Modifier) {
    val phaseArray: Array<Int> = remember { arrayOf(
        R.drawable.moon00,
        R.drawable.moon01,
        R.drawable.moon02,
        R.drawable.moon03,
        R.drawable.moon04,
        R.drawable.moon05,
        R.drawable.moon06,
        R.drawable.moon07,
        R.drawable.moon08,
        R.drawable.moon09,
        R.drawable.moon10,
        R.drawable.moon11,
        R.drawable.moon12,
        R.drawable.moon13,
        R.drawable.moon14,
        R.drawable.moon15,
        R.drawable.moon16,
        R.drawable.moon17,
        R.drawable.moon18,
        R.drawable.moon19,
        R.drawable.moon20,
        R.drawable.moon21,
        R.drawable.moon22,
        R.drawable.moon23,
        R.drawable.moon24,
        R.drawable.moon25,
        R.drawable.moon26,
        R.drawable.moon27,
        R.drawable.moon28,
        R.drawable.moon29,
        )
    }
    with(uiState.collectAsState().value){
        val earthToMoon = earth.fromTo(moon)
        val scale: Double = (earthToMoon.mag()/384400F)*0.7
        LunarPhaseBox(phaseArray, moonPhase(phaseArray.size ), scale.toFloat(), zonedDateTime)
    }
}

@Composable
fun LunarPhaseBox(phaseArray: Array<Int>, phase: Int, scale: Float = 0.5f,  zdt: ZonedDateTime) {
    Image(
        painterResource(id = phaseArray[phase]),
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