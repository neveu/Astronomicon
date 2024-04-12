package fr.lehautcambara.astronomicon.ephemeris

import cosd
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import sind
import java.time.Duration
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.IEEErem
import kotlin.math.abs
import kotlin.math.sqrt


data class Coords( val x: Double, val y: Double, val z: Double) {
    override fun toString(): String {
        return "($x, $y)"
    }

    operator fun unaryMinus(): Coords {
        return Coords(-x, -y, -z)
    }

    operator fun minus(eclipticCoords: Coords): Coords {
        return Coords(
            x - eclipticCoords.x,
            y - eclipticCoords.y,
            z - eclipticCoords.z
        )
    }

}
class SolarEphemeris(private val keplerianElements: KeplerianElements): Ephemeris() {
    private var a: Double = 0.0
    private var e: Double =  0.0
    private var I : Double = 0.0
    private var L : Double = 0.0
    private var wbar : Double = 0.0
    private var omega : Double = 0.0
    private var w : Double = 0.0
    private var M: Double = 0.0
    private var E: Double = 0.0
    private var t: Double = 0.0

    val body = keplerianElements.body
    private fun calculateOrbit(julianCenturies: Double) : Ephemeris {
        t = julianCenturies
        with(keplerianElements) {
            a = ft(a0, dadt, t)
            e = ft(e0, dedt, t)
            I = ft(I0, dIdt, t)
            L = ft(L0, dLdt, t)
            wbar = ft(wbar0, dwbardt, t)
            omega = ft(omega0, domegadt, t)
            w = wbar - omega
            // mean anomaly
            M = plusOrMinus180(L - wbar)
            val estar = 57.29578 * e
            val E0: Double = M + estar * sind(M)
            val tol = 1e-3
            var dE = 1.0
            var En = E0

            while (abs(dE) > tol) {
                val dM: Double = M - (En - estar * sind(En))
                dE = dM / (1 - e * cosd(En))
                En += dE
            }
            E = En
        }
        return this
    }
    private fun xp(): Double {
        return a * (cosd(E) - e)
    }

    private fun yp(): Double {
        return a * sqrt(1 - e * e) * sind(E)
    }

     private fun xecl(): Double {
        return ((cosd(w) * cosd(omega)
                    - sind(w) * sind(omega) * cosd(I)) * xp()
                + (-sind(w) * cosd(omega)
                    - cosd(w) * sind(omega) * cosd(I)) * yp())
    }

     private fun yecl(): Double {
        return ((cosd(w) * sind(omega)
                    + sind(w) * cosd(omega) * cosd(I)) * xp()
                + (-sind(w) * sind(omega)
                    + cosd(w) * cosd(omega) * cosd(I)) * yp())
    }

     private fun zecl(): Double {
        return (sind(w) * sind(I) * xp()) + (cosd(w) * sind(I) * yp())
    }

    private fun xeq(): Double {
        return xecl()
    }

    private fun yeq(): Double {
        return cosd(obliquityJ2000) * yecl() - sind(obliquityJ2000) * zecl()
    }

    private fun zeq(): Double {
        return sind(obliquityJ2000) * yecl() + cosd(obliquityJ2000) * zecl()
    }


    companion object {
        private val obliquityJ2000 = 23.43928
        fun ft(x0: Double, dxdt: Double, t: Double): Double = x0 + (t * dxdt)
        fun plusOrMinus180(v: Double): Double = v.IEEErem(360.0)

    }

    override fun eclipticCoords(dateTime: Calendar): Coords {
       return  eclipticCoords(dateTime.convertToJulianCentury())
    }

    override fun eclipticCoords(julianCentury: Double) : Coords{
        calculateOrbit(julianCentury)
        return Coords( xeq(), yeq(), zeq())
    }

    override fun equatorialCoords(dateTime: Calendar): Coords {
        return equatorialCoords(dateTime.convertToJulianCentury())
    }
    override fun equatorialCoords(julianCentury: Double): Coords {
        calculateOrbit(julianCentury)
        return Coords(xeq(), yeq(), zeq())
    }
}

fun Calendar.convertToJulianCentury(): Double {
    val j2000 = GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0)
    val Tmsec: Double = (this.timeInMillis - j2000.timeInMillis).toDouble()
    val Tsec = Tmsec / 1000.0
    val Tday = Tsec / (60 * 60 * 24)
    val Tyear = Tday / 365.242
    return Tyear / 100.0 // time in julian centuries
}

fun ZonedDateTime.convertToJulianCentury(): Double {
    val zoneID = ZoneId.ofOffset("UTC", ZoneOffset.UTC)
    val j2000 = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, zoneID)
    val now = this
    val duration = Duration.between(j2000, now)
    val period = Period.between(j2000.toLocalDate(), now.toLocalDate())
    val secs: Double = duration.toMillis() / 1000.0
    val days = secs /  (60 * 60 * 24)
    val years = days / 365.242
    return years/100.0
}

abstract class Ephemeris {
    abstract fun eclipticCoords(dateTime: Calendar): Coords

    abstract fun equatorialCoords(dateTime: Calendar): Coords


    abstract fun eclipticCoords(julianCentury: Double): Coords
    abstract fun equatorialCoords(julianCentury: Double): Coords
}