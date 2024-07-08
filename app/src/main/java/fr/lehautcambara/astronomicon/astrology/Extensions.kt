package fr.lehautcambara.astronomicon.astrology

import android.util.Log
import cosd
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.ephemeris.LunarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.SolarEphemeris
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import sind
import tand
import java.lang.Math.atan2
import java.time.Duration
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.GregorianCalendar

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
    val now = this
    val duration = Duration.between(j2000, now,  )
    val secs: Double = duration.toMillis() / 1000.0
    val days = secs / (60 * 60 * 24)
    return days
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
    val x = (sind(lst) * cosd(inclination)) + (tand(latitude)*sind(inclination))
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

fun natalDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, zone: ZoneId) {
    val bday: ZonedDateTime =  ZonedDateTime.of(year, month, day, hour, minute, 0, 0, zone)
    val j2000 = ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, zone)

    val offsetDateTime = bday.toOffsetDateTime()
    val offsetTime: OffsetTime = offsetDateTime.toOffsetTime()

    val zoneOffset: ZoneOffset = ZoneOffset.ofHoursMinutes(offsetTime.hour, offsetTime.minute)
    val offset: ZoneId = ZoneId.ofOffset("GMT", zoneOffset)
    val zoneIDs: Set<String> = ZoneId.getAvailableZoneIds()
    val lst = bday.sidereal()
    Log.d("natalDate", "")
}
