package fr.lehautcambara.astronomicon
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

fun sind(degrees: Double): Double {
    return sin(degrees * Math.PI / 180.0)
}

fun sind(degrees: Float): Float {
    return sin(degrees * Math.PI / 180.0).toFloat()
}


fun cosd(degrees: Double): Double {
    return cos(degrees * Math.PI / 180.0)
}

fun cosd(degrees: Float): Float {
    return cos(degrees * Math.PI / 180.0).toFloat()
}

fun rcosd(r: Float, angleD: Float) = r* cosd(angleD)
fun rcosd(r: Double, angleD: Double) = r* cosd(angleD)

fun rsind(r: Float, angleD: Float) = r* sind(angleD)
fun rsind(r: Double, angleD: Double) = r* sind(angleD)


fun tand(degrees: Double): Double {
    return tan(degrees * Math.PI / 180.0)
}

fun cotand(degrees: Double): Double {
    return 1.0/ tand(degrees)
}

fun atand(t: Double) : Double { // atan in degrees
    return atan(t) * 180.0 / Math.PI
}

fun acotd(t: Double) : Double {
    return atand(1.0/t)
}

fun asind(s: Double) : Double {
    return asin(s) * 180.0 / Math.PI
}

fun acosd(c: Double) : Double {
    return acos(c) * 180.0/Math.PI
}

fun angled(x: Double, y: Double): Double {
    return atan2(y, x) * 180.0 / Math.PI
}

fun elevationd(x: Double, y: Double, z: Double): Double {
    // [x y z] . [x y 0]/|[x y 0]||[x y z]|
    return (180.0/Math.PI) * atan2(z, sqrt(x*x + y*y))
}

fun r(x: Double, y: Double): Double {
    return sqrt(x * x + y * y)
}

fun angle360to180(a: Double) : Double {
    val angle = angle360(a)
    return if (angle > 180) angle - 360 else angle
}

fun angle360(a: Double) : Double {
    return ((a % 360) + 360) % 360
}

