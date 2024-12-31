package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.astrology.ascendant
import fr.lehautcambara.astronomicon.astrology.aspects.Aspect
import fr.lehautcambara.astronomicon.astrology.aspects.AspectType
import fr.lehautcambara.astronomicon.astrology.aspects.aspects
import fr.lehautcambara.astronomicon.astrology.planetSignDrawables
import fr.lehautcambara.astronomicon.astrology.planetSignRetroDrawables
import fr.lehautcambara.astronomicon.astrology.zodiacSignDrawables
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.PolarCoords
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.AspectClickEvent
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.events.PlanetSignClickEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.rcosd
import fr.lehautcambara.astronomicon.rsind
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

fun DrawScope.drawZodiac(
    outerRadius: Double,
    proportions: NatalChartProportions,
    angleOffset: Double = 0.0,
    imageArray: Array<ImageBitmap>,
) {
    val r0 = outerRadius
    val r1 = r0*proportions.innerZodiacRadius
    drawCircle(color = Color(0xffdda680), style = Fill,radius = r0.toFloat(), alpha = 0.8F )
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r0.toFloat())
    drawCircle(color = Color.Black, style = Stroke(width = 5F), radius = r1.toFloat())
    for (index in 0..11) {
        drawDividers(index, r0, r1, angleOffset, color = Color.Black)
        drawZodiacSign(index, r0, r1, angleOffset,  imageArray,)
    }
}

private fun DrawScope.drawHouses(outerRadius: Double, proportions: NatalChartProportions) {
    val r1 = outerRadius * proportions.outerHouseRadius
    val r2 = outerRadius * proportions.innerHouseRadius
    for (index in 0..11) {
        drawDividers(index, r1, r2, angleOffset = 0.0, color = Color.Black)
    }
}

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
    image: ImageBitmap?,
    imageScale: Double = 0.4
) {
    if (image == null) return
    val x = -rcosd(meanRadius, angle)
    val y = rsind(meanRadius, angle)
    val x0 = (x - image.width / 5).toInt()
    val y0 = (y - (image.height / 5)).toInt()
    val intOffset = IntOffset(x0, y0)
    val intSize = IntSize((image.width *imageScale).toInt(), (image.height * imageScale).toInt())
    drawIntoCanvas { canvas ->
        drawImage(image, dstOffset = intOffset, dstSize = intSize)
    }
}

data class PlanetSignPolarCoords(
    val planet: String,
    val coords: Coords,
    val planetBitmap: ImageBitmap? = null, 
    val radius: Double,
    val angle: Double )

@Composable
fun DrawPlanet(planet: PlanetSignPolarCoords?, sizeDp: Dp, planetSymbolDrawable: Int?, modifier: Modifier = Modifier) {
    planetSymbolDrawable?.let { id ->
        planet?.let { planet ->
            val x = -rcosd(planet.radius, planet.angle)
            val y = rsind(planet.radius, planet.angle)

            Image(painterResource(id = id), planet.planet,
                modifier = modifier
                    .absoluteOffset { IntOffset(x.toInt(), y.toInt()) }
                    .size(sizeDp)
                    .clickable {
                        Kbus.post(PlanetSignClickEvent(planet))
                    }
            )
        }
    }
}

fun planetSignPolarCoords(
    planetSignEclipticCoords: Map<String, Coords?>,
    earthCoords: Coords,
    zodiacAngleOffset: Double,
    r: Double,
): Map<String?, PlanetSignPolarCoords?> {
    return planetSignEclipticCoords
        .filter { entry -> entry.key != "Earth" }
        .mapValues { entry: Map.Entry<String?, Coords?> ->
            entry.key?.let { planetString ->
                entry.value?.let { planetCoords: Coords ->
                    val polarGeocentricCoords: PolarCoords =
                        earthCoords.fromTo(planetCoords).toPolar()
                    val angle = (polarGeocentricCoords.a + zodiacAngleOffset) % 360
                    PlanetSignPolarCoords(
                        planetString,
                        coords = planetCoords,
                        radius = r,
                        angle = angle
                    )
                }
            }
        }
}

