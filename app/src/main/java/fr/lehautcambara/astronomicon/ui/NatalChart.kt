package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DrawNatalChart(radius: Float, modifier: Modifier) {

    Canvas(modifier = modifier){
        drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = radius)
    }
}

@Preview
@Composable
fun PreviewDrawNatalChart() {
    var width by remember { mutableStateOf(0F) }
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            width = coordinates.boundsInRoot().width
        }

    ) {

        DrawNatalChart(width/2, modifier = Modifier
            .align(Alignment.Center))
    }
}