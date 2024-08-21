package fr.lehautcambara.astronomicon.astrology

import android.util.Log
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.acosd
import fr.lehautcambara.astronomicon.asind
import fr.lehautcambara.astronomicon.astrology.aspects.AspectType
import fr.lehautcambara.astronomicon.atand
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import fr.lehautcambara.astronomicon.sind
import fr.lehautcambara.astronomicon.tand
import java.lang.Math.atan2
import java.lang.Math.floor
import java.time.Duration
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.truncate

val zodiacNames = arrayOf(
    "Aries",
    "Taurus",
    "Gemini",
    "Cancer",
    "Leo",
    "Virgo",
    "Libra",
    "Scorpio",
    "Sagittarius",
    "Capricorn",
    "Aquarius",
    "Pisces"
    )

val zodiacSignDrawables = arrayOf(
    R.drawable.aries,
    R.drawable.taurus,
    R.drawable.gemini,
    R.drawable.cancer,
    R.drawable.leo,
    R.drawable.virgo,
    R.drawable.libra,
    R.drawable.scorpio,
    R.drawable.sagitarius,
    R.drawable.capricorn,
    R.drawable.aquarius,
    R.drawable.pisces,
    )

val planetSignDrawables = hashMapOf<String, Int> (
    "Sun" to R.drawable.sun_symbol,
    "Earth" to R.drawable.earth_symbol,
    "Mercury" to R.drawable.mercury_symbol,
    "Moon" to  R.drawable.moon_symbol,
    "Venus" to R.drawable.venus_symbol,
    "Mars" to R.drawable.mars_symbol,
    "Jupiter" to R.drawable.jupiter_symbol,
    "Saturn" to R.drawable.saturn_symbol,
)



val ephemerides: HashMap<String, Ephemeris> = hashMapOf<String, Ephemeris>(

    "Sun" to SolarEphemeris( KeplerianElements.Sun()),
    "Earth" to SolarEphemeris( KeplerianElements.EmBary()),
    "Mercury" to SolarEphemeris( KeplerianElements.Mercury()),
    "Moon" to  LunarEphemeris(),
    "Venus" to SolarEphemeris( KeplerianElements.Venus()),
    "Mars" to SolarEphemeris( KeplerianElements.Mars()),
    "Jupiter" to SolarEphemeris( KeplerianElements.Jupiter()),
    "Saturn" to SolarEphemeris( KeplerianElements.Saturn()),

)

class Zenith {
    companion object {
        val Official = 90.0 + 50.0/60.0 // 90 degrees 50'
        val Civil = 96.0
        val Nautical = 102.0
        val Astronomical = 108.0
    }
}

fun Calendar.convertToJulianCentury(): Double {
    val j2000 = GregorianCalendar(2000, Calendar.JANUARY, 1, 12, 0, 0)
    val Tmsec: Double = (this.timeInMillis - j2000.timeInMillis).toDouble()
    val Tsec = Tmsec / 1000.0
    val Tday = Tsec / (60 * 60 * 24)
    val Tyear = Tday / 365.242
    return Tyear / 100.0 // time in julian centuries
}

fun ZonedDateTime.convertToJulianCentury(): Double {
    val days = convertToJulianDays()
    val years = days / 365.242
    return years/100.0
}

fun ZonedDateTime.convertToJulianDays() : Double {
    val j2000 = ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.UTC))
    val duration = Duration.between(j2000, this,  )
    val secs: Double = duration.toMillis() / 1000.0
    return secs / (60 * 60 * 24)
}

fun ZonedDateTime.sidereal(longitude: Double? = null): Double { // approximate longitude from timezone offset
    val ut = this.withZoneSameInstant(ZoneId.of("GMT")) //
    val timeInDecimalHoursUTC = ut.hour + ut.minute/60.0
    val d = convertToJulianDays().toInt()
    val long = longitude?:(15.0 * this.offset.totalSeconds / 3600.0)
    val st = 100.46 + (0.985647 * d) + long + (15.0 *  timeInDecimalHoursUTC)
    return st
}

fun ZonedDateTime.sidereal360(longitude: Double? = null) : Double {
    val st = sidereal(longitude) % 360.0
    return st
}

fun ZonedDateTime.ascendant(longitude: Double? = null, latitude: Double = 0.0): Double {
    val lst = sidereal360(longitude)
    val inclination = 23.44
    val y = -cosd(lst)
    val x = (sind(lst) * cosd(inclination)) + (tand(latitude) * sind(inclination))
    val asc0 = -atan2(y,x) * (180.0/Math.PI)
    val ascendant = if (asc0 < 180.0) asc0 + 180.0 else asc0 - 180
    return ascendant
}
fun ZonedDateTime.ascendantToZodiacIndex(longitude: Double? = null, latitude: Double = 0.0): Int {
    return (ascendant(longitude, latitude)/30.0).toInt()
}
fun ZonedDateTime.ascendantToZodiacSign(longitude: Double? = null, latitude: Double = 0.0): String {
    return zodiacNames[(ascendant(longitude, latitude)/30.0).toInt()]
}
fun ZonedDateTime.ascendantSignDegrees(longitude: Double? = null, latitude: Double = 0.0): Double {
    return ascendant(longitude, latitude) % 30
}

