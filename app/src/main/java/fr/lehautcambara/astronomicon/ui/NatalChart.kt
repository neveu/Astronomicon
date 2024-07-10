package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.astrology.ascendant
import fr.lehautcambara.astronomicon.astrology.planetSignDrawables
import fr.lehautcambara.astronomicon.astrology.zodiacSignDrawables
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.PolarCoords
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.PlanetClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import rcosd
import rsind
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawDividers(
    index: Int,
    r0: Double,
    r1: Double,
    angleOffset: Double,
    color: Color = Color.Black,
) {
    val angle = (index * 30F) + angleOffset // 180 so we start on the left
    val x0 = -rcosd(r0, angle).toFloat()
    val y0 = rsind(r0, angle).toFloat()
    val x1 = -rcosd(r1, angle).toFloat()
    val y1 = rsind(r1, angle).toFloat()
    drawLine(color, start = Offset(x1,y1), end = Offset(x0, y0), strokeWidth = 5F)
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



fun DrawScope.drawPlanet(planetBitmap: ImageBitmap, r: Double, angle: Double, scale: Double = 1.0) {
    val sc = scale.toFloat()
    scale(scale = sc) {
        drawBitmapImage( r/sc, angle, planetBitmap)
    }
}


@Composable
fun DrawPlanetSigns(
    r: Double,
    planetSignImages: Map<String, ImageBitmap?>,
    planetSignEclipticCoords: Map<String, Coords?>,
    zodiacAngleOffset: Double,
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        planetSignEclipticCoords["Earth"]?.let {earthCoords ->
            for ((planet, bitmap) in planetSignImages) {
                if (planet == "Earth") continue
                planetSignEclipticCoords[planet]?.let { planetCoords: Coords ->
                    bitmap?.let {planetBitmap ->
                        val geocentricCoords = earthCoords.fromTo(planetCoords)
                        val polarGeocentricCoords: PolarCoords = geocentricCoords.toPolar()
                        val angle = (polarGeocentricCoords.a + zodiacAngleOffset) % 360
                        drawPlanet(planetBitmap, r -120, angle)
                    }
                }
            }
        }

    }
}

@Composable
fun DrawNatalChart(uiState: OrreryUIState, modifier: Modifier) {
    val zdt = uiState.zonedDateTime
    DrawNatalChart(zdt, modifier = modifier)
}
@Composable
fun DrawNatalChart(zdt: ZonedDateTime = ZonedDateTime.now(), radiusDp: Dp = 400.dp, modifier: Modifier) {
    var layoutWidth by remember { mutableStateOf(1080F) } // Canvas coords
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .width(radiusDp)
        .height((radiusDp))
        .background(Color(0x00000000))
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            layoutWidth = coordinates.boundsInRoot().width
            size = coordinates.size.toSize()
        }
    ) {
        DrawNatalChart(zdt = zdt, r0 = (layoutWidth/2.0), modifier = Modifier
            .align(Alignment.Center)
        )
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

    val planetSignEclipticCoords: Map<String, Coords?> = ephemerides.mapValues { entry ->
        ephemerides[entry.key]?.let {ephemeris: Ephemeris ->
            ephemeris.eclipticCoords(zdt)
        }
    }

    val planetSignPolarCoords: Map<String, PolarCoords?> = ephemerides.mapValues { entry ->
        ephemerides[entry.key]?.let {ephemeris: Ephemeris ->
            ephemeris.eclipticCoords(zdt).toPolar()
        }
    }

    // calculate angle offset of zodiac
    val zodiacAngleOffset = zdt.ascendant()
    DrawNatalChart(r0 = r0, angleOffset = zodiacAngleOffset,  modifier = modifier)
    DrawPlanetSigns(r0, planetSignImages, planetSignEclipticCoords, zodiacAngleOffset, modifier = modifier)
    Image(painterResource(id = R.drawable.accentercrop),
        contentDescription = "",
        modifier = modifier.clickable { Kbus.post(PlanetClickEvent()) })
}
@OptIn(ExperimentalTextApi::class)
@Composable
fun DrawNatalChart(r0: Double, r1: Double = r0 - 70, r2: Double = r1-120, angleOffset: Double = 0.0,  modifier: Modifier) {
    val zodiacImages: Array<ImageBitmap> = zodiacSignDrawables.map {
        ImageBitmap.imageResource(id = it)
    }.toTypedArray()


    Canvas(modifier = modifier) {
        drawZodiac(r0, r1, angleOffset, zodiacImages)
        drawHouses(r1, r2)
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawZodiac(
    r0: Double,
    r1: Double,
    angleOffset: Double = 0.0,
    imageArray: Array<ImageBitmap>,
) {
    drawCircle(color = Color(0xffdda680), style = Fill,radius = r0.toFloat(), alpha = 0.8F )
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r0.toFloat())
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r1.toFloat())
    for (index in 0..11) {
        drawDividers(index, r0, r1, angleOffset, color = Color.Black)
        drawZodiacSign(index, r0, r1, angleOffset,  imageArray,)
    }
}


private fun DrawScope.drawHouses(r1: Double, r2: Double, ) {
    //drawCircle(color = Color(0xffdda680), style = Fill, radius = r2.toFloat(), alpha = 1.0F, blendMode = BlendMode.SrcOver)
    //drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r2.toFloat())
    for (index in 0..11) {
        drawDividers(index, r1, r2, angleOffset = 0.0, color = Color.Black)
    }
}



@Preview
@Composable
fun PreviewDrawNatalChart(zdt: ZonedDateTime = ZonedDateTime.now()) {
    val zdtj2000 = ZonedDateTime.of(2000,9, 3, 12, 0, 0, 0, ZoneId.of("GMT"))
    Box(modifier = Modifier) {
        val modifier = Modifier.align(Alignment.Center)
        DrawNatalChart(zdtj2000, modifier = modifier)
    }

}