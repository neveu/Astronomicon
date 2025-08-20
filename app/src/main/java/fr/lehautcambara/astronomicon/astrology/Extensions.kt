package fr.lehautcambara.astronomicon.astrology

import android.util.Log
import fr.lehautcambara.astronomicon.acosd
import fr.lehautcambara.astronomicon.acotd
import fr.lehautcambara.astronomicon.angle360
import fr.lehautcambara.astronomicon.asind
import fr.lehautcambara.astronomicon.atand
import fr.lehautcambara.astronomicon.cosd
import fr.lehautcambara.astronomicon.cotand
import fr.lehautcambara.astronomicon.ephemeris.Dms
import fr.lehautcambara.astronomicon.sind
import fr.lehautcambara.astronomicon.tand
import java.lang.Math.atan2
import java.lang.Math.floor
import java.time.Duration
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.abs
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

val verysmall = 1E-10

class Zenith {
    companion object {
        val Official = 90.0 + 50.0/60.0 // 90 degrees 50'
        val Civil = 96.0
        val Nautical = 102.0
        val Astronomical = 108.0
    }
}

val J2000 = ZonedDateTime.of( 2000, 1, 1, 12, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.UTC))


fun Calendar.convertToJ2000Century(): Double {
    val j2000 = GregorianCalendar(2000, Calendar.JANUARY, 1, 12, 0, 0)
    val Tmsec: Double = (this.timeInMillis - j2000.timeInMillis).toDouble()
    val Tsec = Tmsec / 1000.0
    val Tday = Tsec / (60 * 60 * 24)
    val Tyear = Tday / 365.242
    return Tyear / 100.0 // time in julian centuries
}

// https://aa.usno.navy.mil/faq/GAST#:~:text=The%20local%20mean%20or%20apparent,to%20the%20Greenwich%20sidereal%20time.
fun ZonedDateTime.convertToJulianDate() : Double {
    // 1/1/4713 BC in proleptic gregorian calendar is
    val year0 = ZonedDateTime.of( -4713, 11, 24, 12, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.UTC))
    val duration = Duration.between(year0, this)
    val jd = duration.toSeconds().toDouble() / (60 * 60 * 24)
    return jd
}

fun ZonedDateTime.julianDayNumber() : Double {
    val time = (hour + (minute/60.0))/24.0
    val jdn = convertToJulianDate() - time
    return jdn
}

fun ZonedDateTime.convertToJ2000Days() : Double {
    val duration = Duration.between(J2000, this,  )
    val secs: Double = duration.toMillis() / 1000.0
    return secs / (60 * 60 * 24)
}

fun ZonedDateTime.convertToJ2000Century(): Double {
    val days = convertToJ2000Days()
    val years = days / 365.242
    return years/100.0
}

fun jdToFactorT(jd: Double): Double {
    val t = (jd - 2451545) / 36525.0
    return t
}

fun ZonedDateTime.factorT(): Double {
    val jd = convertToJulianDate()
    val t = jdToFactorT(jd)
    return t
}

fun ZonedDateTime.siderealTime0UT(): Double {
    val jdn = julianDayNumber()
    val t = jdToFactorT(jdn)
    val st0 = angle360( 100.46061837 + (36000.770053608 * t) + (0.000387933 * t * t) - (t*t*t)/38710000)
    val decimalHours =  st0/15.0
    return decimalHours
}

fun to24Hours(hours: Double): Double {
    val h = ((hours % 24) + 24) % 24
    return h
}

fun ZonedDateTime.siderealTime(): Double {
    val st0 = siderealTime0UT()
    val hours: Double = ((hour + minute/60.0 + second/3600.0) * 1.00273790935)
    val st = st0 + hours
    val st24 = to24Hours(st)
    return st24
}

fun ZonedDateTime.localSiderealTime(longitude: Double) : Double {
    val longitudeInHours = longitude/15.0
    val lst =  siderealTime() + longitudeInHours
    return lst
}

fun ZonedDateTime.localSiderealTimeHourMinute(longitude: Double) : ZonedDateTime {
    val lst: Double = localSiderealTime(longitude) // decimal hours
    val h = (lst % 24).toInt()
    val min = (((lst - h) * 60) % 60).toInt()
    val lstZdt = ZonedDateTime.of(year, monthValue, dayOfMonth, h, min, 0, 0, zone )
    return lstZdt
}

