package fr.lehautcambara.astronomicon.ephemeris

import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.roundToInt
import cosd
import sind
import tand
import java.time.Duration
import java.time.ZonedDateTime

class LunarEphemeris : Ephemeris() {
    override fun eclipticCoords(dateTime: Calendar): Coords {
        return eclipticCoords(dateTime.convertToJulianCentury())
    }

    override fun eclipticCoords(julianCentury: Double): Coords {
        calculateOrbit(julianCentury)
        return Coords(xecl(), yecl(), zecl())
    }

    override fun equatorialCoords(dateTime: Calendar): Coords {
       return equatorialCoords(dateTime.convertToJulianCentury())
    }

    override fun equatorialCoords(julianCentury: Double): Coords {
        calculateOrbit(julianCentury)
        return Coords( xeq(), yeq(), zeq())
    }

    private var t:Double = 0.0
    private var beta0 = 0.0
    private var lambda0 = 0.0
    private val obliquityJ2000 = 23.43928


    private fun calculateOrbit(julianCentury: Double) {
        t = julianCentury
        beta0 = beta(t) - b(t) * sind(
            lambda(t) + c(t)
        )
        lambda0 = lambda(t) - a(t) + b(t) * cosd(
            lambda(t) + c(t)
        ) * tand(beta0)
    }

    private val NewMoonEpoch = GregorianCalendar(1900, Calendar.JANUARY, 1, 0, 0, 0)

    private fun lambda(t: Double): Double {
        return (218.32 + 481267.883 * t + 6.29 * sind(477198.85 * t + 134.9)
                - 1.27 * sind(413335.38 * t + 259.2)) + 0.66 * sind(890534.23 * t + 235.7) + 0.21 * sind(
            954397.70 * t + 269.9
        ) - 0.19 * sind(35999.05 * t + 357.5) - 0.11 * sind(966404.05 * t + 186.6)
    }
    private fun beta(t: Double): Double {
        return (5.13 * sind(483202.03 * t + 93.3)
                + 0.28 * sind(960400.87 * t + 228.2)) - 0.28 * sind(6003.18 * t + 318.3) - 0.17 * sind(
            407332.20 * t + 217.6
        )
    }
    private fun pi(t: Double): Double {
        return 0.9508 + 0.0518 * cosd(477198.85 * t + 134.9) + 0.0095 * cosd(413335.38 * t + 259.2) + 0.0078 * cosd(
            890534.23 * t + 235.7
        ) + 0.0028 * cosd(954397.70 * t + 269.9)
    }
    private fun poly(a0: Double, a1: Double, a2: Double, t: Double): Double {
        return a0 + a1 * t + a2 * t * t
    }

    private fun a(t: Double): Double {
        return poly(0.0, 1.396971, 0.0003086, t)
    }

    private fun b(t: Double): Double {
        return poly(0.0, 0.013056, 0.0000092, t)
    }

    private fun c(t: Double): Double {
        return poly(5.12362, -1.155358, -0.0001964, t)
    }

    private fun distance(t: Double): Double {
        return 6378.140 / sind(pi(t))
    }

    fun xeq(): Double = distance(t) * cosd(beta0) * cosd(lambda0)
    private fun ycommmon(obliquity: Double) =
        distance(t) * (cosd(beta0) * sind(lambda0) * cosd(obliquity) - sind(beta0) * sind(obliquityJ2000))
    private fun zcommon(obliquity: Double) =
        distance(t) * (cosd(beta0) * sind(lambda0) * sind(obliquity) + sind(beta0) * cosd(obliquityJ2000))

    fun yeq(): Double {
        return ycommmon(obliquityJ2000)
    }

    fun zeq(): Double {
        return zcommon(obliquityJ2000)
    }
    fun xecl(): Double {
        return xeq()
    }
    fun yecl(): Double {
        return ycommmon(0.0)
    }
    fun zecl(): Double {
        return zcommon(0.0)
    }
     private fun phaseFraction(dateTime: Calendar): Double {
        val lp = 2551443
        val phase: Double =
            ( ((dateTime.timeInMillis - NewMoonEpoch.timeInMillis) / 1000L) % lp).toDouble()
        return phase / lp
    }

     private fun phaseFraction(zonedDateTime: ZonedDateTime) : Double {
        val lp = 2551443
        val duration: Long = Duration.between(NewMoonEpoch.toZonedDateTime(), zonedDateTime).toMillis() / 1000
        val phase =  (duration % lp).toDouble()
         return phase / lp
    }
    fun phaseImageIndex(dateTime: Calendar, numImages: Int): Int {
        return (phaseFraction(dateTime) * numImages).roundToInt() % numImages
    }

    fun phaseImageIndex(zonedDateTime: ZonedDateTime, numImages: Int): Int {
        return (phaseFraction(zonedDateTime) * numImages).roundToInt() % numImages
    }

}