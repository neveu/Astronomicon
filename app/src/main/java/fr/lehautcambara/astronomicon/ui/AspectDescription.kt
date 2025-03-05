package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AspectDescription(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Text(text = orreryUIState.aspectDescription)
}

@Preview
@Composable
fun PreviewAspectDescription() {
    Box{
        AspectDescription(OrreryVM().uiState)
    }
}