fun ZonedDateTime.localSiderealTimeLocalTime(longitude: Double) : LocalTime {
    val zdt = localSiderealTimeHourMinute(longitude)
    val lt = LocalTime.of(hour, minute)
    return lt
}

fun ZonedDateTime.inclination() : Double {
    val t = convertToJ2000Century() + 1.0 // 1.0 because formula is based on 1900 AD
    return Dms(23, 27, 8).toDecimalDegrees() -
            (t * Dms(0,0, 47).toDecimalDegrees())
}

fun ZonedDateTime.RAMC(longitude: Double = 0.0): Double { // approximate longitude from timezone offset, Right ascension of the midheaven
    val ut: ZonedDateTime = withZoneSameInstant(ZoneId.of("GMT")) //
    val timeInDecimalHoursUTC: Double = ut.hour + ut.minute/60.0
    val d = ut.convertToJ2000Days().toInt()
    val st = 100.46 + (0.985647 * d) + longitude + (15.0 *  timeInDecimalHoursUTC)
    return st
}

fun ZonedDateTime.RAMC360(longitude: Double = 0.0) : Double {
   return angle360(RAMC(longitude))
}

fun ZonedDateTime.MC(longitude: Double = 0.0): Double {
    val ramc = RAMC360(longitude)
    return atand(tand(ramc)/cosd(inclination()))
}

fun ZonedDateTime.cusp(h: Int  = 0, longitude: Double = 0.0,  latitude: Double = 0.0): Double { // h is cusp in degrees from MC e.g. 10th house = 0, 11th house = 30
    val ramc = RAMC360(longitude)
    val e = inclination()
    val r = atand(sind(h.toDouble()) * tand(latitude) / cosd(ramc + h))
    val longitudeOfCusp = atand(cosd(r) * tand(ramc + h) / cosd(r + e))
    return longitudeOfCusp
}

fun ZonedDateTime.ASC(longitude: Double = 0.0, latitude: Double = 0.0): Double {
    val e = inclination()
    val ramc = RAMC360(longitude)
    val f = latitude
    val asc = acotd(-((tand(f) * sind(e))/(sind(ramc)*cosd(e))) / cosd(ramc))
    return asc
}
fun ZonedDateTime.ASC1(longitude: Double = 0.0, latitude: Double = 0.0): Double {
    val ramc = RAMC360(longitude)
    val f = latitude
    if (abs(90 - f) < verysmall) return 180.0 // near North Pole
    if (abs(90.0 + f) < verysmall) return 0.0 // near South Pole

    val quadrant: Int = kotlin.math.floor(ramc / 90.0).toInt()
    return angle360(when (quadrant) {
        0 -> Asc2(ramc, f)
        1 -> 180.0 - Asc2(180.0 - ramc, -f)
        2 -> 180.0 + Asc2(ramc - 180, -f)
        else -> 360.0 - Asc2(360.0 - ramc, f)
    })
}

fun ZonedDateTime.Asc2(ramc: Double, f: Double): Double {
    val e = inclination()
    val ass = ((- tand(f)) * sind(e)) + (cosd(e) * cosd(ramc))
    return atand(sind(ramc)/ass)
}

fun ZonedDateTime.EQA(longitude: Double = 0.0) : Double { // equatorial ascendant  [popularly, but incorrectly, called 'the east point']
    val e = inclination()
    val ramc = RAMC360(longitude)
    return  acotd(- (tand(ramc) * cosd(e)))
}

fun ZonedDateTime.VTX(longitude: Double = 0.0, latitude: Double = 0.0) : Double { // the vertex
    val e = inclination()
    val ramc = RAMC360(longitude)
    val f = latitude
    return acotd( - ((cotand(f) * sind(e)) - (sind(ramc) * cosd(e))) / cosd(ramc))
}

fun ZonedDateTime.CAS(longitude: Double = 0.0, latitude: Double = 0.0) : Double { // co-ascendant
    val e = inclination()
    val ramc = RAMC360(longitude)
    val f = latitude
    return acotd( - ((cotand(f) * sind(e)) + (sind(ramc) * cosd(e))) / cosd(ramc))
}

