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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import fr.lehautcambara.astronomicon.astrology.zodiacSignDrawables
import rcosd
import rsind

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawZodiacDividers(
    index: Int,
    r0: Double,
    r1: Double,
    angleOffset: Double,
    color: Color,
) {
    val angle = 180.0 + (index * 30F) + angleOffset // 180 so we start on the left
    val x0 = rcosd(r0, angle).toFloat()
    val y0 = rsind(r0, angle).toFloat()
    val x1 = rcosd(r1, angle).toFloat()
    val y1 = rsind(r1, angle).toFloat()
    drawLine(Color.Black, start = Offset(x1,y1), end = Offset(x0, y0), strokeWidth = 5F)
}

fun DrawScope.drawZodiacSign(index: Int,
                             r0: Double,
                             r1: Double,
                             angleOffset: Double,
                             imageArray: Array<ImageBitmap>,
) {
    //val angle = 180.0 + (index * 30F) + angleOffset // 180 so we start on the left
    val angle = (index * 30F) + angleOffset // 180 so we start on the left
    val image = imageArray[index]
    val x = -rcosd((r0 + r1)/2.0, angle+15.0)
    val y = rsind((r0 + r1)/2.0, angle+15.0)
    val x0 = (x - image.width/4).toInt()
    val y0 = (y - (image.height/4)).toInt()
    val intOffset = IntOffset(x0, y0)
    val intSize = IntSize(image.width/2, image.height/2)
    drawIntoCanvas { canvas ->
        drawImage(image, dstOffset = intOffset, dstSize = intSize)
    }

}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawZodiac(
    r0: Double,
    r1: Double,
    angleOffset: Double = 0.0,
    imageArray: Array<ImageBitmap>,
) {
    for (index in 0..11) {
        drawZodiacDividers(index, r0, r1, angleOffset, color = Color.Black)
        drawZodiacSign(index, r0, r1, angleOffset,  imageArray,)
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun DrawNatalChart(r0: Double, r1: Double = r0 - 60, angleOffset: Double = 15.0,  modifier: Modifier) {
    val imageArray: Array<ImageBitmap> = zodiacSignDrawables.map {
        ImageBitmap.imageResource(id = it)
    }.toTypedArray()
    Canvas(modifier = modifier){
        drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r0.toFloat())
        drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r1.toFloat())
        drawZodiac(r0, r1, angleOffset, imageArray)

    }
}

@Preview
@Composable
fun PreviewDrawNatalChart() {
    var width by remember { mutableStateOf(0.0) }
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            width = coordinates.boundsInRoot().width.toDouble()
        }
    ) {
        DrawNatalChart(width/2, modifier = Modifier
            .align(Alignment.Center)
        )
    }
}