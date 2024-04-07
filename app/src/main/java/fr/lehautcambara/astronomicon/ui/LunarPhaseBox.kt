package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.R

@Composable
fun LunarPhaseBox(phase: Int, modifier: Modifier = Modifier) {
    val phaseArray = remember{ arrayOf(
        R.drawable.moonphase262_00,
        R.drawable.moonphase262_01,
        R.drawable.moonphase262_02,
        R.drawable.moonphase262_03,
        R.drawable.moonphase262_04,
        R.drawable.moonphase262_05,
        R.drawable.moonphase262_06,
        R.drawable.moonphase262_07,
        R.drawable.moonphase262_08,
        R.drawable.moonphase262_09,
        R.drawable.moonphase262_10,
        R.drawable.moonphase262_11,
        R.drawable.moonphase262_12,
        R.drawable.moonphase262_13,
        R.drawable.moonphase262_14,
        R.drawable.moonphase262_15,
        R.drawable.moonphase262_16,
        R.drawable.moonphase262_17,
        R.drawable.moonphase262_18,
        R.drawable.moonphase262_19,
        R.drawable.moonphase262_20,
        R.drawable.moonphase262_21,
        R.drawable.moonphase262_22,
        R.drawable.moonphase262_23,
    )}
    Image(
        painterResource(id = phaseArray[phase]),
        "Moon Phase",
        modifier = modifier
    )
}

@Preview
@Composable
private fun PreviewLunarPhaseBox() {
    LunarPhaseBox(4)
}