fun ZonedDateTime.longitudeHour(longitude: Double? = null): Double {
    return ((longitude?:(15.0 * this.offset.totalSeconds / 3600.0)) / 15.0) % 360
}

private fun meanAnomaly(t: Double) = (0.9856 * t) - 3.289
fun trueLongitude(meanAnomaly : Double): Double {
    val M = meanAnomaly
    return (M + (1.916 * sind(M)) + (0.020 * sind(2.0 * M)) + 282.634) % 360
}

private fun risingT(n: Int, longitudeHour: Double) = n + ((6 - longitudeHour) / 24.0)
private fun settingT(n: Int, longitudeHour: Double) = n + ((18 - longitudeHour) / 24.0)

fun ZonedDateTime.sunrise(longitude: Double? = null, latitude: Double = 0.0, zenith: Double): ZonedDateTime? {
    return sunriseSunset(true, longitude, latitude, zenith)
}

fun ZonedDateTime.sunset(longitude: Double? = null, latitude: Double = 0.0, zenith: Double): ZonedDateTime? {
    return sunriseSunset(false, longitude, latitude, zenith)
}

private fun ZonedDateTime.sunriseSunset(rising: Boolean = true, longitude: Double? = null, latitude: Double = 0.0, zenith: Double): ZonedDateTime? {
    val n: Int = dayOfYear
    val longHour: Double = longitudeHour(longitude)
    val t: Double = if (rising) risingT(n,longHour) else settingT(n, longHour)

    val L = trueLongitude(meanAnomaly(t))
    val Lquadrant = floor(L / 90.0) * 90.0
    var ra = atand(0.91764 * tand(L)) % 360 // right ascension
    val RAquadrant = (floor(ra / 90.0)) * 90.0
    ra += (Lquadrant - RAquadrant) // right ascension value needs to be in the same quadrant as L
    ra = ra / 15.0 // right ascension value needs to be converted into hours
    val sinDec = 0.39782 * sind(L) // calculate  the Sun's declination
    val cosDec = cosd(asind(sinDec))
    val cosH = (cosd(zenith) - (sinDec * sind(latitude))) / (cosDec * cosd(latitude))  // calculate the Sun's local hour angle
    if (rising && (cosH >  1)) return null else if (cosH < -1) return null //the sun never rises/sets on this location (on the specified date)

    // finish calculating H and convert into hours
    val H = (if (rising) 360.0 - acosd(cosH) else acosd(cosH)) / 15.0
    val T = H + ra - (0.06571 * t) - 6.622 // calculate local mean time of rising/setting
    val ut = (T - longHour) % 24
    val localTime: Double = (ut + (this.offset.totalSeconds / 3600)).mod(24.0)
    val hour = truncate(localTime).toInt()
    val minute = truncate((localTime - hour) * 60).toInt()
    val seconds = ((((localTime - hour) * 60) - minute) * 60).toInt()
    return ZonedDateTime.from(this)
        .withHour(hour)
        .withMinute(minute)
        .withSecond(seconds)
        .truncatedTo(ChronoUnit.SECONDS)
}
fun natalDate(zdt: ZonedDateTime, latitude: Double = 0.0, longitude: Double = 0.0) {
    val sunrise = zdt.sunrise(longitude, latitude, zenith = Zenith.Official)
    val sunset = zdt.sunset(longitude, latitude, zenith = Zenith.Official)
}

fun natalDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, zone: ZoneId) {
    val bday: ZonedDateTime =  ZonedDateTime.of(year, month, day, hour, minute, 0, 0, zone)
    val j2000 = ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, zone)
    val sunrise = bday.sunrise(latitude = 43.07, longitude = -89.4, zenith = Zenith.Official)
    val sunset = bday.sunset(latitude = 51.51, zenith = Zenith.Official)

    val offsetDateTime = bday.toOffsetDateTime()
    val offsetTime: OffsetTime = offsetDateTime.toOffsetTime()

    val zoneOffset: ZoneOffset = ZoneOffset.ofHoursMinutes(offsetTime.hour, offsetTime.minute)
    val offset: ZoneId = ZoneId.ofOffset("GMT", zoneOffset)
    val zoneIDs: Set<String> = ZoneId.getAvailableZoneIds()
    val lst = bday.sidereal()
    Log.d("natalDate", "")
}
