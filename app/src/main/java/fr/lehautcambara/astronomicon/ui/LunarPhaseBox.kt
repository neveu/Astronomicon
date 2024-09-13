package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow


@Composable
fun LunarPhaseBox(uiState: StateFlow<OrreryUIState>, modifier: Modifier = Modifier) {
    val phaseArray: Array<Int> = remember{ arrayOf(
        R.drawable.moon_6622,
        R.drawable.moon_6644,
        R.drawable.moon_6668,
        R.drawable.moon_6692,
        R.drawable.moon_6716,
        R.drawable.moon_6740,
        R.drawable.moon_6764,
        R.drawable.moon_6788,
        R.drawable.moon_6812,
        R.drawable.moon_6836,
        R.drawable.moon_6860,
        R.drawable.moon_6908,
        R.drawable.moon_6932,
        R.drawable.moon_6956,
        R.drawable.moon_6980,
        R.drawable.moon_7004,
        R.drawable.moon_7028,
        R.drawable.moon_7052,
        R.drawable.moon_7076,
        R.drawable.moon_7100,
        R.drawable.moon_7124,
        R.drawable.moon_7148,
        R.drawable.moon_7172,
        R.drawable.moon_7196,
        R.drawable.moon_7220,
        R.drawable.moon_7244,
        R.drawable.moon_7268,
        R.drawable.moon_7292,
        R.drawable.moon_7316,
        )}
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    LunarPhaseBox(phaseArray, orreryUIState.moonPhase(phaseArray.size ), modifier)
}

@Composable
fun LunarPhaseBox(phaseArray: Array<Int>, phase: Int, modifier: Modifier = Modifier) {

    Image(
        painterResource(id = phaseArray[phase]),
        "Moon Phase",
        modifier = modifier
    )
}

@Preview
@Composable
private fun PreviewLunarPhaseBox() {
    LunarPhaseBox(OrreryVM().uiState)
}