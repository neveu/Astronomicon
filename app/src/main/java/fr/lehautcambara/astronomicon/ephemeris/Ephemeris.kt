package fr.lehautcambara.astronomicon.ephemeris

import angled
import cosd
import fr.lehautcambara.astronomicon.astrology.convertToJulianCentury
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import sind
import java.time.ZonedDateTime
import java.util.Calendar
import kotlin.math.IEEErem
import kotlin.math.abs
import kotlin.math.sqrt


data class Coords( val x: Double, val y: Double, val z: Double, val ephemeris: Ephemeris?=null) {
    override fun toString(): String {
        return "($x, $y, $z)"
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
    fun fromTo( to: Coords?): Coords {
        return if (to == null) -this
        else to - this
    }

    fun toPolar(): PolarCoords {
        return PolarCoords(sqrt(x*x + y*y), angled(x,y))
    }
}

data class PolarCoords (val r: Double, val a: Double) {}

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

    val body: String = keplerianElements.body
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

    override fun eclipticCoords(zdt: ZonedDateTime): Coords {
        return eclipticCoords( zdt.convertToJulianCentury())
    }

    override fun eclipticCoords(julianCentury: Double) : Coords{
        calculateOrbit(julianCentury)
        return Coords( xecl(), yecl(), zecl())
    }

    override fun equatorialCoords(dateTime: Calendar): Coords {
        return equatorialCoords(dateTime.convertToJulianCentury())
    }
    override fun equatorialCoords(julianCentury: Double): Coords {
        calculateOrbit(julianCentury)
        return Coords(xeq(), yeq(), zeq())
    }

    override fun toString(): String {
        return body
    }
}


abstract class Ephemeris {
    abstract fun eclipticCoords(dateTime: Calendar): Coords
    abstract fun eclipticCoords(zdt: ZonedDateTime): Coords

    abstract fun equatorialCoords(dateTime: Calendar): Coords


    abstract fun eclipticCoords(julianCentury: Double): Coords
    abstract fun equatorialCoords(julianCentury: Double): Coords
    abstract override fun toString(): String

    fun fromTo( to: Ephemeris?, currentDate: Double): Coords {
        return this.eclipticCoords(currentDate).fromTo(to?.eclipticCoords(currentDate))
    }


}