fun ZonedDateTime.PAS(longitude: Double = 0.0, latitude: Double = 0.0) : Double { // polar ascendant
    val e = inclination()
    val ramc = RAMC360(longitude)
    val f = latitude
    return acotd( ((tand(f) * sind(e)) - (sind(ramc) * cosd(e))) / cosd(ramc))
}

fun ZonedDateTime.declination(zodiacalLongitude: Double) : Double {
    val e = inclination()
    return asind(sind(zodiacalLongitude) * sind(e))
}

fun ZonedDateTime.placidusHouses(longitude: Double = 0.0, latitude: Double = 0.01) {
    val e = inclination()
    val f = latitude
    val ramc = RAMC360(longitude)
    val h11 = ramc + 30.0 // degrees
    val h12 = ramc + 60.0
    val h2 = ramc + 120.0
    val h3 = ramc + 150.0
    val f11 = 1.0/3.0
    val f12 = 2.0/3.0
    val f2 = 2.0/3.0
    val f3 = 1.0/3.0
    var d11 = asind(sind(e) * sind(h11))
    var d12 = asind(sind(e) * sind(h12))
    var d2 = asind(sind(e) * sind(h2))
    var d3 = asind(sind(e) * sind(h3))



    for(i in 0..3) {
        val a11 = f11 * (asind(tand(f) * tand(d11)))
        val a12 = f12 * (asind(tand(f) * tand(d12)))
        val a2 = f2 * (asind(tand(f) * tand(d2)))
        val a3 = f3 * (asind(tand(f) * tand(d3)))
        val m11 = atand(sind(a11) / (cosd(h11) * tand(d11)))
        val m12 = atand(sind(a12) / (cosd(h12) * tand(d12)))
        val m2 = atand(sind(a2) / (cosd(h2) * tand(d2)))
        val m3 = atand(sind(a3) / (cosd(h3) * tand(d3)))
        val r11 = atand((tand(h11) * cosd(m11)) / cosd(m11 + e))
        val r12 = atand((tand(h12) * cosd(m12)) / cosd(m12 + e))
        val r2 = atand((tand(h2) * cosd(m2)) / cosd(m2 + e))
        val r3 = atand((tand(h3) * cosd(m3)) / cosd(m3 + e))
        d11 = r11
        d12 = r12
        d2 = r2
        d3 = r3
    }
    val c10 = angle360(MC(longitude))
    val c11 = angle360(d11)
    val c12 = angle360(d12)
    val c2 = angle360(d2)
    val c3 = angle360(d3)
    val c4 = MC(longitude)
    val c5 = angle360(c11 + 180.0)
    val c6 = angle360(c12 + 180.0)
    val c8 = angle360(c2 + 180.0)
    val c9 = angle360(c3 + 180.0)
    val x = 0
}

fun ZonedDateTime.ascendant(longitude: Double = 0.0, latitude: Double = 0.0): Double {
    val ramc = RAMC360(longitude)
    val f = latitude
    val e = inclination()
    val y = -cosd(ramc)
    val x = (sind(ramc) * cosd(e)) / (tand(f) * sind(e))
    val asc0 = -atan2(x,y) * (180.0/Math.PI)
    val ascendant = if (asc0 < 180.0) asc0 + 180.0 else asc0 - 180
    return ascendant
}
fun ZonedDateTime.ascendantToZodiacIndex(longitude: Double = 0.0, latitude: Double = 0.0): Int {
    return (ascendant(longitude, latitude)/30.0).toInt()
}
fun ZonedDateTime.ascendantToZodiacSign(longitude: Double = 0.0, latitude: Double = 0.0): String {
    return zodiacNames[(ascendant(longitude, latitude)/30.0).toInt()]
}
fun ZonedDateTime.ascendantSignDegrees(longitude: Double = 0.0, latitude: Double = 0.0): Double {
    return ascendant(longitude, latitude) % 30
}

fun ZonedDateTime.longitudeHour(longitude: Double? = null): Double {
    return ((longitude?:(15.0 * this.offset.totalSeconds / 3600.0)) / 15.0) % 360
}

fun meanAnomaly(t: Double) = (0.9856 * t) - 3.289
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
    val lst = bday.RAMC()
    Log.d("natalDate", "")
}