@Composable
fun DrawPlanetsAndAspects( // Compose Images instead of DrawScope imagebitmaps
    outerRadius: Double,
    proportions: NatalChartProportions,
    planetSignCoords: Map<String, Coords?>?,
    retrogradeMap: Map<String, Boolean>,
    significantAspectPairs: List<Aspect>?,
    zodiacAngleOffset: Double,
    modifier: Modifier
) {
    val sizeDp = pixToDp(outerRadius * proportions.planetGlyphScale )
    planetSignCoords?.let {planetCoords ->
        planetSignCoords["Earth"]?.let { earthCoords ->
            val planets: Map<String?, PlanetSignPolarCoords?> = planetSignPolarCoords(
                planetCoords,
                earthCoords,
                zodiacAngleOffset,
                outerRadius * proportions.planetGlyphRadius,
            )
            planets.keys.filterNotNull().forEach { p ->
                planets[p]?.let { planet ->
                    retrogradeMap[p]?.let { isRetro ->
                        if (isRetro) {
                            planetSignRetroDrawables[p]?.let { drawable ->
                                DrawPlanet(planet, sizeDp, drawable, modifier)
                            }
                        } else {
                            planetSignDrawables[p]?.let { drawable ->
                                DrawPlanet(planet, sizeDp, drawable, modifier)
                            }
                        }
                    }
                }
            }
            DrawAspects(planets, significantAspectPairs, outerRadius * proportions.aspectGlyphRadius, pixToDp(outerRadius * proportions.aspectGlyphScale ), modifier)
        }
    }
}

@Composable
fun DrawAspects(
    planets: Map<String?, PlanetSignPolarCoords?>,
    significantAspectPairs: List<Aspect>?,
    r: Double,
    sizeDp: Dp,
    modifier: Modifier
) {
    significantAspectPairs?.forEach { aspect: Aspect ->
        DrawAspect(aspect, planets,  r, sizeDp, modifier)
    }
}

@Composable
fun DrawAspect(
    aspect: Aspect,
    planets: Map<String?, PlanetSignPolarCoords?>,
    r: Double,
    sizeDp: Dp,
    modifier: Modifier
) {
    val b1 = aspect.body1.toString()
    val b2 = aspect.body2.toString()
    planets[b1]?.angle?.let { angle1 ->
        planets[b2]?.angle?.let { angle2 ->
            val start = Offset(-rcosd(r, angle1).toFloat(), rsind(r, angle1).toFloat())
            val end = Offset(-rcosd(r, angle2).toFloat(), rsind(r, angle2).toFloat())

            DrawAspectLine(aspect, start, end, modifier, )
            DrawGlyphs(aspect,  start, end, sizeDp, modifier,)
        }
    }
}

@Composable
private fun DrawGlyphs(
    aspect: Aspect,
    offset1: Offset,
    offset2: Offset,
    sizeDp: Dp,
    modifier: Modifier,
    ) {
        DrawAspectGlyph(aspect, offset1, sizeDp, modifier, )
        DrawAspectGlyph(aspect, offset2, sizeDp, modifier)
}

@Composable
private fun DrawAspectGlyph(
    aspect: Aspect,
    offset: Offset,
    sizeDp: Dp,
    modifier: Modifier,
) {
    aspect.glyph?.let { glyph: Int ->
        Image(
            painterResource(id = glyph),
            aspect.name,
            modifier = modifier
                .absoluteOffset {
                    IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                }
                .size(sizeDp)
                .clickable { Kbus.post(AspectClickEvent(aspect)) },
        )
    }

}

@Composable
private fun DrawAspectLine(
    aspect: Aspect,
    offset1: Offset,
    offset2: Offset,
    modifier: Modifier,
    ) {
    Canvas(modifier = modifier) {
        drawLine(
            color = aspect.aspectType.color,
            start = offset1,
            end = offset2,
            strokeWidth = 5F
        )
    }
}

