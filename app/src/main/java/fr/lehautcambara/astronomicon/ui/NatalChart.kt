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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import fr.lehautcambara.astronomicon.astrology.ascendant
import fr.lehautcambara.astronomicon.astrology.planetSignDrawables
import fr.lehautcambara.astronomicon.astrology.zodiacSignDrawables
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import rcosd
import rsind
import java.time.ZonedDateTime

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawDividers(
    index: Int,
    r0: Double,
    r1: Double,
    angleOffset: Double,
    color: Color,
) {
    val angle = (index * 30F) + angleOffset // 180 so we start on the left
    val x0 = -rcosd(r0, angle).toFloat()
    val y0 = rsind(r0, angle).toFloat()
    val x1 = -rcosd(r1, angle).toFloat()
    val y1 = rsind(r1, angle).toFloat()
    drawLine(Color.Black, start = Offset(x1,y1), end = Offset(x0, y0), strokeWidth = 5F)
}

fun DrawScope.drawZodiacSign(index: Int,
                             r0: Double,
                             r1: Double,
                             angleOffset: Double,
                             imageArray: Array<ImageBitmap>,
) {
    val angle = (index * 30F) + angleOffset + 15.0
    val image = imageArray[index]
    drawBitmapImage( (r0 + r1) / 2.0, angle, image)
}


private fun DrawScope.drawBitmapImage(
    meanRadius: Double,
    angle: Double,
    image: ImageBitmap
) {
    val x = -rcosd(meanRadius, angle)
    val y = rsind(meanRadius, angle)
    val x0 = (x - image.width / 4).toInt()
    val y0 = (y - (image.height / 4)).toInt()
    val intOffset = IntOffset(x0, y0)
    val intSize = IntSize(image.width / 2, image.height / 2)
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
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r0.toFloat())
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r1.toFloat())
    for (index in 0..11) {
        drawDividers(index, r0, r1, angleOffset, color = Color.Black)
        drawZodiacSign(index, r0, r1, angleOffset,  imageArray,)
    }
}


fun DrawScope.drawPlanet(planet: ImageBitmap, r: Double, angle: Double, scale: Double = 1.0) {
    val sc = scale.toFloat()
    scale(scale =sc) {
        drawBitmapImage( r/sc, angle, planet)
    }
}


private fun DrawScope.drawHouses(r1: Double, r2: Double, ) {
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r2.toFloat())
    for (index in 0..11) {
        drawDividers(index, r1, r2, angleOffset = 0.0, color = Color.Black)
    }
}
@OptIn(ExperimentalTextApi::class)
@Composable
fun DrawNatalChart(r0: Double, r1: Double = r0 - 60, r2: Double = r1 - 120, angleOffset: Double = 0.0,  modifier: Modifier) {
    val zodiacImages: Array<ImageBitmap> = zodiacSignDrawables.map {
        ImageBitmap.imageResource(id = it)
    }.toTypedArray()


    Canvas(modifier = modifier) {
        drawZodiac(r0, r1, angleOffset, zodiacImages)
        drawHouses(r1, r2)
    }
}

@Composable
fun DrawNatalChart (
    zdt: ZonedDateTime = ZonedDateTime.now(),
    ephemerides: Map<String, Ephemeris> = fr.lehautcambara.astronomicon.astrology.ephemerides,
    r0: Double, r1: Double = r0 - 60, r2: Double = r1 - 120,
    modifier: Modifier
) {

    val planetSignImages: Map<String, ImageBitmap?> = planetSignDrawables.mapValues { entry: Map.Entry<String, Int> ->
        planetSignDrawables[entry.key]?.let { drawable ->
            ImageBitmap.imageResource(drawable)
        }
    }

    val planetSignPolarCoords: Map<String, Pair<Double, Double>?> = ephemerides.mapValues { entry ->
        ephemerides[entry.key]?.let {ephemeris: Ephemeris ->
            ephemeris.eclipticCoords(zdt).toPolar()
        }
    }

    // calculate angle offset of zodiac
    val zodiacAngleOffset = zdt.ascendant()
    DrawNatalChart(r0 = r0, angleOffset = zodiacAngleOffset,  modifier = modifier)


}

@Composable
fun DrawNatalChart(uiState: OrreryUIState) {

}
@Preview
@Composable
fun PreviewDrawNatalChart() {
    var width by remember { mutableStateOf(1080.0) }
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            width = coordinates.boundsInRoot().width.toDouble()
        }
    ) {
        DrawNatalChart(zdt = ZonedDateTime.now().plusHours(2), r0 = width/2, modifier = Modifier
            .align(Alignment.Center)
        )
    }
}