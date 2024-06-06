package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.astrology.ascendant
import java.time.ZonedDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AscendantCalculator(zdt: ZonedDateTime, longitude: Double? = null) {
    val asc: Double = zdt.ascendant()
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        Text("Ascendant: $asc")
    }
}

@Preview
@Composable
fun PreviewAscendantCalculator () {
    AscendantCalculator(zdt = ZonedDateTime.now())
}