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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import rcosd
import rsind

fun DrawScope.drawZodiacDividers(r0: Float, r1: Float, angleOffset: Float, color: Color) {
    for (i in 0..11) {
        val angle = (i * 30F) + angleOffset
        val x0 = rcosd(r0, angle)
        val y0 = rsind(r0, angle)
        val x1 = rcosd(r1, angle)
        val y1 = rsind(r1, angle)
        drawLine(Color.Black, start = Offset(x1,y1), end = Offset(x0, y0), strokeWidth = 5F)
        val xt = rcosd((r0 + r1)/2F, angle+15F)
        val yt = rsind((r0 + r1)/2F, angle+15F)



    }
}

@Composable
fun DrawNatalChart(r0: Float, r1: Float = r0 - 60, modifier: Modifier) {
    Canvas(modifier = modifier){

        drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r0)
        drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r1)
        drawZodiacDividers(r0, r1, 15F, color = Color.Black)
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
            .align(Alignment.Center)
        )
    }
}