@Composable
fun DrawZodiacAndHouses(outerRadius: Double, proportions: NatalChartProportions, angleOffset: Double = 0.0, modifier: Modifier) {
    val zodiacImages: Array<ImageBitmap> = zodiacSignDrawables.map {
        ImageBitmap.imageResource(id = it)
    }.toTypedArray()

    Canvas(modifier = modifier) {
        drawZodiac(outerRadius, proportions, angleOffset, zodiacImages)
        drawHouses(outerRadius, proportions)
    }
}
@Composable
fun DrawNatalChart(
    outerRadius: Double,
    modifier: Modifier,
    zdt: ZonedDateTime = ZonedDateTime.now(),
    proportions: NatalChartProportions = NatalChartProportions(),
    significantAspectPairs: List<Aspect>? = null,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    ephemerides: Map<String, Ephemeris> = fr.lehautcambara.astronomicon.astrology.ephemerides(),
) {
    val aspectSignImages: MutableMap<AspectType, ImageBitmap?> = mutableMapOf()
    AspectType.values().forEach { aspectType ->
        aspectType.glyph?.let { glyph ->
            aspectSignImages[aspectType]= ImageBitmap.imageResource(glyph)
        }
    }

    val planetSignEclipticCoords: Map<String, Coords?> = ephemerides.mapValues { entry ->
        entry.value.eclipticCoords(zdt)
    }

    val retrogradeMap: Map<String, Boolean> = ephemerides.mapValues { entry ->
        ephemerides["Earth"]?.inRetrograde(entry.value, zdt)?:false
    }
    val dpValue = pixToDp(outerRadius * proportions.innerHouseRadius)

    // calculate angle offset of zodiac
    val zodiacAngleOffset = zdt.ascendant(longitude = longitude, latitude = latitude)
    DrawZodiacAndHouses(outerRadius = outerRadius, proportions, angleOffset = zodiacAngleOffset,  modifier = modifier)
    Image(painterResource(id = R.drawable.accentercrop),
        contentDescription = "",
        modifier = modifier
            .size(dpValue)
            .clickable { Kbus.post(PlanetClickEvent()) })
    DrawPlanetsAndAspects(outerRadius, proportions,  planetSignEclipticCoords, retrogradeMap, significantAspectPairs, zodiacAngleOffset, modifier = modifier)
}

@Composable
private fun pixToDp(
    pixels: Double,
): Dp {
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = ((pixels * 2.0) / screenPixelDensity).dp
    return dpValue
}

@Composable
fun DrawNatalChart(
    outerRadius: Double,
    zdt: ZonedDateTime = ZonedDateTime.now(),
    significantAspectPairs: List<Aspect>? = null,
    modifier: Modifier = Modifier,
    proportions: NatalChartProportions = NatalChartProportions(),
    latitude: Double = 0.0,
    longitude: Double = 0.0,

    ) {
    Box(
        modifier = modifier
            .background(Color(0x00000000))
            .clickable { Kbus.post(PlanetClickEvent()) }

    ) {
        DrawNatalChart(
            outerRadius = outerRadius,
            modifier = modifier,
            zdt = zdt,
            proportions = proportions,
            significantAspectPairs = significantAspectPairs,
            latitude = latitude,
            longitude = longitude,
            ephemerides = fr.lehautcambara.astronomicon.astrology.ephemerides(),
        )
    }
}



@Composable
fun DrawNatalChart(uiState: StateFlow<OrreryUIState>, size: Size, modifier: Modifier = Modifier) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    with(orreryUIState) {
        val zdt = zonedDateTime
        val aspectPairs: List<Aspect> = aspects

        DrawNatalChart(
            zdt = zdt,
            significantAspectPairs = aspectPairs,
            outerRadius = 0.975 * size.width.toDouble() / 2.0,
            proportions = proportions,
            latitude = latitude,
            longitude = longitude,
            modifier = modifier,

            )
    }
}

@Preview
@Composable
fun PreviewDrawNatalChart(zdt: ZonedDateTime = ZonedDateTime.now()) {
    val zdtj2000 = ZonedDateTime.of(2000,9, 3, 12, 0, 0, 0, ZoneId.of("GMT"))
    val aspectPairs: List<Aspect> = aspects(zdt)
    val width = 720.0
    val proportions = NatalChartProportions()
    Box(modifier = Modifier
        .size(320.dp)
    ) {
        DrawNatalChart(zdt=zdt, significantAspectPairs =  aspectPairs, outerRadius = width/2.0, proportions = proportions,  modifier = Modifier.align(Alignment.Center))

    }
}