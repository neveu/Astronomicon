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
import fr.lehautcambara.astronomicon.astrology.Aspects.Aspect
import fr.lehautcambara.astronomicon.astrology.ascendant
import fr.lehautcambara.astronomicon.astrology.planetSignDrawables
import fr.lehautcambara.astronomicon.astrology.zodiacSignDrawables
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.PolarCoords
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.rcosd
import fr.lehautcambara.astronomicon.rsind
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

data class PlanetSignPolarCoords(val planet: String, val planetBitmap: ImageBitmap? = null, val radius: Double, val angle: Double )

fun DrawScope.drawPlanet(planet: PlanetSignPolarCoords?) {
    planet?.planetBitmap?.let {imageBitmap ->
        drawPlanet(imageBitmap, planet.radius, planet.angle )
    }
}

fun DrawScope.drawPlanet(planetBitmap: ImageBitmap, r: Double, angle: Double, scale: Double = 1.0) {
    val sc = scale.toFloat()
    scale(scale = sc) {
        drawBitmapImage( r/sc, angle, planetBitmap)
    }
}

@Composable
fun DrawPlanetsAndAspects(
    r: Double,
    r2: Double,
    planetSignImages: Map<String, ImageBitmap?>,
    planetSignCoords: Map<String, Coords?>,
    significantAspectPairs: List<Aspect>?,
    zodiacAngleOffset: Double,
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        planetSignCoords["Earth"]?.let { earthCoords ->
            val planets: Map<String?, PlanetSignPolarCoords?> = planetSignPolarCoords(
                planetSignImages,
                planetSignCoords,
                earthCoords,
                zodiacAngleOffset,
                r
            )
            planets.keys.filterNotNull().forEach { p ->
                drawPlanet(planets[p])
            }
            drawAspects(planets, significantAspectPairs, r2)
        }
    }
}

fun DrawScope.drawAspects(planets: Map<String?, PlanetSignPolarCoords?>, significantAspectPairs: List<Aspect>?, r: Double) {
    significantAspectPairs?.forEach { aspect: Aspect ->
        drawAspect(aspect, planets, r)
    }
}

private fun DrawScope.drawAspect(
    aspect: Aspect,
    planets: Map<String?, PlanetSignPolarCoords?>,
    r: Double
) {
    val b1 = aspect.body1.toString()
    val b2 = aspect.body2.toString()
    planets[b1]?.angle?.let { angle1 ->
        planets[b2]?.angle?.let { angle2 ->
            val offset1 = Offset(-rcosd(r, angle1).toFloat(), rsind(r, angle1).toFloat())
            val offset2 = Offset(-rcosd(r, angle2).toFloat(), rsind(r, angle2).toFloat())
            drawLine(color = aspect.aspectType.color, start = offset1, end = offset2, strokeWidth = 5F)
        }
    }
}

private fun planetSignPolarCoords(
    planetSignImages: Map<String, ImageBitmap?>,
    planetSignEclipticCoords: Map<String, Coords?>,
    earthCoords: Coords,
    zodiacAngleOffset: Double,
    r: Double
): Map<String?, PlanetSignPolarCoords?> {
    return planetSignEclipticCoords
        .filter { entry -> entry.key != "Earth" }
        .mapValues { entry ->
            entry.key?.let { planetString ->
                entry.value?.let { planetCoords ->
                    val polarGeocentricCoords: PolarCoords =
                        earthCoords.fromTo(planetCoords).toPolar()
                    val angle = (polarGeocentricCoords.a + zodiacAngleOffset) % 360
                    val planetBitmap = planetSignImages[entry.key]
                    PlanetSignPolarCoords(planetString, planetBitmap, r - 120, angle)
                }
            }
        }
}

@Composable
fun DrawNatalChart(uiState: OrreryUIState, modifier: Modifier) {
    val zdt = uiState.zonedDateTime
    val aspectPairs: List<Aspect> = uiState.aspects
    DrawNatalChart(zdt, aspectPairs, modifier = modifier)
}
@Composable
fun DrawNatalChart(zdt: ZonedDateTime = ZonedDateTime.now(), significantAspectPairs: List<Aspect>? = null, radiusDp: Dp = 400.dp, modifier: Modifier) {
    var layoutWidth by remember { mutableStateOf(1080F) } // Canvas coords
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .width(radiusDp)
        .height(radiusDp)
        .background(Color(0x00000000))
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            layoutWidth = coordinates.boundsInRoot().width
            size = coordinates.size.toSize()
        }
    ) {

        DrawNatalChart(zdt = zdt, significantAspectPairs, r0 = (layoutWidth/2.0), modifier = Modifier
            .align(Alignment.Center)
        )
    }
}

@Composable
fun DrawNatalChart (
    zdt: ZonedDateTime = ZonedDateTime.now(),
    significantAspectPairs: List<Aspect>? = null,
    r0: Double, r1: Double = r0 - 60, r2: Double = r1 - 120,
    ephemerides: Map<String, Ephemeris> = fr.lehautcambara.astronomicon.astrology.ephemerides,
    modifier: Modifier
) {

    val planetSignImages: Map<String, ImageBitmap?> = planetSignDrawables.mapValues { entry: Map.Entry<String, Int> ->
        planetSignDrawables[entry.key]?.let { drawable ->
            ImageBitmap.imageResource(drawable)
        }
    }

    val planetSignEclipticCoords: Map<String, Coords?> = ephemerides.mapValues { entry ->
        entry.value.eclipticCoords(zdt)
    }

    val planetSignPolarCoords: Map<String, PolarCoords?> = planetSignEclipticCoords.mapValues { entry: Map.Entry<String, Coords?> ->
        entry.value?.toPolar()
    }

    // calculate angle offset of zodiac
    val zodiacAngleOffset = zdt.ascendant()
    DrawNatalChart(r0 = r0, angleOffset = zodiacAngleOffset,  modifier = modifier)
    Image(painterResource(id = R.drawable.accentercrop),
        contentDescription = "",
        modifier = modifier.clickable { Kbus.post(PlanetClickEvent()) })
    DrawPlanetsAndAspects(r0, r2, planetSignImages, planetSignEclipticCoords, significantAspectPairs, zodiacAngleOffset, modifier = modifier)
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