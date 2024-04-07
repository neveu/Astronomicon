package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme

@Composable
fun OrreryScreen(bg: Int, orreryBackground: Int, orreryVM: OrreryVM) {
    Box( modifier = Modifier
        .fillMaxSize()
        .paint(
            painterResource(id = bg),
            contentScale = ContentScale.FillBounds
        ))
    {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OrreryDate(orreryVM.uiState)
            OrreryBox(orreryVM.uiState, orreryBackground)
            LunarPhaseBox(phase = 5, modifier = Modifier.fillMaxSize(0.5F).padding(vertical = 16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewOrreryScreen() {
    AstronomiconTheme {
        OrreryScreen(R.drawable.milkyway, R.drawable.acsquare4, OrreryVM())

    }
}


